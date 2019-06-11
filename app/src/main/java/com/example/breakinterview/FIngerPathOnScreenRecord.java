package com.example.breakinterview;

import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;



public class FIngerPathOnScreenRecord extends AppCompatActivity {


    public int color;
    public boolean emboss;
    public boolean blur;
    public int strokewidth;
    public Path path;

    public FIngerPathOnScreenRecord(int color, boolean emboss, boolean blur, int strokewidth, Path path) {

        this.color = color;
        this.emboss = emboss;
        this.blur = blur;
        this.strokewidth = strokewidth;
        this.path = path;

    }


}
