package co.vieln.mlkitexperiment.activity;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import co.vieln.mlkitexperiment.Libs.CameraSource;
import co.vieln.mlkitexperiment.Libs.CameraSourcePreview;
import co.vieln.mlkitexperiment.Libs.GraphicOverlay;
import co.vieln.mlkitexperiment.Libs.object_recognition.ObjectDetectorProcessor;
import co.vieln.mlkitexperiment.Libs.text_recognition.TextRecognitionProcessor;
import co.vieln.mlkitexperiment.R;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private int REQUEST_CODE_PERMISSIONS = 0;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};

    private static String TAG = "ALVIN";
    private String data;

    private CameraSource cameraSource = null;
    private CameraSourcePreview cameraView;
    private GraphicOverlay graphicOverlay;
    private Button btnCaputureText;

    private TextRecognitionProcessor textRecognitionProcessor;
    private ObjectDetectorProcessor objectDetectorProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        getData();
        initView();
        initListener();
        initData();
        checkPermissions();
    }

    private void getData(){
        Intent intent = getIntent();
        if(intent!=null){
            data = intent.getStringExtra("data");
        }
    }

    private void initView() {
        cameraView = findViewById(R.id.camera_view);
        graphicOverlay = findViewById(R.id.graphic_overlay);
        btnCaputureText = findViewById(R.id.btn_capture);
    }

    private void initListener(){
        btnCaputureText.setOnClickListener(this);
    }

    private void initData(){
        if(data.equals("TEXT")){
            textRecognitionProcessor = new TextRecognitionProcessor();
        } else if(data.equals("OBJECT")){
            FirebaseVisionObjectDetectorOptions options =
                    new FirebaseVisionObjectDetectorOptions.Builder()
                            .setDetectorMode(FirebaseVisionObjectDetectorOptions.STREAM_MODE)
                            .enableClassification()  // Optional
                            .build();

            objectDetectorProcessor = new ObjectDetectorProcessor(options);
        }
    }

    private void checkPermissions() {
        if (allPermissionsGranted()) {
            createCameraSource();
            startCameraSource();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void createCameraSource() {

        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
            cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
        }

        if (data.equals("TEXT")) {
            cameraSource.setMachineLearningFrameProcessor(textRecognitionProcessor);
        } else if(data.equals("OBJECT")){
            cameraSource.setMachineLearningFrameProcessor(objectDetectorProcessor);
        }
    }

    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (cameraView == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                cameraView.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                createCameraSource();
                startCameraSource();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    @Override
    public void onClick(View v) {
        if(v == btnCaputureText){
            openTextResultActivity();
        }
    }

    private void openTextResultActivity() {
        if(data.equals("TEXT")){
            List<FirebaseVisionText.TextBlock> blocks = textRecognitionProcessor.getData();

            Gson gson = new Gson();
            Intent intent = new Intent(CameraActivity.this, TextResultActivity.class);
            intent.putExtra("BLOCK_SIZE", blocks.size());

            for (int i = 0; i < blocks.size(); i++) {
                String json = gson.toJson(blocks.get(i));
                intent.putExtra("BLOCK_KE_" + i, json);
            }

            startActivity(intent);
        }
    }
}
