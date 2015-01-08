package raster;


import tracer.objects.FastMath;

public class Edge
{
	private float m_x;
	private final float m_xStep;
	private final int m_yStart;
	private final int m_yEnd;
	private float m_texCoordX;
	private final float m_texCoordXStep;
	private float m_texCoordY;
	private final float m_texCoordYStep;
	private float m_oneOverZ;
	private final float m_oneOverZStep;
	private float m_depth;
	private final float m_depthStep;
	private float m_lightAmt;
	private final float m_lightAmtStep;

	public float x() { return m_x; }
	public int yStart() { return m_yStart; }
	public int yEnd() { return m_yEnd; }
	public float texX() { return m_texCoordX; }
	public float texY() { return m_texCoordY; }
	public float oneOverZ() { return m_oneOverZ; }
	public float depth() { return m_depth; }
	public float lightAmount() { return m_lightAmt; }

	public Edge(Gradients gradients, Vertex minYVert, Vertex maxYVert, int minYVertIndex)
	{
		m_yStart = (int)FastMath.ceil(minYVert.y());
		m_yEnd = (int)FastMath.ceil(maxYVert.y());

		float yDist = maxYVert.y() - minYVert.y();
		float xDist = maxYVert.x() - minYVert.x();

		float yPrestep = m_yStart - minYVert.y();
		m_xStep = (float)xDist/(float)yDist;
		m_x = minYVert.x() + yPrestep * m_xStep;
		float xPrestep = m_x - minYVert.x();

		m_texCoordX = gradients.GetTexCoordX(minYVertIndex) +
			gradients.GetTexCoordXXStep() * xPrestep +
			gradients.GetTexCoordXYStep() * yPrestep;
		m_texCoordXStep = gradients.GetTexCoordXYStep() + gradients.GetTexCoordXXStep() * m_xStep;

		m_texCoordY = gradients.GetTexCoordY(minYVertIndex) +
			gradients.GetTexCoordYXStep() * xPrestep +
			gradients.GetTexCoordYYStep() * yPrestep;
		m_texCoordYStep = gradients.GetTexCoordYYStep() + gradients.GetTexCoordYXStep() * m_xStep;

		m_oneOverZ = gradients.GetOneOverZ(minYVertIndex) +
			gradients.GetOneOverZXStep() * xPrestep +
			gradients.GetOneOverZYStep() * yPrestep;
		m_oneOverZStep = gradients.GetOneOverZYStep() + gradients.GetOneOverZXStep() * m_xStep;

		m_depth = gradients.GetDepth(minYVertIndex) +
			gradients.GetDepthXStep() * xPrestep +
			gradients.GetDepthYStep() * yPrestep;
		m_depthStep = gradients.GetDepthYStep() + gradients.GetDepthXStep() * m_xStep;

		m_lightAmt = gradients.GetLightAmt(minYVertIndex) +
			gradients.GetLightAmtXStep() * xPrestep +
			gradients.GetLightAmtYStep() * yPrestep;
		m_lightAmtStep = gradients.GetLightAmtYStep() + gradients.GetLightAmtXStep() * m_xStep;
	}

	public final void step()
	{
		m_x += m_xStep;
		m_texCoordX += m_texCoordXStep;
		m_texCoordY += m_texCoordYStep;
		m_oneOverZ += m_oneOverZStep;
		m_depth += m_depthStep;
		m_lightAmt += m_lightAmtStep;
	}
}
