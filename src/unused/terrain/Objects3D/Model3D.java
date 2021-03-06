package unused.terrain.Objects3D;

import java.util.LinkedList;
import java.util.List;

import unused.terrain.JavaRenderer.Animation;
import unused.terrain.JavaRenderer.Drawable;
import unused.terrain.JavaRenderer.Scene;
import unused.terrain.JavaRenderer.Vector3D;
import unused.terrain.JavaRenderer.Tri;

public class Model3D extends Drawable
{
	public Model3D(Scene frame)
	{
		super(frame);
	}

	public Model3D(Scene frame, Tri[] vertices, Vector3D[] vectors)
	{
		super(frame);
		this.vectors = vectors;
		this.vertices = vertices;
	}

	public Model3D(Scene frame, List<Tri> vertices, List<Vector3D> vectors)
	{
		super(frame);
		this.vectors = vectors.toArray(new Vector3D[vectors.size()]);
		this.vertices = vertices.toArray(new Tri[vertices.size()]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Drawable clone()
	{
		Model3D c = new Model3D(this.scene);
		c.vectors = this.vectors.clone();
		c.vertices = this.vertices.clone();
		c.setPosition(this.getPosition().clone());
		c.color = this.color;
		c.setReflection(this.getReflection());
		c.setShininess(this.getShininess());
		c.animations = (LinkedList<Animation>) this.animations.clone();
		return c;
	}
}
