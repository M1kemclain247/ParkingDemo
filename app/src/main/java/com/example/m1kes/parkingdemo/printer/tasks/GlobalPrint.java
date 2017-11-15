package com.example.m1kes.parkingdemo.printer.tasks;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.callbacks.OnPrintResponse;
import com.example.m1kes.parkingdemo.callbacks.OnPrinterConnected;
import com.example.m1kes.parkingdemo.callbacks.VehicleFoundResponse;
import com.example.m1kes.parkingdemo.models.Printer;
import com.example.m1kes.parkingdemo.printer.PublicAction;
import com.example.m1kes.parkingdemo.printer.models.PrinterJob;
import com.example.m1kes.parkingdemo.printer.models.Receipt;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrintJobAdapter;
import com.example.m1kes.parkingdemo.util.GeneralUtils;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import HPRTAndroidSDK.HPRTPrinterHelper;
import HPRTAndroidSDK.PublicFunction;


public class GlobalPrint extends AsyncTask<PrinterJob,Void,Boolean> {

    private ProgressDialog dialog;
    private Context context;
    private Handler mHandler;
    private static HPRTPrinterHelper HPRTPrinter = new HPRTPrinterHelper();
    private PublicFunction PFun=null;
    private PublicAction PAct=null;
    private Object t;
    private OnPrintResponse response;
    private Printer printer;
    private PrinterJob printerJob;
    private Connection printerConnection = null;


    public <T> GlobalPrint(Context context, OnPrintResponse response, T t, Printer printer){
        this.context = context;
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        mHandler = new Handler();
        PFun=new PublicFunction(context);
        PAct=new PublicAction(context);
        this.t = t;
        this.response = response;
        this.printer = printer;
    }

