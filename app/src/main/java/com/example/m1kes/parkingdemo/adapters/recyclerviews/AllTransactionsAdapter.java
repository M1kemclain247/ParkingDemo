package com.example.m1kes.parkingdemo.adapters.recyclerviews;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.onclicks.OnTransactionSelected;
import com.example.m1kes.parkingdemo.models.Transaction;

import java.util.List;

public class AllTransactionsAdapter extends RecyclerView.Adapter<AllTransactionsAdapter.RecyclerViewHolder> {

    private List<Transaction> transactions;
    private Context context;
    private RecyclerViewHolder viewHolder;
    private OnTransactionSelected clickCallback;

    public AllTransactionsAdapter(Context context,List<Transaction> transactions,OnTransactionSelected clickCallback){
        this.context = context;
        this.transactions = transactions;
        this.clickCallback = clickCallback;
    }



    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item_row,parent,false);
        viewHolder = new RecyclerViewHolder(view,context);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        Transaction transaction = transactions.get(position);


        if(transaction!=null){

            if(!transaction.is_synced()){
               // holder.rootTransView.setBackgroundColor(context.getResources().getColor(R.color.red));
              //  holder.rootHeaderColor.setBackgroundColor(context.getResources().getColor(R.color.red));
                holder.syncedStateTransaction.setImageResource(R.drawable.ic_transaction_unsynced);
            }else{
                //holder.rootTransView.setBackgroundColor(context.getResources().getColor(R.color.green));
             //   holder.rootHeaderColor.setBackgroundColor(context.getResources().getColor(R.color.green));
                holder.syncedStateTransaction.setImageResource(R.drawable.ic_transaction_synced);
            }

            holder.txtTransID.setText(""+transaction.getId());
            holder.txtTransRecieptNo.setText(""+transaction.getRecieptNumber());
            holder.txtTransVehicleReg.setText(""+transaction.getVehicleregNumber());
            holder.txtTransType.setText(""+transaction.getTransactionType());
            holder.txtTransPaymentModeId.setText(""+transaction.getPayment_mode_id());

        }



    }



    @Override
    public int getItemCount() {
        return transactions.size();
    }





    class RecyclerViewHolder extends RecyclerView.ViewHolder{

        //LinearLayout rootTransView;
        CardView rootTransView;
        LinearLayout rootHeaderColor;
        ImageView syncedStateTransaction;
        TextView txtTransRecieptNo,txtTransVehicleReg,txtTransType,txtTransPaymentModeId,
                txtTransID;

        RecyclerViewHolder(View view, Context context){
            super(view);

            //rootTransView = (LinearLayout)view.findViewById(R.id.rootTransView);
           // rootTransView = (CardView)view.findViewById(R.id.rootTransView);
           // rootHeaderColor = (LinearLayout)view.findViewById(R.id.rootHeaderColor);
            syncedStateTransaction = (ImageView)view.findViewById(R.id.syncedStateTransaction);
            txtTransRecieptNo = (TextView)view.findViewById(R.id.txtTransRecieptNo);
            txtTransVehicleReg = (TextView)view.findViewById(R.id.txtTransVehicleReg);
            txtTransType = (TextView)view.findViewById(R.id.txtTransType);
            txtTransPaymentModeId = (TextView)view.findViewById(R.id.txtTransPaymentModeId);
            txtTransID = (TextView)view.findViewById(R.id.txtTransID);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickCallback!=null) {
                        if(!transactions.get(getAdapterPosition()).is_synced()) {
                            clickCallback.onClicked(transactions.get(getAdapterPosition()));
                            System.out.println(transactions.get(getAdapterPosition()));
                        }
                    }
                }
            });

        }
    }


}
