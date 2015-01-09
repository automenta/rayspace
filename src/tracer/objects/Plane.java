/*
 * This file is part of the SimpleRay project
 * 
 * Published as part of the FreeMapper project:
 * http://sourceforge.net/projects/freemapper/
 * 
 * author: Hj. Malthaner <h_malthaner@users.sourceforge.net>
 */
package tracer.objects;

import tracer.RGB;
import tracer.Textures;
import tracer.V3;



/**
 *
 * @author Hj. Malthaner
 */
abstract public class Plane extends AbstractSceneObject {

    private final V3 pos;
    private final V3 normal;



    public Plane(V3 pos, V3 normal) {
        super();

        this.pos = new V3(pos);
        this.normal = new V3(normal);
        this.normal.norm();

    }



    @Override
    public double trace(final V3 camera, final V3 ray, double raylen2) {
        final double d = V3.dot(ray, normal);

        if (d != 0) {
            final double x = pos.x - camera.x;
            final double y = pos.y - camera.y;
            final double z = pos.z - camera.z;

            return V3.dot(x, y, z, normal) / d;
        }

        return Double.MAX_VALUE;
    }

    abstract public long hit(V3 p, V3 ray, V3 light, double t);
    

    @Override
    public void translate(V3 move)
    {
        pos.add(move);
    }

    @Override
    public V3 getPos()
    {
        return pos;
    }

    V3 getNormal()
    {
        return normal;
    }

}
