package com.mcs.th.forge.draganddraw;


import android.graphics.PointF;

public class Box {
    private PointF mOrigin;
    private PointF mCurrent;
    private float mAngle;

    public Box(PointF origin) {
        mOrigin = origin;
        mCurrent = origin;
    }

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

    public PointF getOrigin() {
        return mOrigin;
    }

    public PointF getCurrent() {

        return mCurrent;
    }

    public void setCurrent(PointF current) {
        mCurrent = current;
    }
}
