package com.pluto.www.lamp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Main2Activity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver,makeGattUpdateIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout2);

        initView();


    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED );
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED );
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED );
            intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE );
            intentFilter.addAction(BluetoothLeService.READ_RSSI);
        return intentFilter;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)){
                //连接成功
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                //未连接
            }
        }
    };
}
