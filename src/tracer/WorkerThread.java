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
abstract public class WorkerThread extends Thread {
    private final Tracer tracer;
    volatile boolean go;
    public final TracerDataSet tracerData;

    public WorkerThread(Tracer tracer, int i)
    {
        super("Worker #" + i);
        this.tracer = tracer;
        this.go = true;
        tracerData = new TracerDataSet();
        
        setDaemon(true);
    }


    @Override
    public void run()
    {
        while(go)
        {
            try
            {
                // System.err.println(getName() + " waiting");
                synchronized(this) {
                    wait();
                }
                
                // System.err.println(getName() + " starting yStart=" + yStart + " yEnd=" + yEnd);
                calculate();
                
                // frame ++;
                // System.err.println(getName() + " done, frame=" + frame);
                tracer.workerDone();
            }
            catch (Exception ex)
            {
                Logger.getLogger(WorkerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    synchronized void shutdown() {
        go = false;
        notify();
    }
    
    abstract protected void calculate();
      
}
