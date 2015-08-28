package com.karuppiah.app.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Dashboard extends ActionBarActivity implements View.OnClickListener {

    private Button bDeliver,bUpload,bExit;

    Firebase ref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_dashboard);

        ref = new Firebase("https://sizzling-inferno-5033.firebaseio.com/OrderDetails");

        bDeliver = (Button) findViewById(R.id.bDeliver);
        bUpload = (Button) findViewById(R.id.bUpload);
        bExit = (Button) findViewById(R.id.bExit);

        bDeliver.setOnClickListener(this);
        bUpload.setOnClickListener(this);
        bExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id)
        {
            case R.id.bDeliver :

                Intent i = new Intent(Dashboard.this,ClientList.class);

                startActivity(i);

                finishFromChild(this);

                break;

            case R.id.bUpload :

                upload();

                break;

            case R.id.bExit :

                finish();

                break;
        }

    }

    private void upload()
    {
        String s ;

        //File dir = getDir("MyApp", 0);

        //File f = new File(dir.getAbsolutePath() + File.pathSeparator + "Details.txt");

        File f = new File(Environment.getExternalStorageDirectory() + "/Details.txt");
        
        FileReader fr = null;
        BufferedReader br = null;

        try {

            fr = new FileReader(f);
            br = new BufferedReader(fr);

        } catch (Exception e) {
            Log.i("Final", e.toString());
        }

        try {

            while((s = br.readLine())!=null)
            {
                Map<String, String> post = new HashMap<String, String>();
                post.put("client", s);

                s = br.readLine();
                post.put("material", s);

                s = br.readLine();
                post.put("weight", s);

                ref.push().setValue(post);
            }

            br.close();
            fr.close();

        } catch (Exception e) {
            Log.i("Final",e.toString());
        }

        if(f.exists())
            f.delete();

        Toast.makeText(this, "Uploaded the details succefully!",Toast.LENGTH_LONG);

    }


    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
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
