package com.example.m1kes.parkingdemo.modules.supervisor.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.JsonExportsRecyclerAdapter;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.SearchHistoryRecylerAdapter;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.decoration.DividerItemDecoration;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.onclicks.OnHistoryItemSelected;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.onclicks.OnJsonExportsItemSelected;
import com.example.m1kes.parkingdemo.models.SearchHistory;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.modules.supervisor.AdvancedMainActivity;
import com.example.m1kes.parkingdemo.modules.supervisor.models.JsonExports;
import com.example.m1kes.parkingdemo.modules.supervisor.util.TransactionDumper;
import com.example.m1kes.parkingdemo.sqlite.adapters.JsonExportsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.TransactionAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;
import com.example.m1kes.parkingdemo.util.GeneralUtils;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;
import com.example.m1kes.parkingdemo.util.StartupUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.nononsenseapps.filepicker.FilePickerActivity;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class DumpTransactions extends AppCompatActivity {

    private Context context;
    private Button btnBrowseExportFolder,btnOpenFolder,btnStartDump;
    private TextView txtFileExportDirectory;

    private static final int FILE_CODE = 303;
    //RecyclerView
    private RecyclerView jsonExportsRecycler;
    private RecyclerView.LayoutManager layoutManager;
    private JsonExportsRecyclerAdapter adapter;
    private List<JsonExports> jsonExports;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dump_transactions);
        StartupUtils.setupActionBar("Dump Transactions",this);
        context = DumpTransactions.this;
        initGui();
        setupOnClicks();
        setupDefaults();
        setupRecyclerView(JsonExportsAdapter.getAllExportedFiles(context));

    }

    private void initGui(){

        jsonExportsRecycler = (RecyclerView)findViewById(R.id.jsonExportsRecycler);
        btnBrowseExportFolder = (Button)findViewById(R.id.btnBrowseExportFolder);
        btnOpenFolder = (Button)findViewById(R.id.btnOpenFolder);
        btnStartDump = (Button)findViewById(R.id.btnStartDump);
        txtFileExportDirectory = (TextView)findViewById(R.id.txtFileExportDirectory);

    }


    private void setupRecyclerView(List<JsonExports> jsonExports){

        this.jsonExports = jsonExports;
        adapter = new JsonExportsRecyclerAdapter(context,jsonExports,onJsonExportsItemSelected);
        layoutManager = new LinearLayoutManager(context);
        jsonExportsRecycler.setLayoutManager(layoutManager);
        jsonExportsRecycler.setHasFixedSize(true);
        jsonExportsRecycler.setAdapter(adapter);
        jsonExportsRecycler.addItemDecoration(new DividerItemDecoration(context));
        adapter.notifyDataSetChanged();
    }

    OnJsonExportsItemSelected onJsonExportsItemSelected = new OnJsonExportsItemSelected() {
        @Override
        public void onClick(JsonExports jsonExport) {
            showJsonPreviewDialog(jsonExport);
        }
    };


    private void showJsonPreviewDialog(JsonExports jsonExport){

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        // LayoutInflater inflater = context.getLayoutInflater();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_json_exported_preview, null);
        TextView txtJsonPreviewInfo = (TextView)view.findViewById(R.id.txtJsonPreviewInfo);
        TextView txtJsonExportPreviewStats = (TextView)view.findViewById(R.id.txtJsonExportPreviewStats);

        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader(jsonExport.getFilePath()));
            List<Transaction> transactions = gson.fromJson(reader, new TypeToken<List<Transaction>>() {}.getType());

            if(transactions.isEmpty()){
                txtJsonPreviewInfo.setText("No Transactions");
            }else{

                txtJsonPreviewInfo.setText(transactions.toString());
            }

            int numtransactions = transactions.size();
            String time = GeneralUtils.formatRawLongTime(jsonExport.getCreationDateTime());
            String str  ="Total : "+numtransactions+" Creation Time: "+time;
            txtJsonExportPreviewStats.setText(str);



        } catch (FileNotFoundException e) {
            e.printStackTrace();

            //TODO MAY USE THIS FOR SINGLE READING JSON FILES
            /*try {
                JsonReader reader = new JsonReader(new FileReader(jsonExport.getFilePath()));
                Transaction transaction = gson.fromJson(reader, new TypeToken<Transaction>() {}.getType());

                if(transaction!=null){
                    txtJsonPreviewInfo.setText("No Transactions");
                }else{
                    txtJsonPreviewInfo.setText(transaction.toString());
                }

                int numtransactions = 1;
                String time = GeneralUtils.formatRawLongTime(jsonExport.getCreationDateTime());
                String str  ="Total : "+numtransactions+" Creation Time: "+time;
                txtJsonExportPreviewStats.setText(str);


            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            */
        }


        builder.setView(view);
        //Create the Alert Dialog and Return it
        builder.setCancelable(true);
        final android.app.AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme; //style id
        dialog.show();

    }



    private void setupDefaults(){

        String exportDIR = TransactionDumper.getExportDirectory(context);
        if(exportDIR!=null&&exportDIR.length()>0)
            txtFileExportDirectory.setText(exportDIR);
        else
            txtFileExportDirectory.setText("Please Choose a folder");
    }

    private void setupOnClicks(){

        btnBrowseExportFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFolderPicker(context);
            }
        });

        btnOpenFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFolder();
            }
        });

        btnStartDump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                showExportOptions(context);


            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(getApplicationContext(), AdvancedMainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void openFolder()
    {

        String directory = TransactionDumper.getExportDirectory(context);

        System.out.println("Trying to Open Directory: "+directory);

        File file = new File(directory);
        Uri startDir = Uri.fromFile(file);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(file.getAbsolutePath()); // a directory
        intent.setDataAndType(uri, "*/*");
        startActivity(Intent.createChooser(intent, "Open folder"));

    }


    public void startFolderPicker(Context context){

        // This always works
        Intent i = new Intent(context, FilePickerActivity.class);
        // This works if you defined the intent filter
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);

        // Configure initial directory by specifying a String.
        // You could specify a String like "/storage/emulated/0/", but that can
        // dangerous. Always use Android's API calls to get paths to the SD-card or
        // internal memory.
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, FILE_CODE);

    }

    public void showExportOptions(final Context context){

        final CharSequence[] options = { "Backup To Server","Backup Locally" };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Export Options");
        builder.setCancelable(true);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Backup To Server"))
                {
                    Toast.makeText(context,"Work in Progress...", Toast.LENGTH_SHORT).show();

                }
                else if (options[item].equals("Backup Locally"))
                {

                    String username  = UserAdapter.getLoggedInUser(context);
                    String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
                    List<Transaction> transactions = TransactionAdapter.getAllTransactionsForUserInShift(username,shiftID,
                            context);
                    TransactionDumper dumper = new TransactionDumper(transactions,context);
                    //Only KitKat and above can use try with resources
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        //Returns true if successfull
                        dumper.writeTransactionsToJsonFile();
                    }
                    setupRecyclerView(JsonExportsAdapter.getAllExportedFiles(context));
                }
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            // Do something with the URI
                            TransactionDumper.setNewExportDirectory(context,uri.getPath()+File.separator);
                            txtFileExportDirectory.setText(TransactionDumper.getExportDirectory(context));
                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path: paths) {
                            Uri uri = Uri.parse(path);
                            // Do something with the URI
                            TransactionDumper.setNewExportDirectory(context,uri.getPath()+File.separator);
                            txtFileExportDirectory.setText(TransactionDumper.getExportDirectory(context));
                        }
                    }
                }

            } else {
                Uri uri = data.getData();
                // Do something with the URI
                TransactionDumper.setNewExportDirectory(context,uri.getPath()+File.separator);
                txtFileExportDirectory.setText(TransactionDumper.getExportDirectory(context));
            }
        }
    }



}
