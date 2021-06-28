package com.example.balljump.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {
    Context context;

    // thread variables
    volatile boolean playing;
    boolean paused = true;
    Thread gameThread = null;

    // coordinates
    int screenX;
    int centerScreenX;
    int screenY;
    int centerScreenY;
    int upY;
    int xMax;
    int yMax;
    Random random;

    // screen variables
    Canvas canvas;
    Paint paint;
    Paint strokePaint;
    SurfaceHolder holder; // holder paints on screen

    Ball ball;
    RectF ballCollider;
    List<Platform> platforms;

    // score
    private int playerScore;
    private int bestScore;

    long fps;

    public GameView(Context context, int screenX, int screenY) {
        super(context);
        this.screenX = screenX;
        this.screenY = screenY;
        this.context = context;
        centerScreenX = screenX / 2;
        centerScreenY = screenY / 2;

        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("score", 0);
        bestScore = sp.getInt("score", 0);

        // Initialize holder and paint objects
        holder = getHolder();
        paint = new Paint();
        paint.setTextSize(50);
        paint.setStrokeWidth(5);
        strokePaint = new Paint();
        strokePaint.setStrokeWidth(5);
        strokePaint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(255, 255, 255, 255));
        strokePaint.setColor(Color.argb(255, 255, 255, 255));

        ball = new Ball(context, screenX, screenY);
        platforms = new ArrayList<>();
        ballCollider = new RectF();

        random = new Random();
        xMax = screenX - 130;
        yMax = screenY - 20;
        upY = yMax - 50;

        while (true) {
            int xPlatform = random.nextInt(xMax);
            if (upY <= 0) {
                break;
            }
            Platform platform = new Platform(xPlatform, upY);
            upY -= 400;
            platforms.add(platform);
        }
        ball.setMovementVerticalState(ball.DOWN);
        ball.y = platforms.get(0).getY() - ball.getBall().getHeight();
        ball.x = platforms.get(0).getX();

        for (Platform platform : platforms) {
            platform.setMovementState(platform.STOPPED);
        }
    }

    @Override
    public void run() {
        while (playing) {
            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            if (!paused) {
                update();
            }

            // Draw the frame
            draw();

            // Calculate the fps this frame
            long timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    private void draw() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();

            // Draw the background
            int margin = 50;
            canvas.drawColor(Color.argb(255, 1, 128, 1)); // color background
            canvas.drawCircle(centerScreenX, centerScreenY, 10, paint);
            canvas.drawCircle(centerScreenX, centerScreenY, 100, strokePaint);
            canvas.drawLine(margin, margin, screenX - margin, margin, paint);
            canvas.drawLine(margin, margin, margin, screenY - margin, paint);
            canvas.drawLine(margin, screenY - margin, screenX - margin, screenY - margin, paint);
            canvas.drawLine(screenX - margin, margin, screenX - margin, screenY - margin, paint);
            canvas.drawLine(margin, centerScreenY, screenX - margin, centerScreenY, paint);

            // Draw objects
            canvas.drawBitmap(ball.getBall(), ball.x, ball.y, paint); // ball
            //draw platforms
            for (int i = 0; i < platforms.size(); i++) {
                canvas.drawRect(platforms.get(i).getRectPlatform(), paint);
            }

            // Draw score
            canvas.drawText("Ваш счёт: " + playerScore, 100, 100, paint);
            canvas.drawText("Лучший счёт: " + bestScore, 100, 170, paint);

            holder.unlockCanvasAndPost(canvas);
        }
    }

    // updates by fps
    private void update() {
        // ball update
        ball.update(fps);

        // ball collider update
        ballCollider.left = ball.x;
        ballCollider.top = ball.y;
        ballCollider.right = ball.x + ball.getBall().getWidth();
        ballCollider.bottom = ball.y + ball.getBall().getHeight();

        for (Platform platform : platforms) {
            platform.update(fps);
            if (platform.getY() >= screenY) {
                int xPlatform = random.nextInt(xMax);
                platform.setY(upY);
                platform.setX(xPlatform);
                for (Platform platform1 : platforms) {
                    platform1.setMovementState(platform.STOPPED);
                }
            }
            if (ball.getMovementVerticalState() == ball.DOWN &&
                    RectF.intersects(ballCollider, platform.getRectPlatform())) {
                playerScore += 1;
                ball.setMovementVerticalState(ball.UP);
                for (Platform platform1 : platforms) {
                    platform1.setMovementState(platform.DOWN);
                }
            }
        }
        if (ball.y <= centerScreenY - 200) {
            ball.setMovementVerticalState(ball.DOWN);
        }
        if (ball.x > screenX) {
            ball.x = -(ball.getBall().getWidth() / 2);
        }
        if (ball.x < 0 - (ball.getBall().getWidth() / 2)) {
            ball.x = screenX - (ball.getBall().getWidth() / 2);
        }

        if (ball.y >= screenY) {
            if (bestScore <= playerScore) {
                bestScore = playerScore;
            }
            SharedPreferences sp = context.getApplicationContext().getSharedPreferences("score", 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("score", bestScore);
            editor.apply();
            playerScore = 0;
            restart();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            //Player has touched the screen
            case MotionEvent.ACTION_DOWN:
                paused = false;

                if (event.getX() > centerScreenX) {
                    ball.setMovementState(ball.RIGHT);
                } else {
                    ball.setMovementState(ball.LEFT);
                }

                break;
            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:
                ball.setMovementState(ball.STOPPED);
                break;
        }
        return true;
    }

    // If SimpleGameEngine Activity is paused/stopped
    // shutdown our thread.
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }

    // restart game
    private void restart() {
        platforms.clear();
        ball = new Ball(context, screenX, screenY);
        upY = yMax - 50;

        while (true) {
            int xPlatform = random.nextInt(xMax);
            if (upY <= 0) {
                break;
            }
            Platform platform = new Platform(xPlatform, upY);
            upY -= 400;
            platforms.add(platform);
        }

        ball.setMovementVerticalState(ball.DOWN);
        ball.y = platforms.get(0).getY() - ball.getBall().getHeight();
        ball.x = platforms.get(0).getX();

        paused = true;
    }

    // If SimpleGameEngine Activity is started then
    // start our thread.
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}