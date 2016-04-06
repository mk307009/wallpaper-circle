package pl.m4.wallpaper;

import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

public abstract class Figure {

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    private float mass;

    public String getName() {
        return name;
    }

    private String name;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    private float x;

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    private float y;

    public float getVx() {
        return vx;
    }

    public synchronized void setVx(float vx) {
        this.vx = vx;
    }

    private float vx;

    public float getVy() {
        return vy;
    }

    public void setVy(float vy) {
        this.vy = vy;
    }

    private float vy;


    public boolean isMove(){
        return isMove;
    }

    public void setMove(boolean moving){
        isMove = moving;
    }

    private boolean isMove;

    private Paint paint;

    public Paint getPaint(){
        return paint;
    }

    public void setPaint(Paint paint){
        this.paint = paint;
    }

    private byte padding;

    public void setPadding(byte padding){
        this.padding = padding;
    }

    public byte getPadding(){
        return padding;
    }

    public Figure(String name, float x, float y) {
        this.name = name;
        this.x = x;
        this.y = y;
        mass = 3.5f;
        isMove = false;
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setShadowLayer(10f, 0, 0, Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAlpha(200);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    public void setColor(int color){
        paint.setColor(color);
    }

    public void moveOnScreen(int width, int height) {
        if (y > height - padding && vy > 0) vy = -vy;
        else if (y < padding && vy < 0) vy = -vy;
        if (x > width - padding && vx > 0) vx = -vx;
        else if (x < padding && vx < 0) vx = -vx;
        y += vy;
        x += vx;
    }

    public void negateVector() {
        vx = -vx;
        vy = -vy;
    }

    public void generateRandomPosition() {
        Random rand = new Random();
        int xRandom = rand.nextInt(5);
        int yRandom = rand.nextInt(5);
        boolean bRandom = rand.nextBoolean();
        if (!bRandom) xRandom = -xRandom;
        bRandom = rand.nextBoolean();
        if (!bRandom) yRandom = -yRandom;
        setVx(xRandom);
        setVy(yRandom);
        setMove(true);
    }

}

