package raster;


import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

public class Mesh {

    protected List<Vertex> m_vertices;
    protected List<Integer> m_indices;
    
    transient Vertex ta = new Vertex(), tb = new Vertex(), tc = new Vertex();

    public Mesh() {
        m_vertices = new ArrayList();
        m_indices = new ArrayList();
    }
    
    public Mesh(String fileName) throws IOException {
        IndexedModel model = new OBJModel(fileName).ToIndexedModel();

        m_vertices = new ArrayList<Vertex>(model.GetPositions().size());
        for (int i = 0; i < model.GetPositions().size(); i++) {
            m_vertices.add(new Vertex(
                    model.GetPositions().get(i),
                    model.GetTexCoords().get(i),
                    model.GetNormals().get(i)));
        }

        m_indices = model.GetIndices();
    }

    public void Draw(RenderContext context, Matrix4f viewProjection, Matrix4f transform, Bitmap texture) {
        Matrix4f mvp = viewProjection.Mul(transform);
        for (int i = 0; i < m_indices.size(); i += 3) {
            context.DrawTriangle(
                    m_vertices.get(m_indices.get(i)).transform(mvp, transform, ta),
                    m_vertices.get(m_indices.get(i + 1)).transform(mvp, transform, tb),
                    m_vertices.get(m_indices.get(i + 2)).transform(mvp, transform, tc),
                    texture);
        }
    }
}
