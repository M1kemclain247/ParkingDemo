package com.example.m1kes.parkingdemo.modules.supervisor.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.modules.supervisor.exceptions.TransactionConversionException;
import com.example.m1kes.parkingdemo.modules.supervisor.exceptions.TransactionException;
import com.example.m1kes.parkingdemo.modules.supervisor.models.JsonExports;
import com.example.m1kes.parkingdemo.sqlite.adapters.JsonExportsAdapter;
import com.example.m1kes.parkingdemo.util.GeneralUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.m1kes.parkingdemo.util.DBUtils.getDateParts;

public class TransactionDumper {


    private List<Transaction> transactions;
    private Context context;
    private Transaction transaction;


    public static final String FILE_PATH = Environment.getExternalStorageDirectory() +
            File.separator + "Marshall" + File.separator + "Dumps" + File.separator;

    public static final String DAILY_FILE_PATH = Environment.getExternalStorageDirectory()+
            File.separator+"Marshall"+File.separator + "Dumps"+File.separator+
            "Daily"+File.separator+getDateTimeFormatForFolderCaching()+File.separator;


    private static final String DUMP_SETTINGS_PREF = "DumpSettings";

    public TransactionDumper(List<Transaction> transactions,Context context){
        this.transactions = transactions;
        this.context = context;
    }

    public TransactionDumper(Transaction transaction,Context context){
        this.context = context;
        this.transaction = transaction;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean writeTransactionsToJsonFile(){

        boolean isSuccessfull = false;

        //Convert the list of Transactions to a JsonArray

        String filename = "Transactions-Dump-" + GeneralUtils.getCurrentDateTimeFormatted() + ".txt";

        Gson gson = new Gson();
        JsonElement element = gson.toJsonTree(transactions, new TypeToken<List<Transaction>>() {}.getType());

        try {

            if (!element.isJsonArray()) {
                // fail appropriately
                throw new TransactionConversionException("Failed to Convert Transactions To Json");
            }

            JsonArray jsonArray = element.getAsJsonArray();

            Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(jsonArray.toString());
            String prettyJsonString = gsonBuilder.toJson(je);

            //Write them to a file
            File targetFile = new File(getExportDirectory(context) + filename);
            File parent = targetFile.getParentFile();

            System.out.println("File save path: "+targetFile.getAbsolutePath());

            try {
                if (!parent.exists() && !parent.mkdirs()) {
                    throw new IllegalStateException("Couldn't create dir: " + parent);
                }
                try {

                    try (PrintWriter pw = new PrintWriter(new FileWriter(targetFile, true))) {
                        pw.println(prettyJsonString);
                        System.out.println("Successfully logged Transactions for this shift to File in Json");
                        Toast.makeText(context, "Successfully Backed Up", Toast.LENGTH_SHORT).show();
                        isSuccessfull = true;

                        long creationTime = Long.valueOf(GeneralUtils.getCurrentDateTime());
                        JsonExportsAdapter.addJsonExport(new JsonExports(targetFile.getAbsolutePath(),creationTime),context);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IllegalStateException ex) {
                ex.printStackTrace();
                Toast.makeText(context, "Failed to Log Transactions", Toast.LENGTH_SHORT).show();
            }

        }catch (TransactionException ex){
            ex.printStackTrace();
            System.out.println("Error in converting Transactions to Json");
            Toast.makeText(context, "Failed to Log Transactions", Toast.LENGTH_SHORT).show();
        }
        return isSuccessfull;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean writeTransactionsDaily(){

        boolean isSuccessfull = false;

        //Convert the list of Transactions to a JsonArray

        String filename = "Dump -" + getDateTimeFormatForFileName() + ".txt";

        Gson gson = new Gson();
        String jsonString = gson.toJson(transaction, new TypeToken<Transaction>() {}.getType());

        try {

            //Exception will be thrown if it fails to turn the transaction into a json file form the produced json string
            JSONObject jsonObject = new JSONObject(jsonString);

            Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(jsonObject.toString());
            String prettyJsonString = gsonBuilder.toJson(je);
            //Write them to a file
            File targetFile = new File(DAILY_FILE_PATH + filename);
            File parent = targetFile.getParentFile();

            System.out.println("File save path: " + targetFile.getAbsolutePath());

            try {
                if (!parent.exists() && !parent.mkdirs()) {
                    throw new IllegalStateException("Couldn't create dir: " + parent);
                }
                try {

                    try (PrintWriter pw = new PrintWriter(new FileWriter(targetFile, true))) {
                        pw.println(prettyJsonString);
                        System.out.println("Successfully logged Transactions for this shift to File in Json");
                        Toast.makeText(context, "Successfully Backed Up", Toast.LENGTH_SHORT).show();
                        isSuccessfull = true;

                        long creationTime = Long.valueOf(GeneralUtils.getCurrentDateTime());


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IllegalStateException ex) {
                ex.printStackTrace();
                Toast.makeText(context, "Failed to Log Transactions", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error in converting Transaction to Json");
            Toast.makeText(context, "Failed to Log Transaction", Toast.LENGTH_SHORT).show();
        }
        return isSuccessfull;
    }


    public static boolean setNewExportDirectory(Context context, String exportPath) {

        boolean successful = false;

        System.out.println("New File Path being set: "+exportPath);
            SharedPreferences.Editor editor = context.getSharedPreferences(DUMP_SETTINGS_PREF, MODE_PRIVATE).edit();
            editor.putString("FILE_EXPORT_PATH", exportPath);
            editor.apply();
            System.out.println("Successfully set new Path Export Folder!");


        return successful;
    }

    public static String getExportDirectory(Context context){

        SharedPreferences prefs = context.getSharedPreferences(DUMP_SETTINGS_PREF, MODE_PRIVATE);
        String export_path = prefs.getString("FILE_EXPORT_PATH", FILE_PATH);
        if (!export_path.equals(FILE_PATH)) {
            return export_path;
        }else{
            //Return the default file saving path if couldnt get a new one
            return FILE_PATH;
        }

    }


    public static String getDateTimeFormatForFolderCaching(){

        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        String unformatted = sf.format(calendar.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        String[] parts= getDateParts(unformatted);
        cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])-1, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
        Date date = cal.getTime();
        return formatter.format(date);
    }

    public static String getDateTimeFormatForFileName(){

        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        String unformatted = sf.format(calendar.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
        Calendar cal = Calendar.getInstance();
        String[] parts= getDateParts(unformatted);
        cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])-1, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
        Date date = cal.getTime();
        return formatter.format(date);
    }



}
