package teste2;

/**
 *
 * @author leonardo
 */
public class Vec3 {

    public double x;
    public double y;
    public double z;

    public Vec3() {
    }

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(Vec3 v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void rotateZ(double angle) {
        double nx = x * Math.cos(angle) - y * Math.sin(angle);
        double ny = x * Math.sin(angle) + y * Math.cos(angle);
        double nz = z;
        set(nx, ny, nz);
    }    
    
    public void rotateY(double angle) {
        double nz = z * Math.cos(angle) - x * Math.sin(angle);
        double nx = z * Math.sin(angle) + x * Math.cos(angle);
        double ny = y;
        set(nx, ny, nz);
    }

    public void rotateX(double angle) {
        double ny = y * Math.cos(angle) - z * Math.sin(angle);
        double nz = y * Math.sin(angle) + z * Math.cos(angle);
        double nx = x;
        set(nx, ny, nz);
    }    

    public void translate(double x, double y, double z) {
        set(this.x + x, this.y + y, this.z + z);
    }
    
    public double getSize() {
        return Math.sqrt(x * x + y * y + z * z);
    }
    
    public void normalize() {
        double size = getSize();
        x /= size;
        y /= size;
        z /= size;
    }
    
    public void scale(double f) {
        x *= f;
        y *= f;
        z *= f;
    }
    
    public void add(Vec3 v) {
        x += v.x;
        y += v.y;
        z += v.z;
    }
    
    public void sub(Vec3 v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
    }

    public double dot(Vec3 v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public static double angleBetween(Vec3 a, Vec3 b) {
	double am = a.getSize();
	double bm = b.getSize();
	return Math.acos(a.dot(b) / (am * bm));
    }
    
    public static void sub(Vec3 r, Vec3 a, Vec3 b) {
        r.x = a.x - b.x;
        r.y = a.y - b.y;
        r.z = a.z - b.z;
    }
    
    public static void cross(Vec3 res, Vec3 left, Vec3 right) {
        double x = left.y * right.z - left.z * right.y;
        double y = right.x * left.z - right.z * left.x;
        double z = left.x * right.y - left.y * right.x;
        res.set(x, y, z);
    }
    
    public double getRelativeAngleBetween(Vec3 v) {
        return getSign(v) * Math.acos(dot(v) / (getSize() * v.getSize()));
    }
    
    // http://www.oocities.org/pcgpe/math2d.html
    // http://gamedev.stackexchange.com/questions/45412/understanding-math-used-to-determine-if-vector-is-clockwise-counterclockwise-f
    public int getSign(Vec3 v) {
        return (y*v.x > x*v.y)?-1:1;
    }
    
}
