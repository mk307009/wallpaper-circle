package pl.m4.wallpaper;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyWallpaperService extends WallpaperService {

    @Override
    public WallpaperService.Engine onCreateEngine() {
        return new WallpaperEngine();
    }

    private class WallpaperEngine extends Engine {
        private final static String TAG = "WallpaperEngine";
        public final static int TIME_TO_CHANGE_MOVE_VECTOR = 1000;//in nanoseconds

        private final Handler handler = new Handler();
        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                draw();
            }
        };
        private final Runnable positionUpdateRunner = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(TIME_TO_CHANGE_MOVE_VECTOR);
                    Random rd = new Random();
                    int rand = rd.nextInt(maxNumber);
                    if (!circles.get(rand).isMove()) {
                        circles.get(rand).generateRandomPosition();
                        circles.get(rand).setMove(true);
                    }
                    rand = rd.nextInt(maxNumber);
                    if (!circles.get(rand).isMove()) {
                        circles.get(rand).generateRandomPosition();
                        circles.get(rand).setMove(true);
                    }
                    rand = rd.nextInt(maxNumber);
                    if (!circles.get(rand).isMove()) {
                        circles.get(rand).generateRandomPosition();
                        circles.get(rand).setMove(true);
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        private Thread updateThread;
        private List<Circle> circles;
        private int width;
        private int height;
        private boolean visible = true;
        private int maxNumber;
        private boolean touchEnabled;
        private SharedPreferences prefs;
        private Bitmap background;

        public WallpaperEngine() {
            prefs = PreferenceManager
                    .getDefaultSharedPreferences(MyWallpaperService.this);
            maxNumber = Integer
                    .valueOf(prefs.getString("numberOfCircles", "10"));
            touchEnabled = prefs.getBoolean("touch", true);
            circles = new ArrayList<Circle>();
            handler.post(drawRunner);
            updateThread = new Thread(positionUpdateRunner);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            this.maxNumber = Integer
                    .valueOf(prefs.getString("numberOfCircles", "10"));
            if (visible) {
                handler.post(drawRunner);
            } else {
                handler.removeCallbacks(drawRunner);
                updateThread.interrupt();
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.visible = false;
            handler.removeCallbacks(drawRunner);
            updateThread.interrupt();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            this.width = width;
            this.height = height;
            Resources res = getResources();
            background = BitmapFactory.decodeResource(res, R.drawable.sun);
            background = Bitmap.createScaledBitmap(background, width, height, true);
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            if (touchEnabled && event.getActionMasked() == MotionEvent.ACTION_UP) {
                float x = event.getX();
                float y = event.getY();
                SurfaceHolder holder = getSurfaceHolder();
                Canvas canvas = null;
                try {
                    canvas = holder.lockCanvas();
                    for (Circle point : circles) {
                        if (point.detectCollision(x, y, 15.f)) {
                            Random rand = new Random();
                            point.setColor(Color.argb(200, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
                            point.negateVector();
                        }
                    }
                } finally {
                    if (canvas != null)
                        holder.unlockCanvasAndPost(canvas);
                }
                super.onTouchEvent(event);
            }
        }

        public void collisionInteraction(Circle point, Canvas canvas) {
            for (Circle collided : circles) {
                if (point == collided) continue;
                if (point.hasCollision(collided)) {
                    float[] v1 = point.calculateVector(collided);
                    float[] v2 = point.calculateVectorForCollidedCircle(collided);
                    drawCollisionPoint(point, collided, canvas);
                    point.setVx(v1[0]);
                    point.setVy(v1[1]);
                    collided.setVx(v2[0]);
                    collided.setVy(v2[1]);
                }
            }
        }

        public void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                canvas.drawBitmap(background, 0 , 0, null);
                if (canvas != null) {
                    initCircles();
                    drawCircles(canvas, circles);
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }
            handler.removeCallbacks(drawRunner);
            recall();
        }

        public void recall(){
            if (visible) {
                handler.post(drawRunner);
                try {
                    updateThread.start();
                } catch (IllegalThreadStateException e) {
                }
                if (updateThread.getState() == Thread.State.TERMINATED) {
                    updateThread = new Thread(positionUpdateRunner);
                }
            }
        }

        private void initCircles() {
            if (circles.isEmpty() || circles.size() != maxNumber) {
                circles.clear();
                for (int i = 0; i < maxNumber; i++) {
                    int x = (int) (width * Math.random());
                    int y = (int) (height * Math.random());
                    int size = new Random().nextInt(20)+5;
                    Circle circle = new Circle(String.valueOf(i),
                            x, y, size, size);
                    circle.generateRandomPosition();
                    circles.add(circle);
                }
            }
        }

        public void drawCircles(Canvas canvas, List<Circle> circles) {
            for (Circle point : circles) {
                point.moveOnScreen(width, height);
                canvas.drawCircle(point.getX(), point.getY(), point.getRadius(), point.getPaint());
                collisionInteraction(point, canvas);
            }
        }

        public void drawCollisionPoint(Circle first, Circle second, Canvas canvas){
            float x1 = first.getX();            float y1 = first.getY();
            float x2 = second.getX();           float y2 = second.getY();
            float r1 = first.getRadius();       float r2 = second.getRadius();
            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(5f);
            canvas.drawPoint((x1 * r2 + x2 * r1)/(r1 + r2), (y1 * r2 + y2 * r1) / (r1 + r2), paint);
        }
    }

}