package com.sevenheaven.gesturelock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;


/**
 * Created by caifangmao on 1/28/16.
 */
public class NormalStyleLockView extends GestureLockView {

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int mCenterX, mCenterY;
    private int mWidth, mHeight;

    private int mRadius;

    private static final int COLOR_NORMAL = 0xFFFFFFFF;
    private static final int COLOR_SELECTED = 0xFF0099CC;
    private static final int COLOR_ERROR = 0xFFFF0000;

    private static final int QUAD_ANGLE = 84;

    private float innerRate = 0.2F;
    private float outerWidthRate = 0.05F;
    private float middleWidthRate = 0.1F;
    private float innerWidthRate = 0.08F;
    private float outerRate = 0.91F;
    private float middleRate = 0.8F;
    private float arrowRate = 0.3F;
    private float arrowDistanceRate = 0.65F;
    private int arrowDistance;

    private RectF middleOval;

    private Path arrow;

    public NormalStyleLockView(Context context){
        this(context, null);
    }

    public NormalStyleLockView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public NormalStyleLockView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);

        arrow = new Path();
        middleOval = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;

        mRadius = mCenterX > mCenterY ? mCenterY : mCenterX;

        float r = mRadius * middleRate;
        middleOval.left = mCenterX - r;
        middleOval.right = mCenterX + r;
        middleOval.top = mCenterY - r;
        middleOval.bottom = mCenterY + r;

        arrowDistance = (int) (mRadius * arrowDistanceRate);

        int length = (int) (mRadius * arrowRate);
        arrow.reset();
        arrow.moveTo(mCenterX - length, mCenterY + length - arrowDistance);
        arrow.lineTo(mCenterX, mCenterY - arrowDistance);
        arrow.lineTo(mCenterX + length, mCenterY + length - arrowDistance);
        arrow.close();
    }


    @Override
    protected void doArrowDraw(Canvas canvas){
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(COLOR_ERROR);
        canvas.drawPath(arrow, mPaint);
    }

    @Override
    protected void doDraw(LockerState state, Canvas canvas){
        switch(state){
            case LOCKER_STATE_NORMAL:
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(COLOR_NORMAL);
                canvas.drawCircle(mCenterX, mCenterY, mRadius * innerRate, mPaint);
                break;
            case LOCKER_STATE_SELECTED:
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(COLOR_NORMAL);
                mPaint.setStrokeWidth(mRadius * outerWidthRate);
                canvas.drawCircle(mCenterX, mCenterY, mRadius * outerRate, mPaint);
                mPaint.setStrokeWidth(2);
                canvas.drawCircle(mCenterX, mCenterY, mRadius * innerRate, mPaint);
                break;
            case LOCKER_STATE_ERROR:
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(COLOR_ERROR);
                mPaint.setStrokeWidth(mRadius * outerWidthRate);
                canvas.drawCircle(mCenterX, mCenterY, mRadius * outerRate, mPaint);
                mPaint.setStrokeWidth(2);
                canvas.drawCircle(mCenterX, mCenterY, mRadius * innerRate, mPaint);
                break;
        }
    }

}
