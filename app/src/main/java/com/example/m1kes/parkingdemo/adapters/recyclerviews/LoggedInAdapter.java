package com.example.m1kes.parkingdemo.adapters.recyclerviews;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.sqlite.adapters.TransactionAdapter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.m1kes.parkingdemo.util.DBUtils.getDateParts;


public class LoggedInAdapter extends RecyclerView.Adapter<LoggedInAdapter.RecyclerViewHolder> {

    private  List<Transaction> transactions;
    private Context context;
    private LoggedInAdapter.RecyclerViewHolder viewHolder;

    public LoggedInAdapter(Context context, List<Transaction> transactions) {
        this.transactions = transactions;
        this.context = context;
    }



    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.logged_in_vehicle_row,parent,false);
        viewHolder = new LoggedInAdapter.RecyclerViewHolder(view,context);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        Transaction transaction = transactions.get(position);


        if(transaction!=null){
            holder.txtLoggedIn_RegNum.setText(transaction.getVehicleregNumber());


            String expiryDateTime = ""+transaction.getExpiry_datetime();

            Calendar cal = Calendar.getInstance();
            String[] parts= getDateParts(expiryDateTime);
            cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])-1, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
            Date lastLoggedDate = cal.getTime();

            System.out.println("*******************************************************************************************");
            System.out.println("Expiry Date Time : "+lastLoggedDate + " Current Date Time: "+new Date());
            System.out.println("Expiry Date Time : "+lastLoggedDate.getTime() + " Current Date Time: "+new Date().getTime());
            System.out.println("*******************************************************************************************");

            if(new Date().before(lastLoggedDate)){
                long diff = lastLoggedDate.getTime() - new Date().getTime();//as given

                long diffSeconds = diff / 1000 % 60;
                long diffMinutes = diff / (60 * 1000) % 60;
                long diffHours = diff / (60 * 60 * 1000);


                String duration = parseDuration(diffHours,diffMinutes,diffSeconds);
                holder.txtLoggedIn_Time.setText(duration);


            }

            System.out.println(transaction.getExpiry_datetime());

        }



    }





    private String parseDuration(long diffHours,long diffMinutes, long diffSeconds){

        String duration ="";

        if(diffHours<0){
            duration += Math.abs(diffHours);
        }else{
            duration += diffHours;
        }
        duration += " hrs ";

        if(diffMinutes<0){
            duration += Math.abs(diffMinutes);
        }else {
            duration += diffMinutes;
        }

        duration += " m ";

        if(diffSeconds<0){
            duration += Math.abs(diffSeconds);
        }else {
            duration += diffSeconds;
        }
        duration += " s ";

        return duration;
    }


    @Override
    public int getItemCount() {
        return transactions.size();
    }



     class RecyclerViewHolder extends RecyclerView.ViewHolder{

        TextView txtLoggedIn_RegNum,txtLoggedIn_Time;

         RecyclerViewHolder(View view,Context context){
            super(view);

             txtLoggedIn_RegNum = (TextView)view.findViewById(R.id.txtLoggedIn_RegNum);
             txtLoggedIn_Time = (TextView)view.findViewById(R.id.txtLoggedIn_Time);


        }
    }


}
