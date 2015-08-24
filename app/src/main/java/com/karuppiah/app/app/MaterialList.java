package com.karuppiah.app.app;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;


public class MaterialList extends ListActivity {

    ListView listView;
    List<String> mylist;
    Button bUpdateMaterials;
    int i = 0;
    Firebase ref = null;
    ValueEventListener ve = null;
    File f,dir;
    int flag = 0;
    TextView tvMaterialHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_material_list);

        tvMaterialHead = (TextView) findViewById(R.id.tvMaterialHead);

        ref = new Firebase("https://sizzling-inferno-5033.firebaseio.com/Materials");

        listView = (ListView) findViewById(android.R.id.list);
        tvMaterialHead = (TextView) findViewById(R.id.tvMaterialHead);
        bUpdateMaterials = (Button) findViewById(R.id.bUpdateMaterials);

        mylist = new LinkedList<>();

        String s = null;

        dir = getDir("MyApp",0);

        f = new File(dir.getAbsolutePath() + File.pathSeparator + "MaterialList.txt");

        if (f.exists()) {
            tvMaterialHead.setText("Material List!");

            FileReader fr;
            BufferedReader br;

            try {
                fr = new FileReader(f);
                br = new BufferedReader(fr);

                while ((s = br.readLine()) != null)
                    mylist.add(s);

                br.close();
                fr.close();
            } catch (Exception e) {
                tvMaterialHead.setText("Could not display Material list!");
            }


        } else
            tvMaterialHead.setText("Material List Empty!");


        ArrayAdapter<String> aa = new ArrayAdapter<String>(MaterialList.this, android.R.layout.simple_list_item_1, mylist);

        listView.setAdapter(aa);

        bUpdateMaterials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 0) {
                    if(isNetworkAvailable())
                    {
                        update();
                        flag = 1;
                    }

                    else
                        Toast.makeText(MaterialList.this, "Connect to Internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        String material = mylist.get(position);

        super.onListItemClick(l, v, position, id);

        SharedPreferences sharedPref = getSharedPreferences("Details", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("material",material);
        editor.apply();

        Intent i = new Intent(MaterialList.this, Bluetooth.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_material_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.UpdateMaterials) {



            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void update() {

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                FileWriter fr = null;
                BufferedWriter br = null;

                try {

                    fr = new FileWriter(f);
                    br = new BufferedWriter(fr);

                } catch (Exception e) {
                    tvMaterialHead.setText("First " + e.toString());
                }

                mylist.clear();

                try {

                    for (DataSnapshot i : snapshot.getChildren()) {
                        String s = (String) i.child("name").getValue();

                        mylist.add(s);

                        br.write(s + "\n");
                    }

                    br.close();
                    fr.close();

                } catch (Exception e) {
                    tvMaterialHead.setText("Second : " + e.toString());
                }

                ArrayAdapter<String> aa = new ArrayAdapter<String>(MaterialList.this, android.R.layout.simple_list_item_1, mylist);
                listView.setAdapter(aa);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onPause() {
        finishFromChild(this);
        super.onPause();
    }
}
