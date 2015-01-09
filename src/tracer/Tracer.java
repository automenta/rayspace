/*
 * This file is part of the SimpleRay project
 * 
 * Published as part of the FreeMapper project:
 * http://sourceforge.net/projects/freemapper/
 * 
 * author: Hj. Malthaner <h_malthaner@users.sourceforge.net>
 */
package tracer;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import tracer.objects.FastMath;
import tracer.objects.FastSphere;
import tracer.objects.SceneObject;
import tracer.objects.Sphere;
import tracer.objects.Material;
import tracer.objects.RectPlane;


/**
 * The actual ray tracer.
 * 
 * @author Hj. Malthaner
 */
abstract public class Tracer {
    
    public final V3 camera;
    private final V3 lookAt;
    public final V3 look;
    public final V3 horz;
    public final V3 vert;
    
    private final V3 light;
    

    
    public List<WorkerThread> workers;
    
    private volatile int doneCount;
    
    private int frame;
    

    private int minBrightness = 32; //lower is higher quality
    private int initialBrightness = 255;
    
    private List<SceneObject> objects;
    
    
    public Tracer()  {
        
        this.doneCount = 0;

        camera = new V3();
        lookAt = new V3();
        
        look = new V3();
        light = new V3();
        
        horz = new V3();
        vert = new V3();
        
        camera.set(2, -10, 7);
        lookAt.set(0, 1, 0);

        light.set(-15, -3, 20);
        updateCamera();
    }

    public V3 getLookAt() {
        return lookAt;
    }
    
    
    public void createWorkers()
    {
        
        workers = newWorkers();
        
        for(WorkerThread worker : workers)
        {
            worker.start();
        }
    }
    
    abstract protected List<WorkerThread> newWorkers();
    
    public void setCamera(V3 camera)
    {
        this.camera.set(camera);
    }
    
    public void setLight(V3 light)
    {
        this.light.set(light);
    }
    
    public void lookAt(V3 lookAt)
    {
        this.lookAt.set(lookAt);
        updateCamera();
    }
        
    private void updateCamera()
    {
        look.set(lookAt);
        look.sub(camera);
        
        
        horz.set(look.y, -look.x, 0);
        vert.set(V3.cross(horz, look));
        
        horz.norm();
        vert.norm();
        
        horz.mul(0.018);
        vert.mul(0.018);
    }
        
    
    abstract public void render(Display displayPanel);

    public synchronized void workerDone()
    {
        // System.err.println("notify done:" + Thread.currentThread().getName());
        doneCount++;
        
        if(doneCount == workers.size()) 
        {
            notify();
            // System.err.println("notify sent, count=" + doneCount);
        }
        
        // System.err.println("done count=" + doneCount);
    }
    
    private synchronized void waitForSceneFinish(Display displayPanel)
    {
        frame ++;
        
        try
        {
            // System.err.println("waiting for workers on frame=" + frame);
            
            wait();
            
            doneCount = 0;
            
            // System.err.println("------------- end ---------------");
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(Tracer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    
    
    public int traceObjects(TracerDataSet data)
    {
        boolean go;
        float brightness = initialBrightness;
        
        long objectRgb = -1;
        
        do
        {
            go = false;
            
            final double t = findIntersection(data);

            if(data.bestObject != null)
            {
                final long color = data.bestObject.hit(data.p, data.ray, light, t);
                
                if(color == -1L)
                {
                    // mirror, p and v are set up by hit().
                    go = true;
                    brightness = mirrorReflect(brightness);
                }
                else
                {
                    objectRgb = color;
                }
            }
        }
        while(go && brightness > minBrightness);

        if(objectRgb == -1)
        {
            // Nothing hit

            data.ray.norm();

            objectRgb = RGB.spread(Textures.clouds.getRGB((data.ray.x+1.0) * 0.5, (data.ray.y+1.0) * 0.5));
        }
        else
        {
            // shadows
            // need to calculate ray from data.p to light source
            
            data.ray.set(light);
            data.ray.sub(data.p);
            
            findIntersection(data);
            if(data.bestObject != null)
            {
                // shadow
                brightness = shadowReflect(brightness);
            }
        }
        
        if(objectRgb == 0xFF00000000000000L)
        {
            return brightness >= 160 ? 0x00000000 : 0xFF000000;  
        }
        else  {
            return RGB.shadeAndCompact(objectRgb, (int)brightness);
        }
    }

    final double findIntersection(TracerDataSet data) {
        final double raylen2 = data.ray.length2();

        double bestT = Double.MAX_VALUE;
        data.bestObject = null;
        
        final int numObj = objects.size();
        for(int i=0; i < numObj; i++) {
            final SceneObject object = objects.get(i);
            final double t = object.trace(data.p, data.ray, raylen2);

            if(t >= 0 && t < bestT) {
                data.bestObject = object;
                bestT = t;
            }
        }
        
        return bestT;
    }


    public void shutdown()
    {
        for(WorkerThread worker : workers)
        {
            worker.shutdown();
        }
    }

    
    public void updateScene(double t, Scene scene) {    
        objects = scene.updateScene(t);
    }
    

    synchronized void render(Display display, Graphics gr)
    {
        render(display);
        display.paint(gr);
        waitForSceneFinish(display);
    }

    public synchronized void calculateOneFrame(Display displayPanel)
    {
        waitForSceneFinish(displayPanel);
    }
    

    private float mirrorReflect(float brightness) {
        return brightness * 0.9f;
    }

    private float shadowReflect(float brightness) {
        return brightness/2;
    }
    
    
}
