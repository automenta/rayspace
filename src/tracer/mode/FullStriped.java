/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tracer.mode;

import java.util.ArrayList;
import java.util.List;
import tracer.DisplayInterface;
import tracer.Display;
import tracer.Tracer;
import tracer.TracerDataSet;
import tracer.V3;
import tracer.WorkerThread;

/**
 *
 * @author me
 */
public class FullStriped extends Tracer {
    private final int threads;
    private Display display;

    class FSWorkerThread extends WorkerThread {
        public int yStart;
        public int yEnd;

        public FSWorkerThread(Tracer tracer, int i) {
            super(tracer, i);
        }

        public void setRange(int yStart, int yEnd) {
            this.yStart = yStart;
            this.yEnd = yEnd;
        }
        
        @Override
        protected void calculate() {
            render(yStart, yEnd, tracerData);
        }
        
        
    }
    
    public FullStriped(int threads) {
        super();
        this.threads = threads;
        createWorkers();
        
    }

    @Override
    protected List<WorkerThread> newWorkers() {
        List<WorkerThread> w = new ArrayList(threads);
        for(int i=0; i<threads; i++)
        {
            w.add(new FSWorkerThread(this, i));
        }
        return w;
    }

    
    
    @Override
    public void render(Display displayPanel)
    {
        
        this.display = displayPanel;
        
        final int height = displayPanel.height();
        final int width = displayPanel.width();
        final int hh = height >> 1;
        
        final int stripe = height / workers.size() + 1;
        
        for(int i=0; i<workers.size(); i++)
        {
            final int yStart = -hh + i * stripe;
            final int yEnd = Math.min(hh, yStart + stripe);
            
            FSWorkerThread worker = (FSWorkerThread) workers.get(i);
            worker.setRange(yStart, yEnd);
            
            synchronized(worker)
            {
                worker.notify();
            }
        }
        // System.err.println("frame1=" + one.frame + " frame2=" + two.frame);
    }
    
    void render(int yStart, int yEnd, TracerDataSet data)    {
        
        int width = display.width();
        int height = display.height();
        
        data.updateLinepix(width);
        
        final int hw = width >> 1;
        final int hh = height >> 1;
        
        final V3 lineV = data.lineV;
        
        final int[] line = data.linepix;
        
        
        for(int y=yEnd; y>yStart; y--)
        {
            lineV.set(look);
            lineV.add(vert, y);
            
            final V3 ray = data.ray;
            
            for(int x=-hw; x<hw; x++)
            {
                ray.set(lineV);
                ray.add(horz, x);
                
                
                data.p.set(camera);
                
                final int rgb = traceObjects(data);

            
                int px = x;
                int i = hw+px;
                //if (i >= width) i = width-1;
                line[i] = rgb;
            }          
            
            int py = y;
            int ty = hh-py;
            //if (ty < 0) ty = 0;
                    
            display.setline(ty, line);
        }                
    }

    
    //void calculateScene(int yStart, int yEnd, TracerDataSet data) {
        //calculateSceneComplete(yStart,yEnd,data);
        //calculateSceneHoriSample(yStart, yEnd, data);
        //calculateSceneRandomSample(yStart,yEnd,data);
    //}
    
}
