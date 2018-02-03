package com.mcs.th.forge.draganddraw;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BoxDrawingView extends View {

    private static final String TAG = "BoxDrawingView";
    private static final String EXTRA_BOXEN = "SAVED_BOXEN";
    private static final String EXTRA_STATE = "BOX_DRAWING_VIEW_STATE";

    private Box mCurrentBox;
    private List<Box> mBoxen = new ArrayList<>();
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;

    private int firstPointer, secondPointer;
    private float fx, fy, sx, sy, nfx, nfy, nsx, nsy;


    public BoxDrawingView(Context context) {
        this(context, null);
    }

    public BoxDrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(mBackgroundPaint);
        for (Box box : mBoxen) {
            float angle = box.getAngle();
            float px = (box.getOrigin().x + box.getCurrent().x) / 2;
            float py = (box.getOrigin().y + box.getCurrent().y) / 2;
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);
            canvas.save();
            canvas.rotate(angle, px, py);
            canvas.drawRect(left, top, right, bottom, mBoxPaint);
            canvas.restore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF();

        int currentIndex = event.getActionIndex();
        if (event.getActionIndex() == 0) {
            current.set(event.getX(), event.getY());
        }
        String action = "";
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                firstPointer = event.getPointerId(currentIndex);
                mCurrentBox = new Box(current);
                mBoxen.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                action = "ACTION_POINTER_DOWN";
                secondPointer = event.getPointerId(currentIndex);
                fx = event.getX(event.findPointerIndex(firstPointer));
                fy = event.getY(event.findPointerIndex(firstPointer));
                sx = event.getX(event.findPointerIndex(secondPointer));
                sy = event.getY(event.findPointerIndex(secondPointer));
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                if (firstPointer == 0 && secondPointer != 0) {
                    nfx = event.getX(event.findPointerIndex(firstPointer));
                    nfy = event.getY(event.findPointerIndex(firstPointer));
                    nsx = event.getX(event.findPointerIndex(secondPointer));
                    nsy = event.getY(event.findPointerIndex(secondPointer));
                    mCurrentBox.setAngle(angleBetweenLines(fx, fy, sx, sy, nfx, nfy, nsx, nsy));
                }
                if (mCurrentBox != null) {
                    mCurrentBox.setCurrent(current);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                action = "ACTION_POINTER_UP";
                secondPointer = 0;
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                mCurrentBox = null;
                firstPointer = 0;
                secondPointer = 0;
                break;
        }
        Log.i(TAG, action + " at x=" + current.x + ", y=" + current.y);
        return true;
    }

    private float angleBetweenLines(float fx, float fy, float sx, float sy, float nfx, float nfy, float nsx, float nsy) {
        float angle1 = (float) Math.atan2(sy - fy, sx - fx);
        float angle2 = (float) Math.atan2(nsy - nfy, nsx - nfx);

        float angle = (float) (Math.toDegrees(angle2 - angle1) % 360);
        if (angle < 0f) {
            angle += 360f;
        }
        return angle;
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Log.d(TAG, "SaveInstanceState");
        Bundle bundle = new Bundle();
        MySerializable boxenToSave = new MySerializable(mBoxen);
        bundle.putSerializable(EXTRA_BOXEN, boxenToSave);
        bundle.putParcelable(EXTRA_STATE, super.onSaveInstanceState());
        super.onSaveInstanceState();
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.d(TAG, "onRestore called");
        if (state != null) {
            Bundle bundle = (Bundle) state;
            Log.d(TAG, "mBoxen: " + mBoxen);
            Log.d(TAG, "onRestore Boxen");
            MySerializable boxenToRestore = (MySerializable) bundle.getSerializable(EXTRA_BOXEN);
            mBoxen = boxenToRestore.getBoxen();
            super.onRestoreInstanceState(bundle.getParcelable(EXTRA_STATE));
        }
    }

    private class MySerializable implements Serializable {
        private List<Box> Boxen = new ArrayList<>();

        public MySerializable(List<Box> boxen) {
            Boxen = boxen;
        }

        public List<Box> getBoxen() {
            return this.Boxen;
        }
    }
}
