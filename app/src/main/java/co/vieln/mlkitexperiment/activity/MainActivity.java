package co.vieln.mlkitexperiment.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import co.vieln.mlkitexperiment.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "ALVIN";

    private Button btnTextRecognition;
    private Button btnObjectRecognition;
    private Button btnFaceRecognition;
    private Button btnBarcodeRecognition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
    }

    private void initView(){
        btnTextRecognition = findViewById(R.id.btn_text_recognition);
        btnObjectRecognition = findViewById(R.id.btn_object_recognition);
        btnFaceRecognition = findViewById(R.id.btn_face_recognition);
        btnBarcodeRecognition = findViewById(R.id.btn_barcode_recognition);
    }

    private void initListener(){
        btnTextRecognition.setOnClickListener(this);
        btnObjectRecognition.setOnClickListener(this);
        btnFaceRecognition.setOnClickListener(this);
        btnBarcodeRecognition.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == btnTextRecognition){
            String intentData = "TEXT";
            openMenuActivity(intentData);
        } else if(v == btnObjectRecognition){
            String intentData = "OBJECT";
            openMenuActivity(intentData);
        } else if(v == btnFaceRecognition){
            String intentData = "FACE";
            openMenuActivity(intentData);
        } else if(v == btnBarcodeRecognition){
            String intentData = "BARCODE";
            openMenuActivity(intentData);
        }
    }

    private void openMenuActivity(String intentData){
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        intent.putExtra("data", intentData);
        startActivity(intent);
    }
}