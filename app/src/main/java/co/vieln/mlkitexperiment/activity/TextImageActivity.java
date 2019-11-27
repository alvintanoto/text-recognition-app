package co.vieln.mlkitexperiment.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import co.vieln.mlkitexperiment.R;

public class TextImageActivity extends AppCompatActivity {

    private static int IMAGE_CODE = 0;
    private String intentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_image);

        getData();
        getImage();
    }

    private void getData(){
        Intent intent = getIntent();
        if(intent!=null){
            intentData = intent.getStringExtra("data");
        }
    }

    private void getImage(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, IMAGE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == IMAGE_CODE) {
            if(data.getData()!=null){
                if(intentData.equals("TEXT")){
                    FirebaseVisionImage image;
                    try {
                        image = FirebaseVisionImage.fromFilePath(TextImageActivity.this, data.getData());
                        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                                .getOnDeviceTextRecognizer();
                        detector.processImage(image)
                                .addOnSuccessListener(
                                        new OnSuccessListener<FirebaseVisionText>() {
                                            @Override
                                            public void onSuccess(FirebaseVisionText results) {
                                                Log.d("ALVIN", "onSuccess: "+results);

                                                List<FirebaseVisionText.TextBlock> blocks;
                                                blocks = results.getTextBlocks();

                                                openResultActivity(blocks);
                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("ALVIN", "onFailure: "+e.toString());
                                            }
                                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(intentData.equals("OBJECT")){
                    FirebaseVisionImage image;
                    try {
                        image = FirebaseVisionImage.fromFilePath(TextImageActivity.this, data.getData());
                        FirebaseVisionObjectDetectorOptions options =
                                new FirebaseVisionObjectDetectorOptions.Builder()
                                        .setDetectorMode(FirebaseVisionObjectDetectorOptions.STREAM_MODE)
                                        .build();

                        FirebaseVisionObjectDetector detector = FirebaseVision.getInstance()
                                .getOnDeviceObjectDetector(options);
                        detector.processImage(image)
                                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionObject>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionObject> firebaseVisionObjects) {
                                        Log.d("ALVIN", "onSuccess: "+firebaseVisionObjects);
                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("ALVIN", "onFailure: "+e.toString());
                                            }
                                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void openResultActivity(List<FirebaseVisionText.TextBlock> blocks){
        Gson gson = new Gson();
        Intent intent = new Intent(TextImageActivity.this, TextResultActivity.class);
        intent.putExtra("BLOCK_SIZE", blocks.size());

        for (int i = 0; i < blocks.size(); i++) {
            String json = gson.toJson(blocks.get(i));
            intent.putExtra("BLOCK_KE_" + i, json);
        }

        finish();
        startActivity(intent);
    }
}
