/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tracer.example;

import java.util.ArrayList;
import java.util.List;
import tracer.SimpleRay;
import tracer.Scene;
import tracer.V3;
import tracer.mode.FullStriped;
import tracer.objects.FastSphere;
import tracer.objects.SceneObject;


/**
 *
 * @author me
 */
public class ManyObjects extends Scene {

    private final ArrayList<SceneObject> objects = new ArrayList<SceneObject>();
    
    public static class Quad extends FastSphere {
    
        public Quad(V3 pos, double rad) {
            super(pos, rad);
        }

                
    }
    
    
    double time = 0;
 
    public ManyObjects() {
        double w = 0.5;
        double h = 0.5;
        for (int i = -4; i <= 4; i++) {
            for (int j = -4; j <= 4; j++) {
                double x = i * 2;
                double y = j * 2;
                Quad q;
                objects.add(q =new Quad(
                        new V3(x, y, 0),
                        w));
         
            }
        }
        
        
    }

    
    public List<SceneObject> updateScene(double t) {
    
        time = t;
        return objects;
    }
    
    public static void main(String[] args) {
        new SimpleRay("Quads", 
                new ManyObjects(), new FullStriped(1), 
                800, 600).start();
    }    
    
}
