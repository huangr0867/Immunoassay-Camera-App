package com.example.newcircle;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.util.FloatMath;

import static com.example.newcircle.MainActivity.TAG;

public class BaseDragZoomImageView extends androidx.appcompat.widget.AppCompatImageView implements View.OnTouchListener{


    private int mode = 0;
 
    private static final int MODE_DRAG = 1;

    private static final int MODE_ZOOM = 2;

    private PointF startPoint = new PointF();

    private Matrix matrix = new Matrix();

    public Matrix currentMatrix = new Matrix();

    private float startDis;

    private PointF midPoint;


    public float nowX;
    public float nowY;

    public BaseDragZoomImageView(Context context, AttributeSet attrs, int defStyle)
    {

        super(context, attrs, defStyle);

        setOnTouchListener(this);
    }
    public BaseDragZoomImageView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
        setOnTouchListener(this);
    }
    public BaseDragZoomImageView(Context context)
    {
        this(context, null);
        setOnTouchListener(this);
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                mode = MODE_DRAG;

                currentMatrix.set(getImageMatrix());
                startPoint.set(event.getX(), event.getY());

                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == MODE_DRAG) {
                    float dx = event.getX() - startPoint.x;
                    float dy = event.getY() - startPoint.y;

                    matrix.set(currentMatrix);
                    matrix.postTranslate(dx, dy);
                    nowX = dx;
                    nowY = dy;
                    Log.d(TAG, "onTouch:------ "+dx+"//"+dy);
                }

                else if (mode == MODE_ZOOM) {
                    float endDis = distance(event);
                    if (endDis > 10f) {
                        float scale = endDis / startDis;
                        matrix.set(currentMatrix);
                        matrix.postScale(scale, scale,midPoint.x,midPoint.y);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:

            case MotionEvent.ACTION_POINTER_UP:


                mode = 0;
                break;
                
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = MODE_ZOOM;

                startDis = distance(event);

                if (startDis > 10f) {
                    midPoint = mid(event);

                    currentMatrix.set(getImageMatrix());
                }
                break;
        }
        setImageMatrix(matrix);
        return true;
    }

    private float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);

        return (float) Math.sqrt(dx * dx + dy * dy);
    }


    private PointF mid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }
}
