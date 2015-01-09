/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tracer.example;

import java.util.ArrayList;
import java.util.List;
import tracer.RGB;
import tracer.SimpleRay;
import tracer.Textures;
import tracer.Scene;
import tracer.V3;
import tracer.mode.FullStriped;
import tracer.objects.FastMath;
import tracer.objects.FastSphere;
import tracer.objects.Material;
import tracer.objects.RectPlane;
import tracer.objects.SceneObject;
import tracer.objects.Sphere;


/**
 *
 * @author me
 */
public class Scene1 extends Scene {

    private final ArrayList<SceneObject> objects = new ArrayList<SceneObject>();
    
    private SceneObject sphere1;
    private SceneObject sphere2;
    private SceneObject sphere3;

    double time = 0;
    
    private V3 move1;
    private V3 move2;
    private V3 move3;
 
    public Scene1() {
        sphere1 = new Sphere(new V3(2, 0, 2), 2);
        sphere1.setMaterial(new Material(0xFF7F00, 1.0));
        sphere1.scale(0.5, 0.5, 1.0);

        sphere2 = new FastSphere(new V3(-3, 0, 1), 1);
        sphere2.setMaterial(new Material(0, 1.0));
        
        sphere3 = new FastSphere(new V3(-6, 6, 3), 1);
        sphere3.setMaterial(new Material(0, 1.0));
        
        move1 = new V3(0.05, 0.07, 0);
        move2 = new V3(0.11, 0.07, 0);
        move3 = new V3(-0.03, 0, 0.02);
        
        RectPlane floor = new RectPlane(new V3(), new V3(0, 0, 1)) {
            
            final long[] checker = new long[] { 
                RGB.spread(255, 160, 0), RGB.spread(128, 80, 0)
            };
            
            final long green = RGB.spread(48, 64, 20);

            @Override
            public long getColor(double x, double y, boolean outside) {
                if (!outside) {
                    x += Math.sin(time);
                    final int px = (int) (x + 1024);
                    final int py = (int) (y + 1024);
                    final int f = px + py & 1;

                    if (f == 0) {
                        double fractX = x - FastMath.floor(x);
                        double fractY = y - FastMath.floor(y);

                        return RGB.spread(Textures.sand.getRGB(fractX, fractY));
                    }
                    else
                    {
                        return checker[f];
                    }
                }
                return green;
            }

            
        };
        floor.setMin(new V3(-5, -5, -5));
        floor.setMax(new V3(5, 5, 5));
        
        objects.add(floor);

        objects.add(sphere1);
        objects.add(sphere2);
        objects.add(sphere3);
        
        
    }

    
    public List<SceneObject> updateScene(double t) {
    
        time = t;
        move3.z -= 0.002; // Gravity
        
        sphere3.translate(move3);
        
        if(sphere3.getPos().z <= 1 && move3.z < 0)
        {
            move3.z = -move3.z;
            move3.z += 0.002; // Gravity
        }        
        if(sphere3.getPos().x <= -8 && move3.x < 0)
        {
            move3.x = -move3.x;
        }
        if(sphere3.getPos().x >= 8 && move3.x > 0)
        {
            move3.x = -move3.x;
        }
        
        double sd = 0.75 + 0.25 * Math.sin(System.currentTimeMillis() / 100.0);
        sphere1.translate(move1);
        sphere1.scale(sd, sd, 1.0);
        
        bounceBorder(sphere1.getPos(), move1, sd*2);
        
        sphere2.translate(move2);
        bounceBorder(sphere2.getPos(), move2, 1);
        
        V3 dist = V3.make(sphere1.getPos());
        dist.sub(sphere2.getPos());
        
        double len = dist.length2();
        
        double r = 1 + 2 * sd;
        if(len <= r*r)
        {
            dist.z = 0;
            dist.norm();
            dist.add(sphere2.getPos());
            dist.z = 0;
           
            bounceSphere(dist, sphere1.getPos(), move1, 2*2*sd);
            bounceSphere(dist, sphere2.getPos(), move2, 1.0);
        }
        V3.put(dist);
        
        return objects;
    }

    private void bounceBorder(V3 pos, V3 move, double rad)
    {
        if((pos.x < -5+rad && move.x < 0) || (pos.x > 5-rad && move.x > 0))
        {
            move.x = -move.x;
        }
        if((pos.y < -5+rad && move.y < 0) || (pos.y > 5-rad && move.y > 0))
        {
            move.y = -move.y;
        }        
    }

    private void bounceSphere(V3 kiss, V3 center, V3 move, double r2)
    {
        double x = kiss.x - center.x;
        double y = kiss.y - center.y;
        
        double f = (move.x * x + move.y*y) / (x*x + y*y);
        
        x *= 2*f;
        y *= 2*f;
        
        move.x -= x;
        move.y -= y;
    }
    
    
    public static void main(String[] args) {
        new SimpleRay("Scene1", 
                new Scene1(), new FullStriped(1), 
                800, 600).start();
    }    
    
}
