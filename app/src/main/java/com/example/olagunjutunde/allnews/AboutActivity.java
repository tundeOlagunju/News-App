package com.example.olagunjutunde.allnews;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by OLAGUNJU TUNDE on 12/10/2017.
 */

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);


        TextView textView =(TextView) findViewById(R.id.about1);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/Nunito-Regular.ttf");
        textView.setTypeface(typeface);
    }

}
