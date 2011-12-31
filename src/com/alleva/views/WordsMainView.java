/*
 * Copyright (c) 2011, Ron Alleva
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *  Neither the name of the Ron Alleva nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.alleva.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.*;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.Animation;
import android.widget.TextView;
import com.alleva.R;

import java.security.PublicKey;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * User: ronnie
 * Date: 6/19/11
 */
public class WordsMainView extends SurfaceView implements SurfaceHolder.Callback, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final int STARTING_HEIGHT = 25;
    private Thread thread;

    private final int RANDOM_POSITION = 0;
    private final int CENTER_POSITION = 1;

    private final double FAST = 0.015;
    private final double MEDIUM = 0.01;
    private final double SLOW = 0.008;

    private Boolean newWord = new Boolean(false);
    private Boolean sleep = new Boolean(false);

    private int screenSizeX, screenSizeY;
    private int timePerWordMillis;
    private String[] nouns;
    private int nounsLength;
    private int wordPosition = RANDOM_POSITION;
    private double wordSpeed;


    class WordThread extends Thread {

        private SurfaceHolder _surfaceHolder;
        private double sizeX = 10;
        private double sizeY = 10;
        private int xpos = 100;
        private int ypos = 200;
        private Random random;
        private volatile boolean threadSuspended;


        public WordThread(SurfaceHolder surfaceHolder) {
            _surfaceHolder = surfaceHolder;

            Resources res = getResources();
            nouns = res.getStringArray(R.array.nouns);
            nounsLength = nouns.length;

            random = new Random();
        }

        @Override
        public void run() {


            while (true) {

                if (sleep) {
                    break;
                }

                try {

                    synchronized (this) {
                        if (threadSuspended) {
                            wait();
                        }
                    }

                    if (wordPosition == RANDOM_POSITION) {
                        xpos = random.nextInt(screenSizeX - 80) + 40;
                        ypos = random.nextInt(screenSizeY - 50) + 25;
                    } else {
                        xpos = screenSizeX / 2;
                        ypos = screenSizeY / 2;
                    }

                    Paint paint = new Paint();
                    Paint clearPaint = new Paint();
                    clearPaint.setColor(Color.BLACK);
                    clearPaint.setStyle(Paint.Style.FILL);


                    paint.setAntiAlias(true);
                    paint.setTextSize(100);
                    paint.setColor(Color.DKGRAY);
                    paint.setAlpha(255);
                    String text = nouns[random.nextInt(nounsLength)];

                    Rect bounds = new Rect();

                    paint.getTextBounds(text, 0, text.length(), bounds);

                    Bitmap bm = Bitmap.createBitmap(bounds.width() + 10, bounds.height() + 30, Bitmap.Config.ARGB_8888);
                    Canvas testCanvas = new Canvas(bm);

                    testCanvas.drawPaint(clearPaint);
                    testCanvas.drawText(text, 0, bounds.height() - 1, paint);

                    sizeY = STARTING_HEIGHT;
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
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }

        public synchronized void suspendThread() {
            threadSuspended = true;
        }

        public synchronized void resumeThread() {
            threadSuspended = false;
            notify();
        }

        private synchronized void doDraw(Canvas c, Bitmap bm, int alpha) {

            if (c == null) {
                try {
                    wait(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                return;
            }

            Paint clearPaint = new Paint();
            clearPaint.setColor(Color.BLACK);
            clearPaint.setStyle(Paint.Style.FILL);
            c.drawPaint(clearPaint);

            Paint bitmapPaint = new Paint();
            bitmapPaint.setAlpha(alpha);

            Rect rect = getRectangleFromDimensions(xpos, ypos, sizeX, sizeY);

            c.drawBitmap(bm, null, rect, bitmapPaint);

            sizeX = getNewSize(sizeX, wordSpeed);
            sizeY = getNewSize(sizeY, wordSpeed);

            try {
                wait(16);          // about 60 frames/sec
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

    private Handler messageHandler;
    private Runnable newWordTask;

    public WordsMainView(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        thread = new WordThread(getHolder());

        messageHandler = new Handler();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);

        timePerWordMillis = getWordTimer(prefs);
        wordPosition = getWordPosition(prefs);
        wordSpeed = getWordSpeed(prefs);

        newWordTask = new Runnable() {
            public void run() {
                newWord = true;
            }
        };
    }

    private int getWordTimer(SharedPreferences prefs) {
        return Integer.parseInt(prefs.getString("wordTimer", "5")) * 1000;
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("wordTimer"))
            timePerWordMillis = getWordTimer(sharedPreferences);

        if (s.equals("wordPosition"))
            wordPosition = getWordPosition(sharedPreferences);
        
        if(s.equals("wordSpeed"))
            wordSpeed =  getWordSpeed(sharedPreferences);

    }

    private double getWordSpeed(SharedPreferences sharedPreferences) {
        String speedString = sharedPreferences.getString("wordSpeed", "slow");
        double speed;

        if(speedString.equals("fast"))
            speed = FAST;
        else if(speedString.equals("medium"))
            speed = MEDIUM;
        else
            speed = SLOW;

        return speed;
    }

    private int getWordPosition(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString("wordPosition", "random").equals("random") ? RANDOM_POSITION : CENTER_POSITION;
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        Rect thing = surfaceHolder.getSurfaceFrame();

        setScreenSize(thing);

        if (thread.isAlive()) {
            ((WordThread) thread).resumeThread();
        } else {
            thread.start();

        }
    }

    public void setScreenSize(Rect rect) {
        screenSizeX = rect.width();
        screenSizeY = rect.height();
    }


    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        ((WordThread) thread).suspendThread();
    }


}
