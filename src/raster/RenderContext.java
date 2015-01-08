package raster;


import java.util.List;
import java.util.ArrayList;
import tracer.objects.FastMath;

public class RenderContext extends Bitmap {

    private float[] m_zBuffer;
    List<Vertex> vertices = new ArrayList<>();
    List<Vertex> auxillaryList = new ArrayList<>();
    Matrix4f screenSpaceTransform = new Matrix4f();
    private Vertex minYVert;
    private Vertex midYVert;
    private Vertex maxYVert;
    private Gradients gradients = new Gradients();
        
    public RenderContext(int width, int height) {
        super(width, height);
        m_zBuffer = new float[width * height];
    }

    public void ClearDepthBuffer() {
        for (int i = 0; i < m_zBuffer.length; i++) {
            m_zBuffer[i] = Float.MAX_VALUE;
        }
    }

    public void DrawTriangle(Vertex v1, Vertex v2, Vertex v3, Bitmap texture) {
        if (v1.IsInsideViewFrustum() && v2.IsInsideViewFrustum() && v3.IsInsideViewFrustum()) {
            FillTriangle(v1, v2, v3, texture);
            return;
        }

        vertices.clear();
        auxillaryList.clear();

        vertices.add(v1);
        vertices.add(v2);
        vertices.add(v3);

        if (ClipPolygonAxis(vertices, auxillaryList, 0)
                && ClipPolygonAxis(vertices, auxillaryList, 1)
                && ClipPolygonAxis(vertices, auxillaryList, 2)) {
            Vertex initialVertex = vertices.get(0);

            for (int i = 1; i < vertices.size() - 1; i++) {
                FillTriangle(initialVertex, vertices.get(i), vertices.get(i + 1), texture);
            }
        }
    }

    private boolean ClipPolygonAxis(List<Vertex> vertices, List<Vertex> auxillaryList, int componentIndex) {
        ClipPolygonComponent(vertices, componentIndex, 1.0f, auxillaryList);
        vertices.clear();

        if (auxillaryList.isEmpty())
            return false;

        ClipPolygonComponent(auxillaryList, componentIndex, -1.0f, vertices);
        auxillaryList.clear();

        return !vertices.isEmpty();
    }

    private void ClipPolygonComponent(List<Vertex> vertices, int componentIndex,
            float componentFactor, List<Vertex> result) {
        
        Vertex previousVertex = vertices.get(vertices.size() - 1);
        float previousComponent = previousVertex.get(componentIndex) * componentFactor;
        boolean previousInside = previousComponent <= previousVertex.pos().w();

        final int nv = vertices.size();
        for (int i = 0; i < nv; i++) {
            Vertex currentVertex = vertices.get(i);
            
            float currentComponent = currentVertex.get(componentIndex) * componentFactor;
            boolean currentInside = currentComponent <= currentVertex.pos().w();

            if (currentInside ^ previousInside) {
                float lerpAmt = (previousVertex.pos().w() - previousComponent)
                        / ((previousVertex.pos().w() - previousComponent)
                        - (currentVertex.pos().w() - currentComponent));

                result.add(previousVertex.lerp(currentVertex, lerpAmt));
            }

            if (currentInside) {
                result.add(currentVertex);
            }

            previousVertex = currentVertex;
            previousComponent = currentComponent;
            previousInside = currentInside;
        }
    }

    final static Matrix4f identity = new Matrix4f().InitIdentity();
    private void FillTriangle(Vertex v1, Vertex v2, Vertex v3, Bitmap texture) {
        screenSpaceTransform.InitScreenSpaceTransform(width() / 2, height() / 2);
        
        
        
        minYVert = v1.transform(screenSpaceTransform, identity, minYVert).PerspectiveDivide();
        midYVert = v2.transform(screenSpaceTransform, identity, midYVert).PerspectiveDivide();
        maxYVert = v3.transform(screenSpaceTransform, identity, maxYVert).PerspectiveDivide();

        if (minYVert.TriangleAreaTimesTwo(maxYVert, midYVert) >= 0) {
            return;
        }

        if (maxYVert.y() < midYVert.y()) {
            Vertex temp = maxYVert;
            maxYVert = midYVert;
            midYVert = temp;
        }

        if (midYVert.y() < minYVert.y()) {
            Vertex temp = midYVert;
            midYVert = minYVert;
            minYVert = temp;
        }

        if (maxYVert.y() < midYVert.y()) {
            Vertex temp = maxYVert;
            maxYVert = midYVert;
            midYVert = temp;
        }

        scanTriangle(minYVert, midYVert, maxYVert,
                minYVert.TriangleAreaTimesTwo(maxYVert, midYVert) >= 0,
                texture);
    }

