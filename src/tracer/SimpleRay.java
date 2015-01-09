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
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import tracer.Scene;
import tracer.objects.FastMath;
import tracer.objects.FastSphere;
import tracer.objects.Material;
import tracer.objects.RectPlane;
import tracer.objects.SceneObject;
import tracer.objects.Sphere;


/**
 * A simple raytracer.
 * 
 * @author Hj. Malthaner
 */
public class SimpleRay  {

    private final JFrame frame;
    private final Display display;
    private final Tracer tracer;
    private final Scene scene;
    private final String title;
    
    /** frame limiting: min time between frames */
    long minPeriodMS = 1;
    
    public JFrame getFrame() {
        return frame;
    }
    
    public SimpleRay(String title, Scene scene, Tracer tracer, int width, int height)     {
        
        this.scene = scene;
        this.title = title;
        this.tracer = tracer;
        
        frame = new JFrame(title);
        
        frame.setSize(width, height);
        
        // frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        display = new Display();
        
        
        
        frame.add(display);
        
        display.setIgnoreRepaint(true);
        
        MouseMotionListener mouseNav = new MouseMotionListener() {
            double factor = 0.01;
            
            Integer lastX = null;
            Integer lastY = null;
            
            V3 v = new V3();
            
            @Override
            public void mouseMoved(MouseEvent arg0)
            {
            }
            
            @Override
            public void mouseDragged(MouseEvent me)
            {
                // if (me.getButton() == MouseEvent.BUTTON3)
                {
                if (lastX == null)
                {
                    lastX = me.getX();
                    lastY = me.getY();
                }
                
                int deltaX = me.getX() - lastX;
                int deltaY = me.getY() - lastY;
                
                
                // KeyEvent.VK_UP)
                tracer.getLookAt().add(new V3(deltaY * factor, deltaX * factor, 0));
                tracer.lookAt( tracer.getLookAt() );
                
                
                //scene.currentCamera.setOrientation(vNew);
                
                lastX = me.getX();
                lastY = me.getY();
            }
            }
        };
        display.addMouseMotionListener(mouseNav);
    }
    
    
    public void start()
    {
        frame.setVisible(true);
        
        Textures.init();
        
        boolean go = true;
        
        Graphics gr = display.getGraphics();
        
        int frameCount = 0;
        long t0 = System.nanoTime();
        long t1 = t0;
        
        while(go)
        {
            if(display.isVisible())
            {
                final long t = System.nanoTime();
                final long deltaM = (t - t1);

                
                final long minPeriodNS = 1000000 * minPeriodMS;
                if(deltaM > minPeriodNS)
                {
                    // System.err.println("delta=" + deltaM);
                    try
                    {
                        t1 = t;

                        tracer.updateScene(t1*1E-9, scene);
                        
                        tracer.render(display, gr);
                        
                        display.switchBuffers();                       
                        
                    }
                    catch(Exception ex)
                    {
                        Logger.getLogger(SimpleRay.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    frameCount ++;
                    
                    final long t2 = System.nanoTime();
                    
                    final int sleepTime = 15 - ((int)(t2 - t))/1000000;                    
                    safeSleep(sleepTime);
                }

                final long deltaF = (t - t0);
                if(deltaF > 1500000000)
                {
                    String s = title + ", FPS: " + frameCount;
                    frame.setTitle(s);
                    t0 = t;
                    frameCount = 0;
                }
                
            }
            else
            {
                safeSleep(200);
            }
        }
    }

    public static void safeSleep(int sleepTime)
    {
        try
        {
            if(sleepTime > 1)
            {
                Thread.sleep(sleepTime);
            }
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(SimpleRay.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
