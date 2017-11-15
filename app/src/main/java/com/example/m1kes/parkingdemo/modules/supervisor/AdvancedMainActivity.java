package com.example.m1kes.parkingdemo.modules.supervisor;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.CheckVehiclesLoggedIn;
import com.example.m1kes.parkingdemo.MainActivity;
import com.example.m1kes.parkingdemo.MySchedule;
import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.RegisterAPrinter;
import com.example.m1kes.parkingdemo.SearchVehicle;
import com.example.m1kes.parkingdemo.SyncTransactions;
import com.example.m1kes.parkingdemo.ViewPrinterJobs;
import com.example.m1kes.parkingdemo.adapters.MenuAdapter;
import com.example.m1kes.parkingdemo.modules.supervisor.activities.DumpTransactions;
import com.example.m1kes.parkingdemo.modules.supervisor.activities.SpotCheck;
import com.example.m1kes.parkingdemo.modules.supervisor.util.SupervisorDataManager;
import com.example.m1kes.parkingdemo.util.StartupUtils;

public class AdvancedMainActivity extends AppCompatActivity{

    Integer imageIds[] = {R.drawable.ic_treasure_chest_white_48dp,R.drawable.ic_backup_restore_white_48dp};
    GridView gridView;
    String []keyWords ={"Spot Check","Dump Transactions"};
    private Context context = this;
    ProgressDialog progressDialog;
    private RelativeLayout rootView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_advanced_main);

        setupStandardItems();
        initGui();
        setupGridViewOptions();

    }

    private void setupStandardItems(){
        StartupUtils.setupActionBar("Supervisor Utils",this);
        context = AdvancedMainActivity.this;
    }

    private void initGui(){

        rootView = (RelativeLayout)findViewById(R.id.activity__advanced_main_root);
        gridView = (GridView)findViewById(R.id.gridviewAdvanced);

    }

    private void setupGridViewOptions(){

        gridView.setAdapter(new MenuAdapter(context,imageIds,keyWords));
        gridView.setNumColumns(2);
        gridView.setPadding(0,0,0,0);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0://TODO ADD Code for Spot Checking

                        startActivity(new Intent(context, SpotCheck.class));
                        break;
                    case 1:
                        startActivity(new Intent(context, DumpTransactions.class));
                        break;
                    default:

                }

            }
        });
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            SupervisorDataManager.setActiveSupervisor(null,context);
            startActivity(new Intent(context,MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        SupervisorDataManager.setActiveSupervisor(null,context);
        startActivity(new Intent(context,MainActivity.class));
    }



}
