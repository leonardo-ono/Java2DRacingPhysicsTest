package teste2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * Referencia: http://www.asawicki.info/Mirror/Car%20Physics%20for%20Games/Car%20Physics%20for%20Games.html
 * http://www.carrosinfoco.com.br/carros/2014/06/a-arte-de-dirirgir-gerenciando-a-carga-sobre-os-pneus/
 * http://engineeringdotnet.blogspot.com.br/2010/04/simple-2d-car-physics-in-games.html
 * 
 * @author leonardo
 */
public class Vehicle {

    public double mass = 800; // em kilograma
    public Vec3 position = new Vec3();
    public Vec3 direction = new Vec3(0, 1, 0);
    public Vec3 velocity = new Vec3();
    public Vec3 acceleration = new Vec3();
    
    public double cBraking = 500;
    public Vec3 fBraking = new Vec3();
    
    public double engineForce = 0;
    public Vec3 fTraction = new Vec3();
    
    public double cDrag = 0.4257;
    public Vec3 fDrag = new Vec3();

    public Vec3 fRolingResistence = new Vec3();
    public double cRolingResistence = 12.8;
    
    public Vec3 fLongtitudinal = new Vec3();
    
    public boolean isDrifting;
    
    public Vehicle() {
    }
    
    public void update() {
        
        double cTyre = 0.6;
        
        double dif = cTyre * (velocity.getSize() / 30.0);
        
        if (Keyboard.keydown[37]) {
            direction.rotateZ(Math.toRadians((cTyre) * velocity.getSize()));
            //velocity.rotateZ(Math.toRadians((cTyre - dif) * velocity.getSize()));
        }
        else if (Keyboard.keydown[39]) {
            direction.rotateZ(Math.toRadians((-cTyre) * velocity.getSize()));
            //velocity.rotateZ(Math.toRadians((-cTyre + dif) * velocity.getSize()));
        }
        
        // alinha a velocidade do carro com a direcao do carro
        // quanto menor a velocidade, maior o alinhamento
        {
            double difAngle = velocity.getRelativeAngleBetween(direction);
            System.out.println("dif="+dif);
            if (!Double.isNaN(difAngle)) {
                double r = Math.random() * 50;
                velocity.rotateZ(difAngle/((50+r)*(5*dif)));
                
                isDrifting = Math.abs(Math.toDegrees(difAngle)) > 30;
            }
            
            //Vec3 velTmp = new Vec3();
            //velTmp.set(velocity);
            //double difAngle2 = 2 * dif * velTmp.relativeAngleBetween(direction);
            
            //if (difAngle2 > difAngle) {
            //    velocity.rotateZ(Math.toRadians(- 2 * dif / 10));
            //}
        
        }
        
        if (Keyboard.keydown[38]) {
            engineForce = 300;
        }
        else if (Keyboard.keydown[40]) {
            engineForce = -300;
        }
        else  {
            engineForce = 0;
        }
        
        if (isDrifting) {
            engineForce = engineForce / 5;
        }
        

        boolean isBraking = Keyboard.keydown[66];
        
        calculateBraking();
        calculateTraction();
        calculateDrag();
        calculateRolingResistence();
        calculateLongtitudinalForce(isBraking);
        calculateAcceleration();
        calculateVelocity(1);
        calculatePosition(1);
    }
    
    private Point2D driftLastPoint1;
    private Point2D driftLastPoint2;
    
    public void draw(Graphics2D g, Graphics2D backg) {
        double angle = -Math.atan2(direction.x, direction.y);
        g.rotate(angle, position.x, position.y + 0);
        g.setColor(Color.BLACK);
        g.fillRect((int) (position.x-20), (int) (position.y-30), 40, 60);
        
        if (isDrifting) {
            AffineTransform transf = new AffineTransform();
            transf.rotate(angle, position.x, position.y + 0);
            
            backg.setColor(new Color(0, 0, 0, 16));
            BasicStroke basicStroke = new BasicStroke(10);
            backg.setStroke(basicStroke);
            
            if (driftLastPoint1!=null && driftLastPoint2!=null) {
                Point2D p1 = new Point2D.Double((int) (position.x-17), (int) (position.y-30));
                Point2D p2 = new Point2D.Double((int) (position.x+17), (int) (position.y-30));

                transf.transform(p1, p1);
                transf.transform(p2, p2);
                
                backg.drawLine((int) p1.getX(), (int) p1.getY(), (int) driftLastPoint1.getX(), (int) driftLastPoint1.getY());
                backg.drawLine((int) p2.getX(), (int) p2.getY(), (int) driftLastPoint2.getX(), (int) driftLastPoint2.getY());
            }
            
            driftLastPoint1 = new Point2D.Double((int) (position.x-17), (int) (position.y-30));
            driftLastPoint2 = new Point2D.Double((int) (position.x+17), (int) (position.y-30));
            transf.transform(driftLastPoint1, driftLastPoint1);
            transf.transform(driftLastPoint2, driftLastPoint2);
        }
        else {
            driftLastPoint1 = null;
            driftLastPoint2 = null;
        }
    }
    
    // Ftraction = u * Engineforce, 
    private void calculateTraction() {
        fTraction.set(direction);
        fTraction.normalize();
        fTraction.scale(engineForce);
    }
 
    // Fdrag = - Cdrag * v * |v| 
    // where Cdrag is a constant and v is the velocity vector and the notation |v| refers to the magnitude of vector v    
    // speed = sqrt(v.x*v.x + v.y*v.y); 
    // fdrag.x = - Cdrag * v.x * speed; 
    // fdrag.y = - Cdrag * v.y * speed;    
    private void calculateDrag() {
        double speed = velocity.getSize();
        fDrag.set(velocity);
        fDrag.scale(speed);
        fDrag.scale(-cDrag);
    }

    // Frr = - Crr * v 
    // where Crr is a constant and v is the velocity vector.    
    private void calculateRolingResistence() {
        fRolingResistence.set(velocity);
        fRolingResistence.scale(-cRolingResistence);
    }
    
    // Flong = Ftraction + Fdrag + Frr
    private void calculateLongtitudinalForce(boolean isBraking) {
        if (isBraking) {
            fLongtitudinal.set(fBraking);
        }
        else {
            fLongtitudinal.set(fTraction);
        }
        fLongtitudinal.add(fDrag);
        fLongtitudinal.add(fRolingResistence);
    }
    
    private void calculateAcceleration() {
        acceleration.set(fLongtitudinal);
        acceleration.scale(1/mass);
    }
    
    private void calculateVelocity(double deltaTime) {
        acceleration.scale(deltaTime);
        velocity.add(acceleration);
    }
 
    private void calculatePosition(double deltaTime) {
        velocity.scale(deltaTime);
        position.add(velocity);
    }
    
    private void calculateBraking() {
        fBraking.set(velocity);
        fBraking.normalize();
        fBraking.scale(-cBraking);
    }
    
}
