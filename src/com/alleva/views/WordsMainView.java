package com.alleva.views;

import android.content.Context;
import android.content.SyncContext;
import android.graphics.*;
import android.os.Handler;
import android.os.Message;
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
import java.util.logging.LogRecord;

/**
 * User: ronnie
 * Date: 6/19/11
 */
public class WordsMainView extends SurfaceView implements SurfaceHolder.Callback {

    private Thread thread;

    private Boolean newWord = new Boolean(false);

    class WordThread extends Thread {

        private SurfaceHolder _surfaceHolder;
        private int numValue = 0;
        private float size = 0;
        private double sizeX = 10;
        private double sizeY = 10;
        private int xpos = 100;
        private int ypos = 200;

        public WordThread(SurfaceHolder surfaceHolder) {
            _surfaceHolder = surfaceHolder;
        }

        @Override
        public void run() {


            while (true) {

                Paint paint = new Paint();
                Paint clearPaint = new Paint();
                clearPaint.setColor(Color.BLACK);
                clearPaint.setStyle(Paint.Style.FILL);


                paint.setAntiAlias(true);
                paint.setTextSize(60);
                paint.setColor(Color.DKGRAY);
                paint.setAlpha(255);
                String text = "Some text";

                Rect bounds = new Rect();

                paint.getTextBounds(text, 0, text.length(), bounds);

                Bitmap bm = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888);
                Canvas testCanvas = new Canvas(bm);

                testCanvas.drawPaint(clearPaint);
                testCanvas.drawText(text, 0, bounds.height() - 1, paint);

                sizeY = 10;
                sizeX = sizeY * (bounds.width() / bounds.height());

                newWord = false;
                messageHandler.postDelayed(newWordTask, 5000);

                while (true) {
                    Canvas c = null;

                    try {
                        c = _surfaceHolder.lockCanvas(null);
                        doDraw(c, bm);

                        if (newWord) {
                            break;
                        }
                    } finally {
                        if (c != null) {
                            _surfaceHolder.unlockCanvasAndPost(c);
                        }
                    }
                }
            }
        }

        private synchronized void doDraw(Canvas c, Bitmap bm) {

            Paint clearPaint = new Paint();
            clearPaint.setColor(Color.BLACK);
            clearPaint.setStyle(Paint.Style.FILL);
            c.drawPaint(clearPaint);

            Rect rect = getRectangleFromDimensions(xpos, ypos, sizeX, sizeY);

            c.drawBitmap(bm, null, rect, null);
            drawSize(c);

            sizeX = getNewSize(sizeX, 0.02);
            sizeY = getNewSize(sizeY, 0.02);

            try {
                wait(4);          // about 60 frames/sec
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


        }

        private void drawSize(Canvas c) {
            Paint paint = new Paint();

            paint.setAntiAlias(true);
            paint.setTextSize(8);
            paint.setColor(Color.DKGRAY);
            paint.setAlpha(255);


            c.drawText("X:" + sizeX + ", " + "Y:" + sizeY, 20, 20, paint);
        }

        private double getNewSize(double size, double scale) {
            return size + (size * scale);
        }

        private Rect getRectangleFromDimensions(int centerX, int centerY, double width, double height) {
            int left = (int) Math.floor(centerX - width / 2);
            int right = (int) Math.floor(centerX + width / 2);
            int top = (int) Math.floor(centerY - height / 2);
            int bottom = (int) Math.floor(centerY + height / 2);

            return new Rect(left, top, right, bottom);
        }
    }

    class WordTimingThread extends Thread {

        public void run() {
            try {
                sleep(5000);
                newWord = true;
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }


    private TextView textView;
    private Animation mainAnimation;
    private Handler messageHandler;
    private Runnable newWordTask;

    public WordsMainView(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        thread = new WordThread(getHolder());

        messageHandler = new Handler();

        newWordTask = new Runnable() {
            public void run() {
                newWord = true;
            }
        };
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
