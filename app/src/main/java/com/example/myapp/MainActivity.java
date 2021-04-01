package com.example.myapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button signInButton, signUpButton;
    EditText emailText, passwordText;
    int connectionType;
    // to check if we are connected to Network
    boolean isConnected = true;

    // to check if we are monitoring Network
    private boolean monitoringConnectivity = false;

    int counter = 3;

    private ConnectivityManager.NetworkCallback connectivityCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = (Button) findViewById(R.id.signInButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);

        emailText = (EditText) findViewById(R.id.emailText);
        passwordText = (EditText) findViewById(R.id.passwordText);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailText.getText().toString().equals("admin@email.com") && passwordText.getText().toString().equals("admin")) {
                    Toast.makeText(getApplicationContext(), "Redirecting...", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(v.getContext(), HomeActivity.class);
                    v.getContext().startActivity(myIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                    counter--;
                    if (counter == 0) {
                        signInButton.setEnabled(false);
                    }
                }
            }
        });

        if(!isOnline() || connectionType == ConnectivityManager.TYPE_MOBILE){
            Button checkConnection = findViewById(R.id.checkConnection);
            checkConnection.setVisibility(View.VISIBLE);
            checkConnection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    startActivity(getIntent());
                }
            });

            if(!isOnline()){
                signInButton.setEnabled(false);
                signUpButton.setEnabled(false);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.connectToWifi).setTitle(R.string.wifiAlertTitle);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    checkConnection.setVisibility(View.INVISIBLE);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null){
            connectionType = networkInfo.getType();
        }
        return (networkInfo != null && networkInfo.isConnected());
    }
}