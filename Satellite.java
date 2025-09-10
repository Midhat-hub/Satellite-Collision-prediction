// src/Satellite.java
public class Satellite {
    private String id;
    private double x, y, z;
    private double vx, vy, vz;
    private double radius;

    public Satellite(String id, double x, double y, double z, double vx, double vy, double vz, double radius) {
        this.id = id;
        this.x = x; this.y = y; this.z = z;
        this.vx = vx; this.vy = vy; this.vz = vz;
        this.radius = radius;
    }

    public void move() {
        x += vx;
        y += vy;
        z += vz;
    }

    public double distanceTo(Satellite other) {
        return Math.sqrt(Math.pow(x - other.x, 2) +
                Math.pow(y - other.y, 2) +
                Math.pow(z - other.z, 2));
    }

    public boolean isColliding(Satellite other,boolean is3D) {
        return distanceTo(other) <= (this.radius + other.radius);
    }

    public String getId() { return id; }
}
