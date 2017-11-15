package com.example.m1kes.parkingdemo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.callbacks.OnPrintJobClicked;
import com.example.m1kes.parkingdemo.callbacks.OnPrintResponse;
import com.example.m1kes.parkingdemo.fragments.printjobs.CompletePrints;
import com.example.m1kes.parkingdemo.fragments.printjobs.PendingPrints;
import com.example.m1kes.parkingdemo.models.Printer;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.printer.PrinterDevices;
import com.example.m1kes.parkingdemo.printer.models.PrinterJob;
import com.example.m1kes.parkingdemo.printer.tasks.GlobalPrint;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrinterAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;
import com.example.m1kes.parkingdemo.transactions.MakeFineTransaction;
import com.example.m1kes.parkingdemo.transactions.MakePaymentTransaction;
import com.example.m1kes.parkingdemo.util.GeneralUtils;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;
import com.example.m1kes.parkingdemo.util.StartupUtils;

import java.io.IOException;
import java.util.List;

import static com.example.m1kes.parkingdemo.util.ObjectParser.bytes2Object;


public class ViewPrinterJobs extends AppCompatActivity {

    private Context context;
    private FragmentPagerAdapter adapterViewPager;
    private LinearLayout rootPairedPrinter;
    private TextView txtPairedPrinterDepCode,txtPairedPrinterModel,txtPairedPrinterIMEI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_printer_jobs);
        context = ViewPrinterJobs.this;
        StartupUtils.setupActionBar("Printer Manager",this);




        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        rootPairedPrinter = (LinearLayout)findViewById(R.id.rootPairedPrinter);
        txtPairedPrinterDepCode = (TextView)findViewById(R.id.txtPairedPrinterDepCode);
        txtPairedPrinterModel = (TextView)findViewById(R.id.txtPairedPrinterModel);
        txtPairedPrinterIMEI = (TextView)findViewById(R.id.txtPairedPrinterIMEI);


        List<Printer> printers = PrinterAdapter.getPairedPrinters(context);
        if(!printers.isEmpty()){
            Printer printer =  printers.get(0);

            String depCode =  printer.getPrinterCode();
            String model = printer.getPrinterModel();
            String IMEI = printer.getPrinterIMEI();

            txtPairedPrinterDepCode.setText(depCode);
            txtPairedPrinterModel.setText(model);
            txtPairedPrinterIMEI.setText(IMEI);
        }







        rootPairedPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptions();
            }
        });


        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        final PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_header);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(Color.RED);

        vpPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position==1){
                    pagerTabStrip.setTabIndicatorColor(Color.GREEN);
                }else if(position ==0){
                    pagerTabStrip.setTabIndicatorColor(Color.RED);
                }

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }


    private void showOptions(){



        if(txtPairedPrinterDepCode.getText().toString().contains("None")){

            final CharSequence[] options = { "Pair new Printer" };
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.drawable.ic_coin_black_24dp);
            builder.setTitle("Options");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals("Pair new Printer")) {
                        dialog.dismiss();
                        String username = UserAdapter.getLoggedInUser(context);
                        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
                        Intent i = new Intent(context,RegisterAPrinter.class);
                        if(shiftID!=null){
                            //Continue active shift
                            i.putExtra("ActiveShift",true);
                        }else{
                            i.putExtra("ActiveShift",false);
                        }
                        i.putExtra("LoggedIn",username);
                        startActivity(i);
                    }
                }

            });
            builder.show();

        }else{
            final CharSequence[] options = { "Unpair Printer" };
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.drawable.ic_coin_black_24dp);
            builder.setTitle("Options");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals("Unpair Printer")) {
                        dialog.dismiss();
                        Toast.makeText(context,"Printer Unpaired",Toast.LENGTH_SHORT).show();
                        PrinterAdapter.deleteAll(context);

                        String username = UserAdapter.getLoggedInUser(context);
                        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);

                        Intent i = new Intent(context,RegisterAPrinter.class);
                        if(shiftID!=null){
                            //Continue active shift
                            i.putExtra("ActiveShift",true);
                        }else{
                            i.putExtra("ActiveShift",false);
                        }
                        startActivity(i);
                    }
                }

            });
            builder.show();
        }







    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (resultCode) {
                case PrinterDevices.BLUETOOTH_CONNECTED:
                    String printerName = null;
                    String printerAddress = null;
                    printerName = data.getExtras().getString("model");
                    printerAddress = data.getExtras().getString("IMEI");
                    Bundle b = data.getExtras();
                    PrinterJob p = null;
                    if (b!=null) {
                        try {
                            p = (PrinterJob) bytes2Object(b.getByteArray("printerJob"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                        }
                    }
                    if(printerName!=null&&printerAddress!=null&&p!=null){
                        //Successfully connected lets print
                        new GlobalPrint(context,onPrintResponse,p).execute();
                    }else{
                        //Something went wrong with getting results

                    }
                    break;
                default:

            }

        } catch (Exception e) {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> onActivityResult ")).append(e.getMessage()).toString());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    OnPrintResponse onPrintResponse = new OnPrintResponse() {
        @Override
        public void onPrintComplete() {
            Toast.makeText(context,"Successfully Printed!",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPrintFailed() {
            Toast.makeText(context,"Print Failed",Toast.LENGTH_SHORT).show();
        }
    };



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }

        return false;
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {

        String []titles = new String[]{"Pending","Complete"};

        private int NUM_ITEMS = 2;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return PendingPrints.newInstance(0, titles[position]);
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return CompletePrints.newInstance(1, titles[position]);
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {

            SpannableStringBuilder sb = new SpannableStringBuilder("  " + titles[position]); // space added before text for convenience
            Drawable drawable = null;
            if(position ==0){
                drawable = context.getResources().getDrawable( R.drawable.ic_printer_alert_white_24dp );
            }else if(position==1){
                drawable = context.getResources().getDrawable( R.drawable.ic_printer_white_24dp );
            }


            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


            return sb;
        }

    }



}
