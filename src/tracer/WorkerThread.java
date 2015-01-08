/*
 * This file is part of the SimpleRay project
 * 
 * Published as part of the FreeMapper project:
 * http://sourceforge.net/projects/freemapper/
 * 
 * author: Hj. Malthaner <h_malthaner@users.sourceforge.net>
 */

package tracer;

import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * A raytracing worker thread.
 * 
 * @author Hj. Malthaner
 */
public class WorkerThread extends Thread
{
    private final Tracer tracer;
    private int yStart;
    private int yEnd;
    volatile boolean go;
    private final TracerDataSet tracerData;;

    public WorkerThread(Tracer tracer, int i)
    {
        super("Worker #" + i);
        this.tracer = tracer;
        this.go = true;
        tracerData = new TracerDataSet();
        
        setDaemon(true);
    }
    
    public void startRendering(int yStart, int yEnd, int width)
    {
        this.yStart = yStart;
        this.yEnd = yEnd;

        tracerData.updateLinepix(width);
    }

    @Override
    public void run()
    {
        calculate();
    }

      synchronized void shutdown() {
        go = false;
        notify();
    }
      
    private synchronized void calculate()
    {
        while(go)
        {
            try
            {
                // System.err.println(getName() + " waiting");
                wait();
                
                // System.err.println(getName() + " starting yStart=" + yStart + " yEnd=" + yEnd);
                tracer.calculateScene(yStart, yEnd, tracerData);            
                // frame ++;
                // System.err.println(getName() + " done, frame=" + frame);
                tracer.workerDone();
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(WorkerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
