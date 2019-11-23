package co.vieln.mlkitexperiment.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import co.vieln.mlkitexperiment.R;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSelectImage;
    private Button btnOpenCamera;

    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        getData();
        initView();
        initListener();
    }

    private void getData(){
        Intent intent = getIntent();
        if(intent!=null){
            data = intent.getStringExtra("data");
        }
    }

    private void initView() {
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnOpenCamera = findViewById(R.id.btn_open_camera);
    }

    private void initListener() {
        btnSelectImage.setOnClickListener(this);
        btnOpenCamera.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == btnSelectImage){
            Intent intent = new Intent(MenuActivity.this, TextImageActivity.class);
            intent.putExtra("data", data);
            startActivity(intent);
        } else if(v == btnOpenCamera){
            Intent intent = new Intent(MenuActivity.this, CameraActivity.class);
            intent.putExtra("data", data);
            startActivity(intent);
        }
    }
}
