package com.example.m1kes.parkingdemo.adapters.recyclerviews;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.callbacks.OnPrintJobClicked;
import com.example.m1kes.parkingdemo.models.Printer;
import com.example.m1kes.parkingdemo.printer.models.PrinterJob;
import com.example.m1kes.parkingdemo.printer.tasks.GlobalPrint;

import java.util.List;
import static com.example.m1kes.parkingdemo.util.DBUtils.*;


public class PendingJobsAdapter extends RecyclerView.Adapter<PendingJobsAdapter.RecyclerViewHolder> {

    private List<PrinterJob> printerJobs;
    private Context context;
    private PendingJobsAdapter.RecyclerViewHolder viewHolder;
    private String format = "HH:mm";
    private OnPrintJobClicked onPrintJobClicked;

    public PendingJobsAdapter(Context context, List<PrinterJob> printerJobs,OnPrintJobClicked onPrintJobClicked) {
        this.printerJobs = printerJobs;
        this.context = context;
        this.onPrintJobClicked = onPrintJobClicked;
    }

    public PendingJobsAdapter(Context context, List<PrinterJob> printerJobs) {
        this.printerJobs = printerJobs;
        this.context = context;
    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.printer_job_row, parent, false);
        viewHolder = new RecyclerViewHolder(view, context);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        PrinterJob printerJob = printerJobs.get(position);
        if (printerJob != null) {
            holder.txtPrinterCode.setText(printerJob.getPrinterCode());
            holder.txtTimeOfPrint.setText(getDateFromDateTime(printerJob.getPrintAttemptTime(),format));
            holder.txtPrinterModel.setText(printerJob.getPrinterModel());
            holder.txtPrintType.setText(printerJob.getPrintType());
            if(printerJob.getPrintStatus().equalsIgnoreCase(PrinterJob.STATUS_PENDING)){
                holder.imgPrinterIcon.setImageResource(R.drawable.ic_printer_alert_black_24dp);
            }
        }
    }


    @Override
    public int getItemCount() {
        return printerJobs.size();
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView txtPrinterCode, txtTimeOfPrint,txtPrinterModel,txtPrintType;
        ImageView imgPrinterIcon;

        RecyclerViewHolder(View view, Context context) {
            super(view);
            txtPrinterCode = (TextView) view.findViewById(R.id.txtPrinterCode);
            txtTimeOfPrint = (TextView) view.findViewById(R.id.txtTimeOfPrint);
            txtPrinterModel = (TextView)view.findViewById(R.id.txtPrinterModel);
            txtPrintType = (TextView)view.findViewById(R.id.txtPrintType);

            imgPrinterIcon = (ImageView)view.findViewById(R.id.imgPrinterIcon);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if(onPrintJobClicked!=null) {
                       onPrintJobClicked.onClicked(printerJobs.get(getAdapterPosition()));
                       System.out.println(printerJobs.get(getAdapterPosition()));
                   }
                }
            });

        }






    }


}
