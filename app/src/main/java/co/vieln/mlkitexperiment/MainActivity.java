package co.vieln.mlkitexperiment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import co.vieln.mlkitexperiment.camera.CameraSource;
import co.vieln.mlkitexperiment.camera.CameraSourcePreview;
import co.vieln.mlkitexperiment.text_recognition.TextRecognitionProcessor;
import co.vieln.mlkitexperiment.utils.GraphicOverlay;

public class MainActivity extends AppCompatActivity {

    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;

    private Button mBtnCapture;

    private TextRecognitionProcessor textRecognitionProcessor;

    private static String TAG = MainActivity.class.getSimpleName().toString().trim();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, 0);
        }

        textRecognitionProcessor = new TextRecognitionProcessor();

        preview = (CameraSourcePreview) findViewById(R.id.camera_source);
        if (preview == null) {
            Log.d(TAG, "Preview is null");
        }
        graphicOverlay = (GraphicOverlay) findViewById(R.id.graphic_overlay);
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }

        mBtnCapture = (Button) findViewById(R.id.btn_capture);
        mBtnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d(TAG, "onClick: "+ textRecognitionProcessor.getData());
                List<FirebaseVisionText.TextBlock> blocks = textRecognitionProcessor.getData();

                Gson gson = new Gson();
//                String json = gson.toJson(textRecognitionProcessor.getData());
                Intent intent = new Intent(MainActivity.this, TextActivity.class);
                intent.putExtra("BLOCK_SIZE", blocks.size());

                for(int i=0;i<blocks.size();i++){
                    String json = gson.toJson(blocks.get(i));
                    intent.putExtra("BLOCK_KE_"+i, json);
//                    List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
//                    for(int j=0; j<lines.size();j++){
//                        List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
//                        for(int k=0; k<elements.size();k++){
//                            Log.d(TAG, "onClick: "+elements.get(k).getText());
//                        }
//                    }
                }

                startActivity(intent);
            }
        });

        createCameraSource();
        startCameraSource();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    private void createCameraSource() {

        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
            cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
        }

        cameraSource.setMachineLearningFrameProcessor(textRecognitionProcessor);
    }

    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: "+RESULT_OK+","+grantResults[0]);
        if(requestCode==0 && grantResults[0]==RESULT_OK){

        }
    }
}