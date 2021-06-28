package com.example.balljump.fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;

import com.example.balljump.game.GameView;

public class GameFragment extends Fragment {
    public int screenX;
    public int screenY;
    GameView gameView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenX = size.x;
        screenY = size.y;

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        gameView = new GameView(getContext(), screenX, screenY);

        return gameView;
    }

    @Override
    public void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        gameView.pause();
    }
}