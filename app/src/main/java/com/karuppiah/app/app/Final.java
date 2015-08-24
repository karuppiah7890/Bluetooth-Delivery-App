package com.karuppiah.app.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


public class Final extends AppCompatActivity {

    private TextView tvClient,tvMaterial,tvWeight;
    private Button bPrint;

    String client,material;
    float weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        tvClient = (TextView) findViewById(R.id.tvClient);
        tvMaterial = (TextView) findViewById(R.id.tvMaterial);
        tvWeight = (TextView) findViewById(R.id.tvWeight);
        bPrint = (Button) findViewById(R.id.bPrint);

        SharedPreferences sharedPref = getSharedPreferences("Details", Context.MODE_PRIVATE);

        client = sharedPref.getString("client", "");
        material = sharedPref.getString("material","");
        weight = sharedPref.getFloat("weight", -1);

        if(!client.equals(""))
            tvClient.setText(client);

        if(!material.equals(""))
            tvMaterial.setText(material);

        if(weight!=-1)
            tvWeight.setText("" + weight);

        bPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                writeToFile();

                //sendPrintRequest();

                Intent i = new Intent(Final.this, Dashboard.class);
                startActivity(i);
            }
        });

    }

    private void writeToFile() {

        File dir = getDir("MyApp",0);

        File f = new File(dir.getAbsolutePath() + File.pathSeparator + "Details.txt");

        FileWriter fr = null;
        BufferedWriter br = null;

        try {

            fr = new FileWriter(f,true);
            br = new BufferedWriter(fr);

        } catch (Exception e) {
            Log.i("Final",e.toString());
        }

        try {

            br.write(client + "\n");
            br.write(material + "\n");
            br.write(Float.toString(weight) + "\n");

            br.close();
            fr.close();

        } catch (Exception e) {
            Log.i("Final",e.toString());
        }

    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_final, menu);
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

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }
}
