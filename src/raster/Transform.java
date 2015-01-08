package raster;

public class Transform
{
	private Vector4f   m_pos;
	private Quaternion m_rot;
	private Vector4f   m_scale;
        
        transient private Matrix4f translationMatrix = new Matrix4f();
        transient private Matrix4f scaleMatrix = new Matrix4f();
        transient private Matrix4f rotationMatrix = new Matrix4f();

	public Transform()
	{
		this(new Vector4f(0,0,0,0));
	}

	public Transform(Vector4f pos)
	{
		this(pos, new Quaternion(0,0,0,1), new Vector4f(1,1,1,1));
	}

	public Transform(Vector4f pos, Quaternion rot, Vector4f scale)
	{
		m_pos = pos;
		m_rot = rot;
		m_scale = scale;
	}

	public Transform SetPos(Vector4f pos)
	{
		return new Transform(pos, m_rot, m_scale);
	}

	public Transform Rotate(Quaternion rotation)
	{
		return new Transform(m_pos, rotation.Mul(m_rot).Normalized(), m_scale);
	}

	public Transform LookAt(Vector4f point, Vector4f up)
	{
		return Rotate(GetLookAtRotation(point, up));
	}

	public Quaternion GetLookAtRotation(Vector4f point, Vector4f up)
	{
		return new Quaternion(new Matrix4f().InitRotation(point.Sub(m_pos).normalized(), up));
	}

	public Matrix4f transformation()
	{
		translationMatrix.InitTranslation(m_pos.x(), m_pos.y(), m_pos.z());
		m_rot.ToRotationMatrix(rotationMatrix);
		scaleMatrix.InitScale(m_scale.x(), m_scale.y(), m_scale.z());

		return translationMatrix.Mul(rotationMatrix.Mul(scaleMatrix));
	}

	public Vector4f GetTransformedPos()
	{
		return m_pos;
	}

	public Quaternion GetTransformedRot()
	{
		return m_rot;
	}

	public Vector4f GetPos()
	{
		return m_pos;
	}

	public Quaternion GetRot()
	{
		return m_rot;
	}

	public Vector4f GetScale()
	{
		return m_scale;
	}
}
