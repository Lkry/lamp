package com.pluto.www.lamp;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 0;
    private Button button1;
    private BluetoothAdapter mBluetoothAdapter;//蓝牙适配器
    public  BluetoothManager mBluetoothManager;//蓝牙管理器
    SharedPreferences mySharedPreferences ;
    private BluetoothGatt mBluetoothGatt;
    private String address;
    private BroadcastReceiver mBroadcastReceiver;
    private BluetoothLeService mBluetoothLeService = new BluetoothLeService();


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);


        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);//绑定服务
        Log.d("123", "Try to bindService=" + bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE));


        mySharedPreferences = getSharedPreferences("test", Activity.MODE_PRIVATE);
        //初始化蓝牙
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        //打开蓝牙
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(MainActivity.this, "请确定蓝牙您的蓝牙已打开", Toast.LENGTH_SHORT).show();
                 initDialog();

                // Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                // startActivity(intent);
            }
        });
    }



    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入");    //设置对话框标题
        final EditText edit = new EditText(this);
        builder.setView(edit);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "你输入的是: " + edit.getText().toString(), Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.putString("address",edit.getText().toString()).apply();
                Log.d("123",edit.getText().toString());
                mBluetoothLeService.connect(edit.getText().toString());
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "你点了取消", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED );

        return intentFilter;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)){
                //连接成功
                 Intent intent1 = new Intent(MainActivity.this,Main2Activity.class);
                 startActivity(intent1);
            }
        }
    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {//2

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {

            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();
            Log.d("123", "LeService" + mBluetoothLeService.toString());
            if (!mBluetoothLeService.initialize()) {//3
                Log.e("123", "Unable to initialize Bluetooth");
                finish();
            } else Log.d("123", "能初始化");
            // 自动连接to the device upon successful start-up
            // 初始化.
            /*int currentNum = mViewPager.getCurrentItem();
            int currentBleNum = currentNum + 1;
            mBluetoothLeService.connect(spre.getString("Address"+currentBleNum,"error"));//4*/
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("123", "没有连接");
            mBluetoothLeService = null;
        }
    };

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
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }
}

