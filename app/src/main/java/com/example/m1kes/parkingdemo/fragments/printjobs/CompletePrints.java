package com.example.m1kes.parkingdemo.fragments.printjobs;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.PendingJobsAdapter;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.decoration.DividerItemDecoration;
import com.example.m1kes.parkingdemo.callbacks.OnPrintJobClicked;
import com.example.m1kes.parkingdemo.models.Printer;
import com.example.m1kes.parkingdemo.printer.PrinterDevices;
import com.example.m1kes.parkingdemo.printer.models.PrinterJob;
import com.example.m1kes.parkingdemo.printer.models.Receipt;
import com.example.m1kes.parkingdemo.printer.models.ReprintReceipt;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrintJobAdapter;
import com.example.m1kes.parkingdemo.util.ObjectParser;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.example.m1kes.parkingdemo.util.GeneralUtils.generatePrinter;


public class CompletePrints extends Fragment {

    // Store instance variables
    private String title;
    private int page;

    RecyclerView recyclerView;
    PendingJobsAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    List<PrinterJob> currentList;
    private Context context;

    public CompletePrints() {
        // Required empty public constructor
    }

    // newInstance constructor for creating fragment with arguments
    public static CompletePrints newInstance(int page, String title) {
        CompletePrints fragmentFirst = new CompletePrints();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        context = getContext();
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complete_prints, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerPrintJobsComplete);
        setupRecyclerView(PrintJobAdapter.getAllPrintJobs(PrinterJob.STATUS_COMPLETE,context));
        return view;
    }

    OnPrintJobClicked onPrintJobClicked = new OnPrintJobClicked() {
        @Override
        public void onClicked(PrinterJob printerJob) {
            showOptions(printerJob);
        }
    };

    private void setupRecyclerView(List<PrinterJob> jobs){
        adapter = new PendingJobsAdapter(getContext(),jobs,onPrintJobClicked);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(context));
        currentList = jobs;
    }

    public void showOptions(final PrinterJob printerJob){



            final CharSequence[] options = {"Retry Print"};
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.drawable.ic_printer_black_24dp);
            builder.setTitle("Options");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals("Retry Print")) {
                        Receipt receipt = new ReprintReceipt(printerJob);

                        PrinterJob printerJob1 = new PrinterJob();
                        printerJob1.setPrintData(receipt.getPrintData());
                        printerJob1.setPrintType(receipt.getPrintType());
                        printerJob1.setPrintAttemptTime(new Date());

                        PrintJobAdapter.deletePrintJob(printerJob,context);
                        System.out.println("PrintJob to be Updated!:" + printerJob1);
                        //Show bluetooth devices and follow normal standard to print.
                        Intent i = new Intent(context, PrinterDevices.class);
                        Bundle b = new Bundle();
                        try {
                            b.putByteArray("printerJob", ObjectParser.object2Bytes(printerJob1));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        i.putExtras(b);
                        startActivity(i);
                        dialog.dismiss();

                    }
                }

            });

            builder.show();
    }

}
