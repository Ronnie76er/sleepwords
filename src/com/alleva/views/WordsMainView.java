package com.alleva.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * User: ronnie
 * Date: 6/19/11
 */
public class WordsMainView extends SurfaceView {

    private Paint paint;
    private TextView textView;

    public WordsMainView(Context context) {
        super(context);
    }

    public WordsMainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WordsMainView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint = new Paint();
        paint.setColor(Color.DKGRAY);
        paint.setTextSize(12);
        paint.setAntiAlias(true);
        canvas.drawText("Hello World", 50, 200 , paint);



        invalidate();

    }
}
