package co.vieln.mlkitexperiment.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import co.vieln.mlkitexperiment.R;

public class TextResultActivity extends AppCompatActivity {

    private TextView tvTextResult;
    List<FirebaseVisionText.TextBlock> blocks;
    String text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_result);

        getData();
        initView();
        initData();
    }

    private void getData(){
        Intent intent = getIntent();
        if (intent != null) {
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

    }

    private void initView(){
        tvTextResult = findViewById(R.id.text_result);
    }

    private void initData(){
        tvTextResult.setText(text);
    }
}
