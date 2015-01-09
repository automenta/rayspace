/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tracer.mode;

import java.util.List;
import java.util.Random;
import tracer.Display;
import tracer.Tracer;
import tracer.TracerDataSet;
import tracer.V3;
import tracer.WorkerThread;
import tracer.objects.FastMath;

/**
 *
 * @author me
 */
public class Sampled extends Tracer {
    double samplingRate = 1.0;
    
 

    public void setSamplingRate(double samplingRate) {
        this.samplingRate = samplingRate;
    }
    
    /** # pixels to move horizontally */
    public double nextDX(Random rng) {
        if (samplingRate == 1.0) return 1.0;
        return rng.nextFloat() * 1.0/samplingRate;
    }
    
    /** # pixels to move vertically */
    public double nextDY(Random rng) {
        return 1.0;
        //if (samplingRate == 1.0) return 1.0;
        //return rng.nextFloat() * 1.0/samplingRate * 0.01;
    }

    void calculateSceneHoriSample(Display displayPanel, int yStart, int yEnd, TracerDataSet data)
    {
        int width = displayPanel.width();
        int height = displayPanel.height();
        
        data.updateLinepix(width);
        
        final int hw = width >> 1;
        final int hh = height >> 1;
        
        final V3 lineV = data.lineV;
        
        final int[] line = data.linepix;
        
        final Random rng = data.rng;
        
        for(double y=yEnd; y>yStart; y-=nextDY(rng))
        {
            lineV.set(look);
            lineV.add(vert, y);
            
            final V3 ray = data.ray;
            
            for(double x=-hw; x<hw; x+=nextDX(rng))
            {
                ray.set(lineV);
                ray.add(horz, x);
                
                data.p.set(camera);            
                
                final int rgb = traceObjects(data);

            
                int px = FastMath.round(x);
                int i = hw+px;
                if (i >= width) i = width-1;
                line[i] = rgb;
            }          
            
            int py = FastMath.round(y);
            int ty = hh-py;
            if (ty < 0) ty = 0;
                    
            displayPanel.setline(ty, line);
        }                
    }
    

    
    void calculateSceneRandomSample(Display displayPanel, int yStart, int yEnd, TracerDataSet data)
    {
        int width = displayPanel.width();
        int height = displayPanel.height();
        int hrange = yEnd - yStart;
        
        
        data.updateLinepix(width * height);
        
        final int hw = width >> 1;
        final int hh = height >> 1;
        
        yStart += hh;
        yEnd += hh;
        
        final V3 lineV = data.lineV;

        
        final int[] line = data.linepix;
        
        final Random rng = data.rng;
        
        int points = (int) ((width * height) * samplingRate);
        final V3 ray = data.ray;
        for ( ; points > 0; points--) {
            float vx = (-0.5f + rng.nextFloat()) * width;
            float vy = (-0.5f + rng.nextFloat()) * height;
            int px = (int) (vx+hw);
            int py = (int) (hh-vy);
            ray.set(look);
            ray.add(vert, vy);
            ray.add(horz, vx-0.5);
            
            data.p.set(camera);
            
            
            final int rgb = traceObjects(data);
            int i = ((py))*width+px;
            
            if (i >=0 && i < line.length)
                line[i] = rgb;
            //else..
            
        }
        displayPanel.setline(0, line);
    }

    @Override
    protected List<WorkerThread> newWorkers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void render(Display displayPanel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}
