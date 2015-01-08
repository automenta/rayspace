package unused.to.us.harha.jpath.util.math;

import unused.to.us.harha.jpath.Main;

public class Plane extends Primitive
{

	private Vec3f m_norm;

	public Plane(Vec3f pos, Vec3f norm)
	{
		super(pos);
		m_norm = norm;
	}

	@Override
	public Intersection intersect(Ray r) 	{
		
		final float d = Vec3f.dot(m_norm, r.getDir());                
                if (d < Main.EPSILON)
                    return null;
                
		final float t = Vec3f.dot( Vec3f.sub(m_vertices[0], r.getPos()) , m_norm) / d;

		if (t < Main.EPSILON)
                    return null;

		Intersection x = new Intersection(
                    Vec3f.addSelf(Vec3f.scale(r.getDir(), t), r.getPos()),
                    Vec3f.normalize(m_norm),
                    t);

		return x;
	}

}
