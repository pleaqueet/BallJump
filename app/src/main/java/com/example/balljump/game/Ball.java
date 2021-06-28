package com.example.balljump.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.balljump.R;

public class Ball {
    Bitmap ball;

    // which ways can the ball move
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;
    public final int UP = 3;
    public final int DOWN = 4;

    // coordinates
    public float x;
    public float y;

    // ball moving
    private int ballMoving = STOPPED;
    private int ballMovingVertical = STOPPED;
    private float ballSpeed;

    public Ball(Context context, int screenX, int screenY) {
        ball = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball);

        x = screenX/2 - (ball.getWidth()/2);
        y = screenY/2 - (ball.getHeight()/2);

        ballSpeed = 450;
    }

    public Bitmap getBall() {
        return ball;
    }

    public void setMovementState(int state){
        ballMoving = state;
    }

    public int getMovementVerticalState() {
        return ballMovingVertical;
    }

    public void setMovementVerticalState(int state){
        ballMovingVertical = state;
    }

    public void update(long fps) {
        if (ballMovingVertical == DOWN) {
            y = y + ballSpeed / fps;
        }

        if (ballMovingVertical == UP) {
            y = y - ballSpeed / fps;
        }

        if(ballMoving == LEFT){
            x = x - ballSpeed / fps;
        }

        if(ballMoving == RIGHT){
            x = x + ballSpeed / fps;
        }
    }
}
