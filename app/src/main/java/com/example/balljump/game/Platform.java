package com.example.balljump.game;

import android.graphics.RectF;

public class Platform {
    private RectF rectPlatform;

    public float x;
    public float y;

    private float length;
    private float height;

    private float paddleSpeed;

    public final int STOPPED = 0;
    public final int DOWN = 1;

    private int paddleMoving = STOPPED;

    public Platform(int x, int y) {
        length = 130;
        height = 20;

        this.x = x;
        this.y = y;

        rectPlatform = new RectF(x, y, x + length, y + height);

        paddleSpeed = 1200;
    }

    public void up() {
        rectPlatform.bottom += 300;
        //rectPlatform.right += 300;
        rectPlatform.top += 300;
        //rectPlatform.left += 300;
    }

    public void setMovementState(int state){
        paddleMoving = state;
    }

    public void update(long fps){
        if(paddleMoving == DOWN){
            y = y + paddleSpeed / fps;
        }
        rectPlatform.right = x + length;
        rectPlatform.left = x;
        rectPlatform.top = y;
        rectPlatform.bottom = y + height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public RectF getRectPlatform() {
        return rectPlatform;
    }
}
