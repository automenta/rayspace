/*
 * This file is part of the SimpleRay project
 * 
 * Published as part of the FreeMapper project:
 * http://sourceforge.net/projects/freemapper/
 * 
 * author: Hj. Malthaner <h_malthaner@users.sourceforge.net>
 */
 
package tracer;



/**
 * Simple 3D vector type.
 * 
 * @author Hj. Malthaner
 */
public class V3
{
    /**
     * Hajo: Object pooling seems to be needed
     */
    private static final ThreadLocal<PoolArrayList> pools = new ThreadLocal<PoolArrayList> ()
    {
        @Override
        protected PoolArrayList initialValue()
        {
            return new PoolArrayList(8192);
        }        
    };
        
    public static V3 make(final V3 other)
    {
        final PoolArrayList pool = pools.get();
        final V3 v;
        
        if(pool.size > 0)
        {
            v = pool.removeLast(other);
        }
        else
        {
            v = new V3(other);
        }
        
        return v;
    }
    
    public static void put(final V3 v)
    {
        if(v.myPool == null)
        {
            pools.get().add(v);
        }
        else
        {
            v.myPool.add(v);
        }
    }
    public static V3 cross(final V3 a, final V3 b)
    {
        return new V3
                (
                    a.y * b.z - a.z * b.y,
                    a.z * b.x - a.x * b.z,
                    a.x * b.y - a.y * b.x
                );
    }

    public static V3 cross(double x, double y, double z, V3 b)
    {
        return new V3
                (
                    y * b.z - z * b.y,
                    z * b.x - x * b.z,
                    x * b.y - y * b.x
                );
    }
    
    public static double dot(final V3 a, final V3 b)
    {
        return a.x*b.x + a.y * b.y + a.z * b.z;
    }

    public static double dot(double x, double y, double z, V3 b)
    {
        return x*b.x + y * b.y + z * b.z;
    }

    
    public double x, y, z;
    private PoolArrayList myPool;

    public V3()
    {
    }

    public V3(final double x, final double y, final double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public V3(final V3 v)
    {
        x = v.x;
        y = v.y;
        z = v.z;
    }

    /**
     * @returns length^2
     */
    public final double length2() 
    {
        return x * x + y * y + z * z;
    }

    public final double length()
    {
        return Math.sqrt(length2());
    }

    public final void reverse()
    {
        x = -x;
        y = -y;
        z = -z;
    }
    public final void sub(final V3 other)
    {
        x -= other.x;
        y -= other.y;
        z -= other.z;
    }

    public final void add(final V3 other)
    {
        x += other.x;
        y += other.y;
        z += other.z;
        
    }

    public final void div(final double d)
    {
        x /= d;
        y /= d;
        z /= d;
    }

    public final void mul(final double d)
    {
        x *= d;
        y *= d;
        z *= d;
    }

    public void mul(double scaleX, double scaleY, double scaleZ)
    {
        x *= scaleX;
        y *= scaleY;
        z *= scaleZ;
    }

    public final void norm()
    {
        final double length = Math.sqrt(length2());
        div(length);
    }

    public final void set(final V3 v)
    {
        x = v.x;
        y = v.y;
        z = v.z;        
    }

    public final void add(final V3 ray, final double t)
    {
        x += ray.x * t;
        y += ray.y * t;
        z += ray.z * t;
    }

    public final void set(final double x, final double y, final double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;        
    }
    
    public final void rotate(V3 axis, double r)
    {
        final double u = axis.x;
        final double v = axis.y;
        final double w = axis.z;        

        final double sin = Math.sin(r);
        final double cos = Math.cos(r);

        final double nx = u*(u*x+v*y+w*z)*(1-cos)+x*cos+(v*z-w*y)*sin;
        final double ny = v*(u*x+v*y+w*z)*(1-cos)+y*cos+(w*x-u*z)*sin;
        final double nz = w*(u*x+v*y+w*z)*(1-cos)+z*cos+(u*y-v*x)*sin;

        x = nx;
        y = ny;
        z = nz;
    }

    @Override
    public String toString()
    {
        return "x=" + x + " y=" + y + " z=" + z;
    }

    
    
    private static class PoolArrayList
    {
        private V3 [] data;
        int size;
        
        public PoolArrayList(int size)
        {
            data = new V3[size];
        }
        
        public final void add(V3 v)
        {
            data[size++] = v;
        }
                
        public final V3 removeLast(V3 other)
        {
            final V3 v = data[--size];

            v.x = other.x;
            v.y = other.y;
            v.z = other.z;
            v.myPool = this;
            
            return v;
        }
    }
}
