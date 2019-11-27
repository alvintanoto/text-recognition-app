package co.vieln.mlkitexperiment.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.vieln.mlkitexperiment.R;

public class TextResultActivity extends AppCompatActivity {

    private View layoutDefault;
    private View layoutFace;

    private TextView tvTextResult;
    private TextView textImgResult;
    private ImageView imgResult;

    List<FirebaseVisionText.TextBlock> blocks;

    String text = "";
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_result);
        initView();

        getData();
        initData();
    }

    private void getData(){
        Intent intent = getIntent();

        data = intent.getStringExtra("data");
        if(data!=null){
            Log.d("ALVIN", "getData: "+data);
            if(data.equals("TEXT")){
                getText(intent);
            } else if(data.equals("OBJECT")){

            } else if(data.equals("FACE")) {
                getFaces(intent);
            } else if(data.equals("BARCODE")){
                getBarcode(intent);
            }
        }

    }

    private void initView(){
        layoutDefault = findViewById(R.id.layout_default);
        layoutFace = findViewById(R.id.layout_face);

        tvTextResult = findViewById(R.id.text_result);
        imgResult = findViewById(R.id.face_text_image_result);
        textImgResult = findViewById(R.id.face_text_result);
    }

    private void initData(){
        tvTextResult.setText(text);
    }

    private void getText(Intent intent){
        showDefaultLayout();
        blocks = new ArrayList<>();

        int length = intent.getIntExtra("BLOCK_SIZE", 0);
        for (int i = 0; i < length; i++) {
            FirebaseVisionText.TextBlock block = new Gson().fromJson(intent.getStringExtra("BLOCK_KE_" + i), FirebaseVisionText.TextBlock.class);
            blocks.add(block);
        }

        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    text += elements.get(k).getText() + " ";
                }
                text += "\n";
            }
            text+="\n\n";
        }
    }

    private void getBarcode(Intent intent){
        showDefaultLayout();
        int length = intent.getIntExtra("BLOCK_SIZE", 0);
        for (int i = 0; i < length; i++) {
            text += intent.getStringExtra("DISPLAY_VALUE_"+i);
            text += '\n';
        }
    }

    private void getFaces(Intent intent){
        showFaceLayout();

        String imgPath = intent.getStringExtra("img_path");

        File file = new File(imgPath);
        Uri uri = Uri.fromFile(file);

        Log.d("ALVIN", "getFaces: "+imgPath);

        Glide.with(this).load(imgPath).into(imgResult);
        textImgResult.setText("asdfkasdlfjaslkdfjlaksdjflaksjdfkljalskdfjlaskfjdklsdjfksjdafkjdlkfjaslkfd");
    }

    private void showDefaultLayout(){
        layoutDefault.setVisibility(View.VISIBLE);
        layoutFace.setVisibility(View.INVISIBLE);
    }

    private void showFaceLayout(){
        layoutDefault.setVisibility(View.INVISIBLE);
        layoutFace.setVisibility(View.VISIBLE);
    }
}
