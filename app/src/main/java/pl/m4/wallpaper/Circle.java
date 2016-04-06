package pl.m4.wallpaper;

public class Circle extends Figure {
    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    private float radius;

    public Circle(String name, float x, float y) {
        super(name, x, y);
        this.radius = 20f;
        setPadding((byte) 20);
        setMass(1.5f);
    }

    public Circle(String name, float x, float y, float mass, float radius) {
        super(name, x, y);
        setMass(mass);
        setRadius(radius);
        setPadding((byte) radius);
    }

    public boolean detectCollision(float x, float y, float radius) {
        float dx = x - getX();
        float dy = y - getY();
        double distanceSquared = Math.pow(dx, 2)  + Math.pow(dy, 2);
        return distanceSquared < Math.pow((getRadius() + radius), 2);
    }

    public boolean detectCollision(Circle circle) {
        float dx = circle.getX() - getX();
        float dy = circle.getY() - getY();
        double distanceSquared = Math.pow(dx, 2)  + Math.pow(dy, 2);
        return distanceSquared < Math.pow(getRadius() + circle.getRadius(), 2);
    }

    public boolean hasCollision(Circle circle){
        //(x2-x1)^2 + (y1-y2)^2 <= (r1+r2)^2
        double xDiff = getX() - circle.getX();
        double yDiff = getY() - circle.getY();
        double distance = Math.pow(xDiff, 2) + Math.pow(yDiff, 2);
        return distance-5 <= Math.pow((getRadius() + circle.getRadius()), 2);
    }

    /**
     * Function calculate new vector for this Circle object.
     * @param collided
     * @return float array size of 2.
     * float[0] - new calculated vector for axis x;
     * float[1] - new calculated vector for axis y.
     */
    public float[] calculateVector(Figure collided){
        float[] vector = new float[2];
        vector[0] = ( getVx() * (getMass() - collided.getMass()) + (2 * collided.getMass() * collided.getVx()) ) / (getMass()+collided.getMass());
        vector[1] = ( getVy() * (getMass() - collided.getMass()) + (2 * collided.getMass() * collided.getVy()) ) / (getMass()+collided.getMass());
        return vector;
    }

    /**
     * Function calculate new vector for collided Figure object.
     * @param collided - Collided Figure object.
     * @return float array size of 2.
     * float[0] - new calculated vector for axis x;
     * float[1] - new calculated vector for axis y.
     */
    public float[] calculateVectorForCollidedCircle(Circle collided){
        float[] vector = new float[2];
        vector[0] = ( collided.getVx() * (collided.getMass() - getMass()) + (2 * getMass() * getVx()) ) / (getMass()+collided.getMass());
        vector[1] = ( collided.getVy() * (collided.getMass() - getMass()) + (2 * getMass() * getVy()) ) / (getMass()+collided.getMass());
        return vector;
    }

    public double calculateDistance(Circle circle){
        return Math.hypot(getX()-circle.getX(), getY()-circle.getY());
        //Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
        //var distance:Number = Math.sqrt(((a.x - b.x) * (a.x - b.x)) + ((a.y - b.y) * (a.y - b.y)));
    }

}
