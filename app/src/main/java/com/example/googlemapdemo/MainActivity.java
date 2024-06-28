package com.example.googlemapdemo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText etFromLocation;
    private EditText etToLocation;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

//        etFromLocation = findViewById(R.id.et_from_location);
//        etToLocation = findViewById(R.id.et_to_location);
//        button = findViewById(R.id.button);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String userLocation = etFromLocation.getText().toString();
//                String userDestination = etToLocation.getText().toString();
//
//                if(userLocation.equals("") || userDestination.equals("")){
//                    Toast.makeText(MainActivity.this, "Please enter valid address", Toast.LENGTH_SHORT).show();
//                } else{
//                    getDirections(userLocation, userDestination);
//                }
//            }
//        });
    }

    public void getDirections(String userLocation, String userDestination){
        try{
            Uri uri = Uri.parse("http://www.google.com/maps/dir/"+userLocation+"/"+userDestination);
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            i.setPackage("com.google.android.apps.maps");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }catch (ActivityNotFoundException e){
            Uri uri = Uri.parse("http://www.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }
}