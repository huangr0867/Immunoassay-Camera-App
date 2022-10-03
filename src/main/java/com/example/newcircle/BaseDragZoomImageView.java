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

    /** 記錄是拖拉照片模式還是放大縮小照片模式 */
    private int mode = 0;// 初始狀態
    /** 拖拉照片模式 */
    private static final int MODE_DRAG = 1;
    /** 放大縮小照片模式 */
    private static final int MODE_ZOOM = 2;
    /** 用於記錄開始時候的座標位置 */
    private PointF startPoint = new PointF();
    /** 用於記錄拖拉圖片移動的座標位置 */
    private Matrix matrix = new Matrix();
    /** 用於記錄圖片要進行拖拉時候的座標位置 */
    public Matrix currentMatrix = new Matrix();
    /** 兩個手指的開始距離 */
    private float startDis;
    /** 兩個手指的中間點 */
    private PointF midPoint;

    /***取得圖片現在真實座標***/
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
                /** 通過與運算保留最後八位 MotionEvent.ACTION_MASK = 255 */
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    // 手指壓下螢幕
            case MotionEvent.ACTION_DOWN:
                mode = MODE_DRAG;
                    // 記錄ImageView當前的移動位置
                currentMatrix.set(getImageMatrix());
                startPoint.set(event.getX(), event.getY());

                break;
                // 手指在螢幕上移動，改事件會被不斷觸發
            case MotionEvent.ACTION_MOVE:
                    // 拖拉圖片
                if (mode == MODE_DRAG) {
                    float dx = event.getX() - startPoint.x; // 得到x軸的移動距離
                    float dy = event.getY() - startPoint.y; // 得到x軸的移動距離
                        // 在沒有移動之前的位置上進行移動
                    matrix.set(currentMatrix);
                    matrix.postTranslate(dx, dy);
                    nowX = dx;
                    nowY = dy;
                    Log.d(TAG, "onTouch:------ "+dx+"//"+dy);
                }
                    // 放大縮小圖片
                else if (mode == MODE_ZOOM) {
                    float endDis = distance(event);// 結束距離
                    if (endDis > 10f) { // 兩個手指併攏在一起的時候畫素大於10
                        float scale = endDis / startDis;// 得到縮放倍數
                        matrix.set(currentMatrix);
                        matrix.postScale(scale, scale,midPoint.x,midPoint.y);
                    }
                }
                break;
                // 手指離開螢幕
            case MotionEvent.ACTION_UP:
                // 當觸點離開螢幕，但是螢幕上還有觸點(手指)
            case MotionEvent.ACTION_POINTER_UP:


                mode = 0;
                break;
                // 當螢幕上已經有觸點(手指)，再有一個觸點壓下螢幕
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = MODE_ZOOM;
                    /** 計算兩個手指間的距離 */
                startDis = distance(event);
                    /** 計算兩個手指間的中間點 */
                if (startDis > 10f) { // 兩個手指併攏在一起的時候畫素大於10
                    midPoint = mid(event);
                    //記錄當前ImageView的縮放倍數
                    currentMatrix.set(getImageMatrix());
                }
                break;
        }
        setImageMatrix(matrix);
        return true;
    }
    /** 計算兩個手指間的距離 */
    private float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        /** 使用勾股定理返回两点之间的距离 */
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /** 计算两个手指间的中间点 */
    private PointF mid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }
}