package com.example.breakinterview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;


public class PaintView extends View {

    public static int Brush_size =10;
    public static final int Brush_Color= Color.BLACK;
    public static final int BG_Color=Color.WHITE;
    private static final float tolerance=4;
    private float mx,my;
    private Path mpath;
    private Paint mpaint;
    private ArrayList<FIngerPathOnScreenRecord> paths= new ArrayList<>();
    private int currentColor;
    private int backColor=BG_Color;
    private int strokeWidth;
    private boolean emboss;
    private boolean blur;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    private Bitmap mbitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);


    public PaintView(Context context) {
        this(context,null);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mpaint= new Paint();
        mpaint.setAntiAlias(true);
        mpaint.setDither(true);
        mpaint.setColor(Brush_Color);
        mpaint.setStyle(Paint.Style.STROKE);
        mpaint.setStrokeJoin(Paint.Join.ROUND);
        mpaint.setStrokeCap(Paint.Cap.ROUND);
        mpaint.setXfermode(null);
        mpaint.setAlpha(0xff);

        mEmboss=new EmbossMaskFilter(new float[] {1,1,1}, 0.4f,6,3.5f);
        mBlur= new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);


    }

    public void init(DisplayMetrics metrics)
    {
        int height=metrics.heightPixels;
        int width=metrics.widthPixels;

        mbitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        mCanvas= new Canvas(mbitmap);

        currentColor= Brush_Color;
        strokeWidth= Brush_size;
    }

    public void normal(){

        emboss=false;
        blur=false;

    }

    public void emboss()
    {
        emboss=true;
        blur=false;
    }

    public void blur()
    {
        emboss=false;
        blur=true;
    }

    public void clear()
    {
        backColor=BG_Color;
        paths.clear();
        normal();
        invalidate();
    }

//    @TargetApi(26)
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas.drawColor(backColor);

        for(FIngerPathOnScreenRecord fp: paths){
            mpaint.setColor(fp.color);
            mpaint.setStrokeWidth(fp.strokewidth);
            mpaint.setMaskFilter(null);

            if(fp.emboss)
                mpaint.setMaskFilter(mEmboss);
            else if(fp.blur)
                mpaint.setMaskFilter(mBlur);

          mCanvas.drawPath(fp.path,mpaint);
        }

        canvas.drawBitmap(mbitmap,0,0,mBitmapPaint);
        canvas.restore();
    }

    private void touchStart(float x,float y){

        mpath=new Path();
        FIngerPathOnScreenRecord fp=new FIngerPathOnScreenRecord(currentColor,emboss,blur,strokeWidth,mpath);
        paths.add(fp);

        mpath.reset();
        mpath.moveTo(x,y);
        mx=x;
        my=y;

    }


    private void touchMove(float x,float y){

        float dx=Math.abs(x-mx);
        float dy=Math.abs(y-my);

        if(dx>=tolerance || dy>=tolerance){

            mpath.quadTo(mx,my,(x+mx)/2,(y+my)/2);
            mx=x;
            my=y;
        }
    }

    private void touchUp(){

        mpath.lineTo(mx,my);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x=event.getX();
        float y=event.getY();
        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN :
                touchStart(x,y);
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE :
                touchMove(x,y);
                invalidate();
                break;

            case MotionEvent.ACTION_UP :
                touchUp();
                invalidate();
                break;
        }
        return true;
    }
}


