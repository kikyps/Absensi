package com.kp.absensi;

import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public abstract class MyLongClickListener implements View.OnTouchListener {

    private final int delay;
    private boolean down;
    private final Runnable callback;
    private final RectF mRect = new RectF();

    public MyLongClickListener(int delay) {
        this.delay = delay;
        this.callback = () -> {
            down = false;
            onLongClick();
        };
    }

    public abstract void onLongClick();

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        if (e.getPointerCount() > 1) return true;

        int action = e.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            down = true;
            mRect.set(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
            v.postDelayed(callback, delay);
            return false;

        } else if (action == MotionEvent.ACTION_MOVE) {
            if (down && !mRect.contains(v.getLeft() + e.getX(), v.getTop() + e.getY())) {
                v.removeCallbacks(callback);
                down = false;
                return false;
            }

        } else if (action == MotionEvent.ACTION_UP) {
            if (down) {
                v.removeCallbacks(callback);
                v.performClick();
            }
            return true;

        } else if (action == MotionEvent.ACTION_CANCEL) {
            v.removeCallbacks(callback);
            down = false;
        }

        return false;
    }

}
