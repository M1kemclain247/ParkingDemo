package com.example.m1kes.parkingdemo.printer.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;


import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.printer.PublicAction;
import com.example.m1kes.parkingdemo.printer.models.PayFineReceipt;

import HPRTAndroidSDK.HPRTPrinterHelper;


public class PrintReceiptTask extends AsyncTask<PayFineReceipt,Void,Boolean> {


    private Context context;
    private TextView txtPrinterStatus;
    private ProgressDialog dialog;
    private PublicAction PAct;
    private Handler mHandler;


    public PrintReceiptTask(Context context, TextView txtPrinterStatus, PublicAction PAct){
        this.context = context;
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        this.txtPrinterStatus = txtPrinterStatus;
        this.PAct = PAct;
        mHandler = new Handler();
    }



    @Override
    protected void onPreExecute() {
        dialog.setMessage("Printing...");
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(PayFineReceipt... payFineReceipts) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        PayFineReceipt receipt =  payFineReceipts[0];

        if(receipt!=null){

            try
            {
                PAct.LanguageEncode();
                PAct.BeforePrintAction();

                //Prints out entire single String may need to be changed here
                HPRTPrinterHelper.PrintText(receipt.getReceipt());
                PAct.AfterPrintAction();

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setMessage("Printing Complete!");
                    }
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        txtPrinterStatus.setText("Print Complete!");
                        txtPrinterStatus.setTextColor(context.getResources().getColor(R.color.green));
                        dialog.dismiss();
                    }
                });


            }
            catch(Exception e)
            {
                Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
                e.printStackTrace();

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        txtPrinterStatus.setText("Print Failed!");
                        txtPrinterStatus.setTextColor(context.getResources().getColor(R.color.red));
                        dialog.dismiss();
                    }
                });
            }
        }


        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if(dialog.isShowing()){
            dialog.dismiss();
        }
        super.onPostExecute(aBoolean);
    }
}
