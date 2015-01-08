package Objects3D;

import java.awt.Color;

import JavaRenderer.Drawable;
import JavaRenderer.Scene;
import JavaRenderer.Vector3D;
import JavaRenderer.Tri;

public class Cylinder3D extends Drawable
{
	private Cylinder3D(Scene frame)
	{
		super(frame);
	}

	public Cylinder3D(Scene frame, Color c, int steps, double radius, double height)
	{
		super(frame);
		this.color = c;
		this.setShininess(1);
		this.setReflection(this.color);
		this.vertices = getVertices(frame, steps, radius, height);
	}

	private Tri[] getVertices(Scene frame, int steps, double radius, double height)
	{
		Vector3D top = new Vector3D(0, height / 2, 0);
		Vector3D bottom = new Vector3D(0, -height / 2, 0);
		Tri[] vs = new Tri[steps * 4];

		this.vectors = new Vector3D[2 * steps + 2];
		this.vectors[0] = top;
		this.vectors[1] = bottom;

		Vector3D vLastTop = new Vector3D(radius, 0, 0).add(top);
		Vector3D vFirstTop = vLastTop;
		Vector3D vLastBottom = new Vector3D(radius, 0, 0).add(bottom);
		Vector3D vFirstBottom = vLastBottom;

		Vector3D vCurrent;
		Vector3D vCurrentBottom;
		Vector3D vCurrentTop;
		for (int i = 1; i <= steps; i++)
		{

			if (i == steps)
			{
				vCurrentTop = vFirstTop;
				vCurrentBottom = vFirstBottom;
			} else
			{
				double angle = Math.PI * 2 / steps * i;
				vCurrent = new Vector3D(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);
				vCurrentTop = vCurrent.clone().add(top);
				vCurrentBottom = vCurrent.clone().add(bottom);
			}

			Tri vFront = new Tri(vCurrentTop, vLastTop, top);
			Tri vBack = new Tri(bottom, vLastBottom, vCurrentBottom);

			Tri vSide1 = new Tri(vFront.B, vFront.A, vBack.C);
			Tri vSide2 = new Tri(vBack.C, vBack.B, vFront.B);

			vs[i - 1] = vFront;
			vs[i + steps - 1] = vBack;
			vs[i + 2 * steps - 1] = vSide1;
			vs[i + 3 * steps - 1] = vSide2;

			this.vectors[i + 1] = vCurrentTop;
			this.vectors[i + steps + 1] = vCurrentBottom;

			vLastTop = vCurrentTop;
			vLastBottom = vCurrentBottom;
		}
		return vs;
	}

	@Override
	public Drawable clone()
	{
		Cylinder3D c = new Cylinder3D(this.scene);
		c.vectors = this.vectors;
		c.vertices = this.vertices;
		c.setPosition(this.getPosition().clone());
		c.color = this.color;
		c.setReflection(this.getReflection());
		c.setShininess(this.getShininess());
		return c;
	}
}
