package com.tvi.nuoica.entities;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.tvi.nuoica.GameActivity;
import com.tvi.nuoica.engine.GameBitmap;
import com.tvi.nuoica.engine.GameSound;
import com.tvi.nuoica.engine.GameView;

public class Shell extends Actor {

    public static final float SPEED = 2f;
    public static final int[] VALUES = { 40, 60, 80, 100 };

    public int value;
    public boolean falling;

    public Shell(int level, float x, float y) {
        this.state = level % VALUES.length;
        this.x = x;
        this.y = y;
        dx = 0;
        dy = SPEED;
        value = VALUES[state];
        alive = true;
        width = 32;
        height = 32;
        dx = 0;
        dy = SPEED;
        index = INDEX_MIN;
        falling = true;
    }

    public void collect() {
        GameActivity.mGameSound.playSound(GameSound.money_0 + state);
        dy = -5 * SPEED;
        dx = dy / y * (x - Tank.WIDTH / 2);
        falling = false;
    }

    @Override
    public void update() {
        index = ++index % SHEET_COLUMNS;
        if (y >= Tank.HEIGHT - height / 2 || y <= height / 2) {
            alive = false;
            if (y >= Tank.HEIGHT - height / 2) {
                value = 0;
            }
        }
        updateBody();
    }

    @Override
    public boolean touch(MotionEvent event) {
        if (falling) {
            float ex = event.getX() * Tank.WIDTH / GameView.width;
            float ey = event.getY() * Tank.HEIGHT / GameView.height;
            if (dst.contains(ex, ey)) {
                collect();
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(Canvas canvas) {
        renderBody(canvas, GameBitmap.SHELL);
    }

}
