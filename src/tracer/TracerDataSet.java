/*
 * This file is part of the SimpleRay project
 * 
 * Published as part of the FreeMapper project:
 * http://sourceforge.net/projects/freemapper/
 * 
 * author: Hj. Malthaner <h_malthaner@users.sourceforge.net>
 */
package tracer;

import java.util.Random;
import unused.to.us.harha.jpath.util.XORShiftRandom;
import tracer.objects.SceneObject;

/**
 * Data set for a raytracing thread.
 * 
 * @author Hj. Malthaner
 */
public class TracerDataSet
{
    public final V3 ray = new V3();
    public final V3 p = new V3();
    public final V3 tmpRay = new V3();
    public final V3 tmpP = new V3();
    public final V3 lineV = new V3();
    public final V3 hit = new V3();
    
    public final Random rng = new XORShiftRandom();
    
    public int[] linepix;

    public SceneObject bestObject;

    public final void updateLinepix(int targetSize)     {
        
        if(linepix == null || linepix.length != targetSize) {                        
            linepix = new int[targetSize];
        }
    }
}
