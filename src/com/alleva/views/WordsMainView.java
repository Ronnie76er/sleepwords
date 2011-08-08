package com.alleva.views;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.Animation;
import android.widget.TextView;

import java.util.Random;

/**
 * User: ronnie
 * Date: 6/19/11
 */
public class WordsMainView extends SurfaceView implements SurfaceHolder.Callback {

    private Thread thread;

    private Boolean newWord = new Boolean(false);

    private int screenSizeX, screenSizeY;


    class WordThread extends Thread {

        private SurfaceHolder _surfaceHolder;
        private double sizeX = 10;
        private double sizeY = 10;
        private int xpos = 100;
        private int ypos = 200;
        private int timePerWordMillis = 5000;
        private Random random;

        public WordThread(SurfaceHolder surfaceHolder) {
            _surfaceHolder = surfaceHolder;

            random = new Random();
        }

        @Override
        public void run() {


            while (true) {


                xpos = random.nextInt(screenSizeX);
                ypos = random.nextInt(screenSizeY);

                Paint paint = new Paint();
                Paint clearPaint = new Paint();
                clearPaint.setColor(Color.BLACK);
                clearPaint.setStyle(Paint.Style.FILL);


                paint.setAntiAlias(true);
                paint.setTextSize(100);
                paint.setColor(Color.DKGRAY);
                paint.setAlpha(255);
                String text = "Some text";

                Rect bounds = new Rect();

                paint.getTextBounds(text, 0, text.length(), bounds);

                Bitmap bm = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888);
                Canvas testCanvas = new Canvas(bm);

                testCanvas.drawPaint(clearPaint);
                testCanvas.drawText(text, 0, bounds.height() - 1, paint);

                sizeY = 12;
                sizeX = sizeY * (bounds.width() / bounds.height());

                newWord = false;
                messageHandler.postDelayed(newWordTask, timePerWordMillis);
                int currentAlpha = 255;
                while (true) {
                    Canvas c = null;

                    try {

                        if (newWord) {
                            currentAlpha -= 30;
                            if (currentAlpha < 0) {
                                break;
                            }
                        }

                        c = _surfaceHolder.lockCanvas(null);
                        doDraw(c, bm, currentAlpha);


                    } finally {
                        if (c != null) {
                            _surfaceHolder.unlockCanvasAndPost(c);
                        }
                    }
                }
            }
        }

        private synchronized void doDraw(Canvas c, Bitmap bm, int alpha) {

            Paint clearPaint = new Paint();
            clearPaint.setColor(Color.BLACK);
            clearPaint.setStyle(Paint.Style.FILL);
            c.drawPaint(clearPaint);

            Paint bitmapPaint = new Paint();
            bitmapPaint.setAlpha(alpha);

            Rect rect = getRectangleFromDimensions(xpos, ypos, sizeX, sizeY);

            c.drawBitmap(bm, null, rect, bitmapPaint);

            sizeX = getNewSize(sizeX, 0.015);
            sizeY = getNewSize(sizeY, 0.015);

            try {
                wait(4);          // about 60 frames/sec
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


        }

        //for debugging purposes.
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

        Rect thing = surfaceHolder.getSurfaceFrame();

        setScreenSize(thing);

        thread.start();
    }

    public void setScreenSize(Rect rect) {
        screenSizeX = rect.width();
        screenSizeY = rect.height();
    }



    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
