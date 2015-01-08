package raster.object;

import java.util.ArrayList;
import raster.Mesh;
import raster.Vector4f;

import raster.Vertex;

public class Cube extends Mesh {

	public Cube(final float height, final float width, final float depth)  {

            final Vector4f p0 = new Vector4f(0,0,0);
            m_vertices=new ArrayList(){{
		add(new Vertex(p0));
                add(new Vertex(p0.Add(new Vector4f(width, 0, 0))));
		add(new Vertex(p0.Add(new Vector4f(width, height, 0))));
		add(new Vertex(p0.Add(new Vector4f(0, height, 0))));

		add(new Vertex(p0.Add(new Vector4f(0, 0, depth))));
		add(new Vertex(p0.Add(new Vector4f(width, 0, depth))));
		add(new Vertex(p0.Add(new Vector4f(width, height, depth))));
		add(new Vertex(p0.Add(new Vector4f(0, height, depth))));
                
            }};
            
            m_indices = new ArrayList() {{
                //front
                add(2); add(1); add(0);
                add(0); add(3); add(2);
                
                //back
                add(7); add(4); add(5);
                add(5); add(6); add(7);
                
		//left
                add(3); add(0); add(4);
                add(4); add(7); add(3);
                
		//right
                add(6); add(5); add(1);
                add(1); add(2); add(6);
		
		//top
                add(6); add(2); add(3);
                add(3); add(7); add(6);

                //bottom
                add(1); add(5); add(4);
                add(4); add(0); add(1);

            }};

	}

}