    public <T> GlobalPrint(Context context, OnPrintResponse response,PrinterJob printerJob){
        this.context = context;
        mHandler = new Handler();
        PFun=new PublicFunction(context);
        PAct=new PublicAction(context);
        this.printerJob = printerJob;
        this.response = response;
        this.printer = GeneralUtils.generatePrinter(printerJob);
    }





    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage("Printing Reciept....");
        dialog.show();
    }


    @Override
    protected Boolean doInBackground(PrinterJob... params) {

        Boolean printed = false;

        if(t!=null) {
            if (t instanceof Receipt) {
                Receipt receipt = (Receipt) t;
                //Create Print job we need shiftI
                printed = printReciepts(receipt);
            }
        }else{
            if(printerJob.getPrinterModel().contains("MZ220")){
                //TODO Add code for printing to the printer
               printed =  printRecieptsWithZebra(printerJob);
            }else{
                printed = printReciepts(printerJob);
            }
        }

        return printed;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean printed) {

        dialog.dismiss();

        if(printed){
            response.onPrintComplete();
        }else{
            response.onPrintFailed();
        }

    }

    private boolean printRecieptsWithZebra(PrinterJob printerJob)
    {

        BluetoothAdapter.getDefaultAdapter().enable();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        boolean isSuccessfull = false;

        printerJob.setPrinterCode(printer.getPrinterCode());
        printerJob.setPrinterModel(printer.getPrinterModel());
        printerJob.setPrinterIMEI(printer.getPrinterIMEI());

        System.out.println("Printing PrintJob: "+printerJob);
        System.out.println("PrintJob data: "+printerJob.getPrintData());

        try
        {
            //Initializing the connection to the printer

            printerConnection = new BluetoothConnection(printerJob.getPrinterIMEI());
            printerConnection.close();
            printerConnection.open();
            ZebraPrinter printer = null;

            if (printerConnection.isConnected()) {
                printer = ZebraPrinterFactory.getInstance(printerConnection);
            }

            if (printer != null) {
                PrinterLanguage pl = printer.getPrinterControlLanguage();
                if (pl == PrinterLanguage.CPCL) {
                    sendTestLabel();
                }

                printerConnection.close();
                isSuccessfull = true;
                printerJob.setPrintStatus(PrinterJob.STATUS_COMPLETE);
                PrintJobAdapter.updatePrintStatus(printerJob,context);

            }

        }
        catch(Exception e)
        {
            Log.e("ZebraConnectivity", (new StringBuilder("Global Print--> PrintSampleReceipt ")).append(e.getMessage()).toString());
            e.printStackTrace();
            //Need to update status to print complete
            printerJob.setPrintStatus(PrinterJob.STATUS_PENDING);
            PrintJobAdapter.updatePrintStatus(printerJob,context);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,"Failed to connect to Zebra Printer!",Toast.LENGTH_SHORT).show();

                }
            });


        }
        return isSuccessfull;
    }


    private void sendTestLabel() {
        try {



            byte[] configLabel = printerJob.getPrintData().getBytes();
            printerConnection.write(configLabel);

           /* String FILE_PATH = Environment.getExternalStorageDirectory() +
                    File.separator + "Marshall"+ File.separator;
            String filename = "app_login_logo_3.png";
            InputStream is = new FileInputStream(FILE_PATH+filename);
*/

           // printerConnection.write(is);
        } catch (ConnectionException e) {
            System.out.println("in send Test: "+ e);
        }
    }


    private boolean printReciepts(Receipt receipt)
    {
        boolean isSuccessfull = false;

        PrinterJob printerJob = new PrinterJob();
        printerJob.setPrintAttemptTime(new Date());
        printerJob.setPrinterCode(printer.getPrinterCode());
        printerJob.setPrinterModel(printer.getPrinterModel());
        printerJob.setPrinterIMEI(printer.getPrinterIMEI());

        try
        {
            PAct.LanguageEncode();
            PAct.BeforePrintAction();

                HPRTPrinterHelper.PrintText(receipt.getPrintString());

            isSuccessfull = PAct.AfterPrintAction();

            //Need to update status to print complete
            printerJob.setPrintStatus(PrinterJob.STATUS_COMPLETE);
            PrintJobAdapter.updatePrintStatus(printerJob,context);
            isSuccessfull = true;
        }
        catch(Exception e)
        {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
            e.printStackTrace();
            //Need to update status to print complete
            printerJob.setPrintStatus(PrinterJob.STATUS_PENDING);
            PrintJobAdapter.updatePrintStatus(printerJob,context);
        }
        return isSuccessfull;
    }

    private boolean printReciepts(PrinterJob printerJob)
    {

        boolean isSuccessfull = false;

        printerJob.setPrinterCode(printer.getPrinterCode());
        printerJob.setPrinterModel(printer.getPrinterModel());
        printerJob.setPrinterIMEI(printer.getPrinterIMEI());

        System.out.println("Printing PrintJob: "+printerJob);
        System.out.println("PrintJob data: "+printerJob.getPrintData());

        try
        {
            PAct.LanguageEncode();
            PAct.BeforePrintAction();

            HPRTPrinterHelper.PrintText(printerJob.getPrintData());

            //printingImages();

            isSuccessfull = PAct.AfterPrintAction();

            //Need to update status to print complete
            printerJob.setPrintStatus(PrinterJob.STATUS_COMPLETE);
            PrintJobAdapter.updatePrintStatus(printerJob,context);
            isSuccessfull = true;
        }
        catch(Exception e)
        {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
            e.printStackTrace();
            //Need to update status to print complete
            printerJob.setPrintStatus(PrinterJob.STATUS_PENDING);
            PrintJobAdapter.updatePrintStatus(printerJob,context);
        }
        return isSuccessfull;
    }

    private void printingImages(){

            Bitmap bmp = null;
            String FILE_PATH = Environment.getExternalStorageDirectory() +
                    File.separator + "Marshall"+ File.separator;
            String filename = "app_login_logo_3.png";
            File file = new File(FILE_PATH,filename);
            bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            Bitmap invertedBmp =  doInvert(bmp);
            HPRTPrinterHelper.PrintBitmap(invertedBmp,(byte)0,(byte)0);

    }

    public static Bitmap invertImage(Bitmap bitmap) {

        int length = bitmap.getWidth()*bitmap.getHeight();
        int[] array = new int[length];
        bitmap.getPixels(array,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        for (int i=0;i<length;i++){
            if (array[i] == 0xff000000){
                array[i] = 0xffffffff;
            }
        }
        bitmap.setPixels(array,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());

        return bitmap;
    }


    public static Bitmap doInvert(Bitmap src) {
        // create new bitmap with the same settings as source bitmap
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        // color info
        int A, R, G, B;
        int pixelColor;
        // image size
        int height = src.getHeight();
        int width = src.getWidth();

        // scan through every pixel
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                // get one pixel
                pixelColor = src.getPixel(x, y);
                // saving alpha channel
                A = Color.alpha(pixelColor);
                // inverting byte for each R/G/B channel
                R = 255 - Color.red(pixelColor);
                G = 255 - Color.green(pixelColor);
                B = 255 - Color.blue(pixelColor);
                // set newly-inverted pixel to output image
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final bitmap
        return bmOut;
    }


}
