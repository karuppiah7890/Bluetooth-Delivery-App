package com.karuppiah.app.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button bLogIn;
    private TextView tvStatus;

    SharedPreferences sharedpref = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);

        setContentView(R.layout.login);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final Firebase ref = new Firebase("https://sizzling-inferno-5033.firebaseio.com/Users");

        sharedpref = getSharedPreferences("Login", Context.MODE_PRIVATE);

        tvStatus = (TextView) findViewById(R.id.tvStatus);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bLogIn = (Button) findViewById(R.id.bLogIn);

        bLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final SharedPreferences.Editor editor = sharedpref.edit();

                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();

                if (isNetworkAvailable()) {

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            if (!username.equals("") && !password.equals("")) {

                                String value = (String) snapshot.child(username).getValue();

                                if (value != null) {

                                    if (value.equals(password)) {
                                        tvStatus.setText("Logged In!");
                                        editor.putString("username", username);
                                        editor.putString("password", password);
                                        editor.apply();

                                        Intent i = new Intent(LoginActivity.this, Dashboard.class);

                                        startActivity(i);

                                    } else {
                                        tvStatus.setText("Wrong Password!");

                                    }
                                } else {
                                    tvStatus.setText("Wrong Username");

                                }
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            tvStatus.setText("The read failed: " + firebaseError.getMessage());
                        }
                    });

                } else {
                    String uname = sharedpref.getString("username", "");
                    String pwd = sharedpref.getString("password", "");

                    if (!username.equals("") && !password.equals("")) {

                        if (uname.equals(username)) {

                            if (password.equals(pwd)) {
                                tvStatus.setText("Logged In!");

                                Intent i = new Intent(LoginActivity.this, Dashboard.class);

                                startActivity(i);

                            } else {
                                tvStatus.setText("Wrong Password!");
                                Toast.makeText(LoginActivity.this, "Not local login. Connect to Internet!", Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            //tvStatus.setText("Wrong Username / New Account ? Connect To Internet");
                            Toast.makeText(LoginActivity.this, "Not local login. Connect to Internet!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
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
        super.onPause();

        finish();
    }
}