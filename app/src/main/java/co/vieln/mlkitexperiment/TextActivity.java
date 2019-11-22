package co.vieln.mlkitexperiment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TextActivity extends AppCompatActivity {

    List<FirebaseVisionText.TextBlock> blocks;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        mTextView = findViewById(R.id.mText);
        String data;
        String text = "";

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


            mTextView.setText(text);
        }
    }
}
