package com.alleva.views;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alleva.R;

import java.util.Random;
import java.util.logging.Handler;

/**
 * User: ronnie
 * Date: 6/19/11
 */
public class WordsMainView extends SurfaceView implements SurfaceHolder.Callback{

    private Thread thread;

    class WordThread extends Thread{

        private SurfaceHolder _surfaceHolder;
        private int numValue = 0;
        private float size = 0;
        private int sizeX = 10;
        private int sizeY = 10;

        public WordThread(SurfaceHolder surfaceHolder){
            _surfaceHolder = surfaceHolder;
        }

        @Override
        public void run() {

            Paint paint = new Paint();
            Paint clearPaint = new Paint();
            clearPaint.setColor(Color.BLACK);
            clearPaint.setStyle(Paint.Style.FILL);


            paint.setAntiAlias(true);
            paint.setTextSize(50);
            paint.setColor(Color.DKGRAY);
            paint.setAlpha(255);
            String text =   "Some text";

            Rect bounds = new Rect();

            paint.getTextBounds(text, 0, text.length(), bounds);

            Bitmap bm = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888);
            Canvas testCanvas = new Canvas(bm);

            testCanvas.drawPaint(clearPaint);
            testCanvas.drawText( text, 0, bounds.height() -1 , paint);


            while (true){
                Canvas c = null;

                try {
                    c = _surfaceHolder.lockCanvas(null);
                    doDraw(c, bm);
                } finally {
                    if(c != null){
                        _surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        private synchronized void doDraw(Canvas c, Bitmap bm) {

            Paint clearPaint = new Paint();
            clearPaint.setColor(Color.BLACK);
            clearPaint.setStyle(Paint.Style.FILL);
            c.drawPaint(clearPaint);

            c.drawBitmap(bm, null, new Rect(10, 10, sizeX, sizeY), null);
            sizeY += 4;
            sizeX = (sizeY - 10) * 2;


            try {
                wait(16);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


        }
    }

    private TextView textView;
    private Animation mainAnimation;

    public WordsMainView(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        thread = new WordThread(getHolder());
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        thread.start();
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //To change body of implemented methods use File | Settings | File Templates.
    }



}
