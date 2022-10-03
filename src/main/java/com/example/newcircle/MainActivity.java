package com.example.newcircle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import  com.example.newcircle.MyDataView;

import static android.view.View.INVISIBLE;

public class MainActivity extends AppCompatActivity {
    public static  final String TAG = MainActivity.class.getSimpleName()+"My";

    private String mPath = "";//設置高畫質的照片位址
    public static final int CAMERA_PERMISSION = 100;//檢測相機權限用
    public static final int REQUEST_HIGH_IMAGE = 101;//檢測高畫質相機回傳

    DisplayMetrics metric = new DisplayMetrics();

    int width ;     // 螢幕寬度（畫素）
    int height ;   // 螢幕高度（畫素）
    float density ;      // 螢幕密度（0.75 / 1.0 / 1.5）
    int densityDpi ;  // 螢幕密度DPI（120 / 160 / 240）
    Button camerabtn;
    Button colorbutton;
    ImageView imageHigh;
    ImageView circleimg;

    float circleimgX;
    float circleimgY;
    float circleimgW;
    float circleimgH;


    int getcolor1,getcolor2,getcolor3,getcolor4,getcolor5,getcolor6,getcolor7,getcolor8 ;
    int total;
    int ifadd = 0;
    private float[] points;

    private ConstraintLayout   mylayout;
    private MyDataView myDataView;
    private HashMap<String , Float> dataDegee = new HashMap<>();
    private HashMap<String , String> dataColor = new HashMap<>();




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        camerabtn = findViewById(R.id.camerabutton);
        colorbutton = findViewById(R.id.colorbutton);
        imageHigh = findViewById(R.id.imageView);
        circleimg = findViewById(R.id.imageView2);
        mylayout = findViewById(R.id.layout);

        mylayout.setOnTouchListener(new TouchListener());
        circleimg.setVisibility(INVISIBLE);
        colorbutton.setVisibility(INVISIBLE);

        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;     // 螢幕寬度（畫素）
        height = metric.heightPixels;   // 螢幕高度（畫素）
        density = metric.density;      // 螢幕密度（0.75 / 1.0 / 1.5）
        densityDpi = metric.densityDpi;

        Log.d(TAG, "onCreate:------------------------ "+width+"/"+height);


        /*取得相機權限*/
        if (checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION);


        camerabtn.setOnClickListener(v->{
            Intent highIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //檢查是否已取得權限
            if (highIntent.resolveActivity(getPackageManager()) == null) return;
            //取得相片檔案的URI位址及設定檔案名稱
            File imageFile = getImageFile();
            if (imageFile == null) return;
            //取得相片檔案的URI位址
            Uri imageUri = FileProvider.getUriForFile(
                    this,
                    "com.example.newcolorcircle.camera",//記得要跟AndroidManifest.xml中的authorities 一致
                    imageFile
            );
            highIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
            startActivityForResult(highIntent,REQUEST_HIGH_IMAGE);//開啟相機
            if(ifadd == 1)
            {
                mylayout.removeView(myDataView);
            }
        });


