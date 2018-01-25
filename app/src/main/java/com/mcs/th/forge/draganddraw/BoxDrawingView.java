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
        Log.d(TAG, "onDraw");
        canvas.drawPaint(mBackgroundPaint);
        for (Box box : mBoxen) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);
            canvas.drawRect(left, top, right, bottom, mBoxPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());
        String action = "";
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                mCurrentBox = new Box(current);
                mBoxen.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                if (mCurrentBox != null) {
                    mCurrentBox.setCurrent(current);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                mCurrentBox = null;
                break;
        }

        Log.i(TAG, action + " at x=" + current.x + ", y=" + current.y);
        return true;
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