    private void scanTriangle(Vertex minYVert, Vertex midYVert,
            Vertex maxYVert, boolean handedness, Bitmap texture) {
        
        gradients.set(minYVert, midYVert, maxYVert);
        Edge topToBottom = new Edge(gradients, minYVert, maxYVert, 0);
        Edge topToMiddle = new Edge(gradients, minYVert, midYVert, 0);
        Edge middleToBottom = new Edge(gradients, midYVert, maxYVert, 1);

        scanEdges(gradients, topToBottom, topToMiddle, handedness, texture);
        scanEdges(gradients, topToBottom, middleToBottom, handedness, texture);
    }

    private void scanEdges(Gradients gradients, Edge a, Edge b, boolean handedness, Bitmap texture) {
        Edge left = a;
        Edge right = b;
        if (handedness) {
            Edge temp = left;
            left = right;
            right = temp;
        }

        int yStart = b.yStart();
        int yEnd = b.yEnd();
        for (int j = yStart; j < yEnd; j++) {
            scanLine(gradients, left, right, j, texture);
            left.step();
            right.step();
        }
    }

    private void scanLine(Gradients gradients, Edge left, Edge right, final int j, Bitmap texture) {
        int xMin = FastMath.ceil(left.x());
        int xMax = FastMath.ceil(right.x());
        float xPrestep = xMin - left.x();

//		float xDist = right.GetX() - left.GetX();
//		float texCoordXXStep = (right.GetTexCoordX() - left.GetTexCoordX())/xDist;
//		float texCoordYXStep = (right.GetTexCoordY() - left.GetTexCoordY())/xDist;
//		float oneOverZXStep = (right.GetOneOverZ() - left.GetOneOverZ())/xDist;
//		float depthXStep = (right.GetDepth() - left.GetDepth())/xDist;
		// Apparently, now that stepping is actually on pixel centers, gradients are
        // precise enough again.
        float texCoordXXStep = gradients.GetTexCoordXXStep();
        float texCoordYXStep = gradients.GetTexCoordYXStep();
        float oneOverZXStep = gradients.GetOneOverZXStep();
        float depthXStep = gradients.GetDepthXStep();
        float lightAmtXStep = gradients.GetLightAmtXStep();

        float texCoordX = left.texX() + texCoordXXStep * xPrestep;
        float texCoordY = left.texY() + texCoordYXStep * xPrestep;
        float oneOverZ = left.oneOverZ() + oneOverZXStep * xPrestep;
        float depth = left.depth() + depthXStep * xPrestep;
        float lightAmt = left.lightAmount() + lightAmtXStep * xPrestep;

        final int twidth = texture.width();
        final float twidthMin1 = (twidth - 1);
        final float tHeightMin1 = (texture.height() - 1);
        final int width = width();

        int jw = j * width;
        int queueSteps = 0;
        
        for (int i = xMin + jw; i < xMax + jw; i++) {
            
            
            if (depth < m_zBuffer[i]) {
                
                if (queueSteps > 0) {
                    oneOverZ += oneOverZXStep * queueSteps;
                    texCoordX += texCoordXXStep * queueSteps;
                    texCoordY += texCoordYXStep * queueSteps;
                    lightAmt += lightAmtXStep * queueSteps;
                    queueSteps = 0;
                }
                
                m_zBuffer[i] = depth;
                
                //TODO floating point texture coordinates, for shaders
                
                int srcX = (int) ((texCoordX / oneOverZ) * twidthMin1 + 0.5f);
                int srcY = (int) ((texCoordY / oneOverZ) * tHeightMin1 + 0.5f);

                int destIndex = i * 4;
                int srcIndex = (srcX + srcY * twidth) * 4;

                for (int t = 0; t < 4; t++)
                    pixels[destIndex++] = (byte)texture.pixLight(srcIndex++, lightAmt);
            }
            
            queueSteps++;
            depth += depthXStep;
        }
    }
}
