package unused.terrain.JavaRenderer;

import java.awt.Color;

public abstract class Light extends SceneObject
{
	public Color color;

	public Light(Scene frame)
	{
		super(frame);
		this.color = Color.white;
	}

	public Light(Scene frame, Color color)
	{
		super(frame);
		this.color = color;
	}

	public abstract Color shade(Drawable d, Color c, Tri v);

	@Override
	public SceneObject transform(Transformation m)
	{
		return null;
	}

}
