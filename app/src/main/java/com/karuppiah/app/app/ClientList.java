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

public class ClientList extends ListActivity {

    ListView listView;
    TextView tvHead;
    List<String> mylist;
    Button bUpdateClients;
    int i = 0;
    Firebase ref = null;
    ValueEventListener ve = null;
    File f,dir;
    int flag = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_client_list);

        ref = new Firebase("https://sizzling-inferno-5033.firebaseio.com/Clients");

        listView = (ListView) findViewById(android.R.id.list);
        tvHead = (TextView) findViewById(R.id.tvClientHead);
        bUpdateClients = (Button) findViewById(R.id.bUpdateClients);

        mylist = new LinkedList<>();

        String s = null;

        dir = getDir("MyApp",0);

        f = new File(dir.getAbsolutePath() + File.pathSeparator + "ClientList.txt");

        if (f.exists()) {
            tvHead.setText("Client List!");

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
                tvHead.setText("Could not display Client list!");
            }


        } else
            tvHead.setText("Client List Empty!");


        ArrayAdapter<String> aa = new ArrayAdapter<String>(ClientList.this, android.R.layout.simple_list_item_1, mylist);

        listView.setAdapter(aa);

        bUpdateClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 0) {
                    if(isNetworkAvailable())
                    {
                        update();
                        flag = 1;
                    }

                    else
                        Toast.makeText(ClientList.this, "Connect to Internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        String client = mylist.get(position);

        super.onListItemClick(l, v, position, id);

        SharedPreferences sharedPref = getSharedPreferences("Details", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("client",client);
        editor.apply();

        Intent i = new Intent(ClientList.this, MaterialList.class);
        startActivity(i);

        finishFromChild(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_client_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.UpdateClients) {
            update();
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
                    tvHead.setText("First " + e.toString());
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
                    tvHead.setText("Second : " + e.toString());
                }

                ArrayAdapter<String> aa = new ArrayAdapter<String>(ClientList.this, android.R.layout.simple_list_item_1, mylist);
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

}
