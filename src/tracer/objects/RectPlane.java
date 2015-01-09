/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tracer.objects;

import tracer.V3;
import static tracer.objects.AbstractSceneObject.ALMOST_ONE;

/**
 *
 * @author me
 */
abstract public class RectPlane extends Plane {
    
    private V3 min;
    private V3 max;

    public RectPlane(V3 pos, V3 normal) {
        super(pos, normal);
    }
    
    
    public void setMin(V3 min) {
        this.min = min;
    }

    public void setMax(V3 max) {
        this.max = max;
    }
    
    @Override
    public long hit(V3 p, V3 ray, V3 light, double t)
    {
        p.add(ray, t * ALMOST_ONE);

        boolean inside = false;
        
        if((min == null || (p.x >= min.x && p.y >= min.y && p.z >= min.z)) &&
           (max == null || (p.x <= max.x && p.y <= max.y && p.z <= max.z)))
        {
            inside = true;
        }
        
        return getColor(p.x, p.y, !inside);
    }


    abstract public long getColor(double x, double y, boolean outside);
    
}
