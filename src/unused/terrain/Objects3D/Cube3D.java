package unused.terrain.Objects3D;

import java.awt.Color;

import unused.terrain.JavaRenderer.Drawable;
import unused.terrain.JavaRenderer.Scene;
import unused.terrain.JavaRenderer.Vector3D;
import unused.terrain.JavaRenderer.Tri;

public class Cube3D extends Drawable
{
	private Cube3D(Scene frame)
	{
		super(frame);
	}

	public Cube3D(Scene frame, Vector3D x, double height, double width, double depth, Color color)
	{
		super(frame);
		this.vertices = getVertices(frame, x, height, width, depth, color);
		this.color = color;
	}

	private Tri[] getVertices(Scene frame, Vector3D pos, double height, double width, double depth, Color color2)
	{

		Vector3D[] p = new Vector3D[8];

		this.setPosition(pos);

		p[0] = new Vector3D(-width / 2, -height / 2, -depth / 2);
		p[1] = p[0].clone().add(new Vector3D(width, 0, 0));
		p[2] = p[0].clone().add(new Vector3D(width, height, 0));
		p[3] = p[0].clone().add(new Vector3D(0, height, 0));

		p[4] = p[0].clone().add(new Vector3D(0, 0, depth));
		p[5] = p[0].clone().add(new Vector3D(width, 0, depth));
		p[6] = p[0].clone().add(new Vector3D(width, height, depth));
		p[7] = p[0].clone().add(new Vector3D(0, height, depth));

		Tri[] v = new Tri[12];
		v[0] = new Tri(p[2], p[1], p[0]); // front
		v[1] = new Tri(p[0], p[3], p[2]);
		v[2] = new Tri(p[7], p[4], p[5]); // back
		v[3] = new Tri(p[5], p[6], p[7]);
		v[4] = new Tri(p[3], p[0], p[4]); // left
		v[5] = new Tri(p[4], p[7], p[3]);
		v[6] = new Tri(p[6], p[5], p[1]); // right
		v[7] = new Tri(p[1], p[2], p[6]);
		v[8] = new Tri(p[6], p[2], p[3]); // top
		v[9] = new Tri(p[3], p[7], p[6]);
		v[10] = new Tri(p[1], p[5], p[4]); // bottom
		v[11] = new Tri(p[4], p[0], p[1]);

		this.vectors = p;

		return v;
	}

	@Override
	public Drawable clone()
	{
		Cube3D c = new Cube3D(this.scene);
		c.vectors = this.vectors;
		c.vertices = this.vertices;
		c.setPosition(this.getPosition().clone());
		c.color = this.color;
		c.setReflection(this.getReflection());
		c.setShininess(this.getShininess());
		return c;
	}
}
