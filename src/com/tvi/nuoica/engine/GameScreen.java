package com.tvi.nuoica.engine;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.tvi.nuoica.entities.Action;
import com.tvi.nuoica.entities.Tank;

public class GameScreen implements Action {

    public Tank tank;
    public int level;

    public GameScreen(int level) {
        tank = new Tank(level);
    }

    @Override
    public void update() {
        tank.update();
    }

    @Override
    public boolean touch(MotionEvent event) {
        return tank.touch(event);
    }

    @Override
    public void render(Canvas canvas) {
        tank.render(canvas);
    }

}
