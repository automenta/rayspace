package unused.terrain.Objects3D;

import java.awt.*;

import unused.terrain.JavaRenderer.DrawMode;
import unused.terrain.JavaRenderer.Drawable;
import unused.terrain.JavaRenderer.Scene;
import unused.terrain.JavaRenderer.Vector3D;
import unused.terrain.JavaRenderer.Tri;

public class Line3D extends Drawable
{
	public int width;

	private Line3D(Scene frame)
	{
		super(frame);
	}

	public Line3D(Scene frame, Vector3D a, Vector3D b, Color color, int width)
	{
		super(frame);

		this.vertices = new Tri[] { new Tri(a, b, a) };
		this.color = color;
		this.width = width;
		this.drawMode = DrawMode.FlatOutline;
		this.vectors = new Vector3D[] { a, b };
	}

	@Override
	public Drawable clone()
	{
		Line3D c = new Line3D(this.scene);
		c.vectors = this.vectors;
		c.vertices = this.vertices;
		c.setPosition(this.getPosition().clone());
		c.color = this.color;
		c.setReflection(this.getReflection());
		c.setShininess(this.getShininess());
		return c;
	}
}