        colorbutton.setOnClickListener(new Button.OnClickListener(){

            @Override

            public void onClick(View v) {

                circleimgX = circleimg.getX();
                circleimgY = circleimg.getY();
                circleimgW = circleimg.getWidth();
                circleimgH = circleimg.getHeight();

                Bitmap image = ((BitmapDrawable)imageHigh.getDrawable()).getBitmap();
                Log.d(TAG, "onClick: "+image.getHeight());

               //第一個圓
                getcolor1 = getcolor(image,circleimgX+circleimgW/4+circleimgW/15,circleimgY+circleimgH/5);
                //第二個圓
                getcolor2 = getcolor(image,circleimgX+circleimgW/5+5,circleimgY+circleimgH/5+circleimgH/4-5);
                //第三個圓
                getcolor3 = getcolor(image,circleimgX+circleimgW/5+10,circleimgY+circleimgH/5+circleimgH/2);
                //第四個圓
                getcolor4 = getcolor(image,circleimgX+circleimgW/3+circleimgW/20,circleimgY+circleimgH/3+circleimgH/2+circleimgH/30);
                //第五個圓
                getcolor5 = getcolor(image,circleimgX+circleimgW/2+circleimgW/8,circleimgY+circleimgH/3+circleimgH/2+circleimgH/25);
                //第六個圓
                getcolor6 = getcolor(image,circleimgX+circleimgW/2+circleimgW/4+circleimgW/30,circleimgY+circleimgH/5+circleimgH/2);
                //第七個圓
                getcolor7 = getcolor(image,circleimgX+circleimgW/2+circleimgW/5+circleimgW/12,circleimgY+circleimgH/5+circleimgH/5+5);
                //第八個圓
                getcolor8 = getcolor(image,circleimgX+circleimgW/5+circleimgW/2,circleimgY+circleimgH/5);

                total = getcolor1+getcolor2+getcolor3+getcolor4+getcolor5+getcolor6+getcolor7+getcolor8;
                ifadd = 1;
                tatalcircle();
                colorbutton.setVisibility(INVISIBLE);
               // test();
            }

        });
        setlayout();
    }
    public void test()
    {
        Log.d(TAG, "test: -------");
        final DrawView view2 = new DrawView(this);
        mylayout.addView(view2);
    }

    public  void setlayout()
    {
        camerabtn.setWidth(width/4);
        camerabtn.setHeight(height/9);
        camerabtn.setX(width/2-width/3);
        camerabtn.setY(height-height/4);

        colorbutton.setHeight(height/9);
        colorbutton.setWidth(width/4);
        colorbutton.setX(width/2+width/8);
        colorbutton.setY(height-height/4);

        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) circleimg.getLayoutParams();
        params.width = width/2+width/6; params.height = height/3;
        circleimg.setX(width/2-width/3);
        circleimg.setY(height/2-height/4);


    }
    public int getcolor(Bitmap bip,float x,float y)
    {
        Log.d(TAG, "getcolor: "+x+"y="+y+"hh");
        int A, R, G, B;
        int pixelColor;
        int allpixel;

        pixelColor = bip.getPixel((int)x, (int)y);
        A = Color.alpha(pixelColor);
        R = Color.red(pixelColor);
        G = Color.green(pixelColor);
        B = Color.blue(pixelColor);

        allpixel = A+R+G+B;

        return allpixel;
    }


    public void tatalcircle()
    {

        dataDegee.put("no.1", getDegree(getcolor1,total));
        dataDegee.put("no.2", getDegree(getcolor2,total));
        dataDegee.put("no.3", getDegree(getcolor3,total));
        dataDegee.put("no.4", getDegree(getcolor4,total));
        dataDegee.put("no.5", getDegree(getcolor5,total));
        dataDegee.put("no.6", getDegree(getcolor6,total));
        dataDegee.put("no.7", getDegree(getcolor7,total));
        dataDegee.put("no.8", getDegree(getcolor8,total));

        myDataView.setVisibility(View.VISIBLE);
        mylayout.addView(myDataView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        myDataView = new MyDataView(this, DataDegree(), DataColor());
        //myDataView.setVisibility(INVISIBLE);
      //  mylayout.addView(myDataView);

    }

    private HashMap<String , Float> DataDegree(){
        dataDegee.put("no.1", 32f);
        dataDegee.put("no.2", 42f);
        dataDegee.put("no.3", 52f);
        dataDegee.put("no.4", 92f);
        dataDegee.put("no.5",142f);
        dataDegee.put("no.6",11f);
        dataDegee.put("no.7",20f);
        dataDegee.put("no.8",21f);
        return dataDegee;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private HashMap<String , String> DataColor(){

        dataColor.put("no.1","#6600ff");
        dataColor.put("no.2","#cc00ff");
        dataColor.put("no.3","#9933ff");
        dataColor.put("no.4","#ff9900");
        dataColor.put("no.5","#FFFF77");
        dataColor.put("no.6","#8B4513");
        dataColor.put("no.7","#2E8B57");
        dataColor.put("no.8","#3CB371");

        return dataColor;
    }

    private float getDegree(float number, float total){
        return  number/total * 360;
    }


    private File getImageFile()  {
        String time = new SimpleDateFormat("yyMMdd").format(new Date());
        String fileName = time+"_";
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            //給予檔案命名及檔案格式
            File imageFile = File.createTempFile(fileName,".jpg",dir);
            //給予全域變數中的照片檔案位置，方便後面取得
            mPath = imageFile.getAbsolutePath();
            return imageFile;
        } catch (IOException e) {
            return null;
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        /**可在此檢視回傳為哪個相片，requestCode為上述自定義，resultCode為-1就是有拍照，0則是使用者沒拍照*/
        Log.d(TAG, "onActivityResult: requestCode: "+requestCode+", resultCode "+resultCode);
        /**如果是高畫質的相片回傳*/
        if (requestCode == REQUEST_HIGH_IMAGE && resultCode == -1){
            camerabtn.setText("Retry");
            colorbutton.setVisibility(View.VISIBLE);
            circleimg.setVisibility(View.VISIBLE);
            new Thread(()->{
                //在BitmapFactory中以檔案URI路徑取得相片檔案，並處理為AtomicReference<Bitmap>，方便後續旋轉圖片
                AtomicReference<Bitmap> getHighImage = new AtomicReference<>(BitmapFactory.decodeFile(mPath));
                Matrix matrix = new Matrix();
                matrix.setRotate(90f);//轉90度
                getHighImage.set(Bitmap.createBitmap(getHighImage.get()
                        ,0,0
                        ,getHighImage.get().getWidth()
                        ,getHighImage.get().getHeight()
                        ,matrix,true));
                runOnUiThread(()->{
                    //以Glide設置圖片(因為旋轉圖片屬於耗時處理，故會LAG一下，且必須使用Thread執行緒)
                    Glide.with(this)
                            .load(getHighImage.get())
                            .centerCrop()
                            .into(imageHigh);
                });
            }).start();
        }

    }

    private final class TouchListener implements View.OnTouchListener {

        /** 记录是拖拉照片模式还是放大缩小照片模式 */
        private int mode = 0;// 初始状态
        /** 拖拉照片模式 */
        private static final int MODE_DRAG = 1;
        /** 放大缩小照片模式 */
        private static final int MODE_ZOOM = 2;

        /** 用于记录开始时候的坐标位置 */
        private PointF startPoint = new PointF();
        /** 用于记录拖拉图片移动的坐标位置 */
        private Matrix matrix = new Matrix();
        /** 用于记录图片要进行拖拉时候的坐标位置 */
        private Matrix currentMatrix = new Matrix();

        /** 两个手指的开始距离 */
        private float startDis;
        /** 两个手指的中间点 */
        private PointF midPoint;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            /** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                // 手指压下屏幕
                case MotionEvent.ACTION_DOWN:
                    mode = MODE_DRAG;
                    // 记录ImageView当前的移动位置
                    currentMatrix.set(imageHigh.getImageMatrix());
                    startPoint.set(event.getX(), event.getY());
                    break;
                // 手指在屏幕上移动，改事件会被不断触发
                case MotionEvent.ACTION_MOVE:
                    // 拖拉图片
                    if (mode == MODE_DRAG) {
                        float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
                        float dy = event.getY() - startPoint.y; // 得到x轴的移动距离
                        // 在没有移动之前的位置上进行移动
                        matrix.set(currentMatrix);
                        matrix.postTranslate(dx, dy);
                        Log.d(TAG, "onTouch------: "+matrix);
                    }
                    // 放大缩小图片
                    else if (mode == MODE_ZOOM) {
                        float endDis = distance(event);// 结束距离
                        if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                            float scale = endDis / startDis;// 得到缩放倍数
                            matrix.set(currentMatrix);
                            matrix.postScale(scale, scale,midPoint.x,midPoint.y);
                        }
                    }
                    break;
                // 手指离开屏幕
                case MotionEvent.ACTION_UP:
                    // 当触点离开屏幕，但是屏幕上还有触点(手指)
                case MotionEvent.ACTION_POINTER_UP:
                    mode = 0;
                    break;
                // 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
                case MotionEvent.ACTION_POINTER_DOWN:
                    mode = MODE_ZOOM;
                    /** 计算两个手指间的距离 */
                    startDis = distance(event);
                    /** 计算两个手指间的中间点 */
                    if (startDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                        midPoint = mid(event);
                        //记录当前ImageView的缩放倍数
                        currentMatrix.set(imageHigh.getImageMatrix());
                    }
                    break;
            }
            imageHigh.setImageMatrix(matrix);

            return true;
        }

        /** 计算两个手指间的距离 */
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


    /*------------------------debug-----------------------------*/

    public class DrawView extends View {

        public DrawView(Context context) {
            super(context);
        }


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Paint p = new Paint();

            p.setColor(Color.RED);
            p.setStyle(Paint.Style.FILL);//设置填满
            canvas.drawCircle(circleimgX+circleimgW/4+circleimgW/15,circleimgY+circleimgH/5, 10, p);
            canvas.drawCircle(circleimgX+circleimgW/5+5,circleimgY+circleimgH/5+circleimgH/4-5, 10, p);
            canvas.drawCircle(circleimgX+circleimgW/5+10,circleimgY+circleimgH/5+circleimgH/2, 10, p);
            canvas.drawCircle(circleimgX+circleimgW/3+circleimgW/20,circleimgY+circleimgH/3+circleimgH/2+circleimgH/30, 10, p);
            canvas.drawCircle(circleimgX+circleimgW/2+circleimgW/8,circleimgY+circleimgH/3+circleimgH/2+circleimgH/25, 10, p);
            canvas.drawCircle(circleimgX+circleimgW/2+circleimgW/4+circleimgW/30,circleimgY+circleimgH/5+circleimgH/2, 10, p);
            canvas.drawCircle(circleimgX+circleimgW/2+circleimgW/5+circleimgW/12,circleimgY+circleimgH/5+circleimgH/5+5, 10, p);
            canvas.drawCircle(circleimgX+circleimgW/5+circleimgW/2,circleimgY+circleimgH/5, 10, p);

        }
    }
}