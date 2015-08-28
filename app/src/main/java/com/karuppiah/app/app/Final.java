package com.karuppiah.app.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


public class Final extends AppCompatActivity {

    private TextView tvClient, tvMaterial, tvWeight;
    private Button bPrint;
    private Button btPrintBill;

    String client, material;
    float weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        tvClient = (TextView) findViewById(R.id.tvClient);
        tvMaterial = (TextView) findViewById(R.id.tvMaterial);
        tvWeight = (TextView) findViewById(R.id.tvWeight);
        bPrint = (Button) findViewById(R.id.btAnother);
        btPrintBill = (Button) findViewById(R.id.btPrint);

        SharedPreferences sharedPref = getSharedPreferences("Details", Context.MODE_PRIVATE);

        client = sharedPref.getString("client", "");
        material = sharedPref.getString("material", "");
        weight = sharedPref.getFloat("weight", -1);

        if (!client.equals(""))
            tvClient.setText(client);

        if (!material.equals(""))
            tvMaterial.setText(material);

        if (weight != -1)
            tvWeight.setText("" + weight);


        writeToFile();

        bPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendPrintRequest();

                Intent i = new Intent(Final.this, MaterialList.class);
                startActivity(i);

                finish();
            }
        });

        btPrintBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bluetooth.mChatService.write(readFromFile().getBytes());

                Intent i = new Intent(Final.this, Dashboard.class);
                startActivity(i);

                finish();
            }
        });
    }

    private String readFromFile() {

        String readData = "";
        String s;

        //File dir = getDir("MyApp", 0);

        //File f = new File(dir.getAbsolutePath() + File.pathSeparator + "Details.txt");

        File f = new File(Environment.getExternalStorageDirectory() + "/currDetails.txt");

        FileReader fr = null;
        BufferedReader br = null;

        try {

            fr = new FileReader(f);
            br = new BufferedReader(fr);

        } catch (Exception e) {
            Log.i("Final", e.toString());
        }

        try {
            readData = client + "\n";
            while ((s = br.readLine()) != null) {
                readData += "\n" + br.readLine();
                readData += ":    " + br.readLine();
            }

            br.close();
            fr.close();

        } catch (Exception e) {
            Log.i("Final", e.toString());
        }

        return readData;
    }

    private void writeToFile() {

        //File dir = getDir("MyApp", 0);

        //File f = new File(dir.getAbsolutePath() + File.pathSeparator + "Details.txt");

        File f = new File(Environment.getExternalStorageDirectory() + "/Details.txt");
        File f1  = new File(Environment.getExternalStorageDirectory() + "/currDetails.txt");

        FileWriter fw = null;
        FileWriter fw1 = null;
        BufferedWriter bw = null;
        BufferedWriter bw1 = null;
        FileReader fr = null;
        BufferedReader br = null;

        try {

            fw = new FileWriter(f, true);
            bw = new BufferedWriter(fw);

            if(f1.exists()) {
                fr = new FileReader(f1);
                br = new BufferedReader(fr);

                String s;

                if ((s = br.readLine()) != null) {

                    if (s.equals(client)) {
                        fw1 = new FileWriter(f1, true);
                        bw1 = new BufferedWriter(fw1);
                    } else {
                        fw1 = new FileWriter(f1);
                        bw1 = new BufferedWriter(fw1);
                    }

                }
            }

            else {

                fw1 = new FileWriter(f1, true);
                bw1 = new BufferedWriter(fw1);
            }

            bw.write(client + "\n");
            bw1.write(client + "\n");

            bw.write(material + "\n");
            bw1.write(material + "\n");

            bw.write(Float.toString(weight) + "\n");
            bw1.write(Float.toString(weight) + "\n");

            bw.close();
            fw.close();

            bw1.close();
            fw1.close();

            br.close();
            fr.close();

        } catch (Exception e) {
            Log.i("Final", e.toString());
        }

    }
}