package com.karuppiah.app.app;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.karuppiah.app.app.common.logger.Log;


public class Bluetooth extends AppCompatActivity {

    private static EditText etWeight;
    private Button bOk;

    private static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public static BluetoothChatService mChatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        etWeight = (EditText) findViewById(R.id.etWeight);
        bOk = (Button) findViewById(R.id.bOk);

        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float weight = Float.parseFloat(etWeight.getText().toString());

                SharedPreferences sharedPref = getSharedPreferences("Details", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putFloat("weight", weight);
                editor.apply();

                Intent i = new Intent(Bluetooth.this, Final.class);
                startActivity(i);

                finishFromChild(Bluetooth.this);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 1);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
    }

    private void setupChat() {
        Log.d("Hello", "setupChat()");

        if(mChatService == null) {
            // Initialize the BluetoothChatService to perform bluetooth connections
            mChatService = new BluetoothChatService(this, mHandler);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    etWeight.setText(readMessage);
                    Toast.makeText(Bluetooth.this, "Data Received", Toast.LENGTH_LONG).show();
                    break;
                case Constants.MESSAGE_WRITE:
                    Toast.makeText(Bluetooth.this, "Data Sent", Toast.LENGTH_LONG).show();
            }
        }
    };

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

}
