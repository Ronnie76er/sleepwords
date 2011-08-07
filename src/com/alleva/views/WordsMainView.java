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
        private double sizeX = 10;
        private double sizeY = 10;
        private int xpos = 100;
        private int ypos = 200;

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
                    sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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

            int realXPos = getPositionFromMidpoint(xpos, sizeX);
            int realYPos = getPositionFromMidpoint(ypos, sizeY);
            int realSizeX = (int)Math.floor(sizeX);
            int realSizeY = (int)Math.floor(sizeY);

            c.drawBitmap(bm, null, new Rect(realXPos, realYPos, realSizeX, realSizeY), null);

            sizeX = getNewSize(sizeX, 0.01);
            sizeY = getNewSize(sizeY, 0.01);

            try {
                wait(16);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


        }

        private double getNewSize(double size, double scale) {
            return size + (size * scale);
        }

        private int getPositionFromMidpoint(int centerPosition, double size) {
            return (int)Math.floor(centerPosition - (size/2));
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
