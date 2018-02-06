package com.example.anhkhoa.ble_car_controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by Anh Khoa on 2/1/2018.
 */

public class JoystickView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    private float centerX;
    private float centerY;
    private float baseRadius;
    private float hatRadius;
    private float displacement;
    private Bitmap joystick_bg;
    private Bitmap Joystick_Controller;
    private JoystickListener joystickcallback;
    private Paint colors = new Paint();
    Canvas myCanvas = null;

    private final int ratio = 5;

    public JoystickView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (context instanceof JoystickListener) {
            joystickcallback = (JoystickListener) context;
        }
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (context instanceof JoystickListener)
            joystickcallback = (JoystickListener) context;
    }

    public JoystickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if (context instanceof JoystickListener)
            joystickcallback = (JoystickListener) context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public JoystickView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        setupDimension();
        //canvas.drawColor(Color.WHITE);
        joystick_bg = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        Joystick_Controller = BitmapFactory.decodeResource(getResources(), R.drawable.joystick_controller);
        //drawBG();
        drawJoystick(centerX, centerY);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onDraw(Canvas canvas) {
//        try {
//            canvas = getHolder().lockCanvas(null);
//            canvas.drawColor(Color.WHITE);
//            joystick_bg = BitmapFactory.decodeResource(getResources(), R.drawable.joystick_bg);
//            canvas.drawBitmap(joystick_bg, centerX, centerY, new Paint());
//        }
//        finally {
//            if(canvas != null){
//                getHolder().unlockCanvasAndPost(canvas);
//            }
//        }
    }

    private void setupDimension() {
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        baseRadius = (Math.min(getWidth(), getHeight()) / 3) - 100;
        hatRadius = Math.min(getWidth(), getHeight()) / 5;
    }

    private void drawJoystick(float newX, float newY) {
        invalidate();
        SurfaceHolder holder = getHolder();
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            Paint paint = new Paint();
            float halfwidthbg = joystick_bg.getWidth();
            float halfwidthcontrol = Joystick_Controller.getWidth();
            float halfheightbg = joystick_bg.getHeight();
            float halfheightcontrol = Joystick_Controller.getHeight();
            //canvas.drawBitmap(joystick_bg, null, new RectF(centerX - halfwidthbg, centerY - halfheightbg, centerX + halfwidthbg, centerY + halfheightbg), paint);
            //canvas.drawBitmap(Joystick_Controller, null, new RectF(centerX - halfwidthcontrol, centerY - halfheightcontrol, centerX + halfwidthcontrol, centerY + halfheightcontrol), paint);
            canvas.drawBitmap(joystick_bg, null, new RectF(centerX - halfwidthbg, centerY - halfheightbg, centerX + halfwidthbg, centerY + halfheightbg), paint);
            canvas.drawBitmap(Joystick_Controller, null, new RectF(newX - halfwidthcontrol, newY - halfheightcontrol, newX + halfwidthcontrol, newY + halfheightcontrol), paint);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawBG() {
        invalidate();
        SurfaceHolder holder = getHolder();
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            Paint paint = new Paint();
            //joystick_bg = BitmapFactory.decodeResource(getResources(), R.drawable.joystick_bg);
            float halfwidthbg = joystick_bg.getWidth() / 2;
            //float halfwidthcontrol = Joystick_Controller.getWidth() / 2;
            float halfheightbg = joystick_bg.getHeight() / 2;
            //float halfheightcontrol = Joystick_Controller.getHeight() / 2;
            canvas.drawBitmap(joystick_bg, null, new RectF(centerX - halfwidthbg, centerY - halfheightbg, centerX + halfwidthbg, centerY + halfheightbg), paint);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.equals(this)) {
            if (motionEvent.getAction() != motionEvent.ACTION_UP) {
                displacement = (float) Math.sqrt((Math.pow(motionEvent.getX() - centerX, 2)) + Math.pow(motionEvent.getY() - centerY, 2));
                if (displacement < baseRadius) {
                    drawJoystick(motionEvent.getX(), motionEvent.getY());
                    joystickcallback.onJoystickMoved((motionEvent.getX() - centerX) / baseRadius, (motionEvent.getY() - centerY) / baseRadius, getId());
                } else {
                    float ratio = baseRadius / displacement;
                    float constrainedX = centerX + (motionEvent.getX() - centerX) * ratio;
                    float constrainedY = centerY + (motionEvent.getY() - centerY) * ratio;
                    drawJoystick(constrainedX, constrainedY);
                    joystickcallback.onJoystickMoved((constrainedX - centerX) / baseRadius, (constrainedY - centerY) / baseRadius, getId());
                }
            } else {
                drawJoystick(centerX, centerY);
                joystickcallback.onJoystickMoved(0, 0, getId());
            }
        }
        return true;
    }

    public interface JoystickListener {
        void onJoystickMoved(float xPercent, float yPercent, int id);
    }
}
