package com.lyldding.simplepolygonview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lyldding.library.SimplePolygonView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SimplePolygonView dimView = findViewById(R.id.demo_view3);
        int sides = dimView.getSides();
        List<Float> dimPercentages = new ArrayList<>();
        for (int index = 0; index < sides; index++) {
            dimPercentages.add((index + 1f) / (sides + 1f));
        }
        dimView.setDimPercentages(dimPercentages);
        dimView.setPolygonShowDim(true);
    }
}
