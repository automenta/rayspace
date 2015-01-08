package JavaRenderer;

public class Tri
{
	public Vector3D A, B, C;
	private Vector3D normal;

	public Tri(Vector3D a, Vector3D b, Vector3D c)
	{
		this.A = a;
		this.B = b;
		this.C = c;
	}

	public Tri(Vector3D a, Vector3D b, Vector3D c, Vector3D normal)
	{
		this.A = a;
		this.B = b;
		this.C = c;
		this.normal = normal;
	}

	public Vector3D normal()
	{
		if (normal == null)
		{
			Vector3D a = B.clone().subtract(A), b = C.clone().subtract(B);
			this.normal = a.multiply(b).normalize();
		}
		return this.normal.clone();
	}

	public Tri transform(Matrix m)
	{
		this.A.multiply(m);
		this.B.multiply(m);
		this.C.multiply(m);
		return this;
	}

	public Tri clone()
	{
		return new Tri(this.A, this.B, this.C);
	}
}
