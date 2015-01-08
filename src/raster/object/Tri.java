package raster.object;

import raster.Matrix4f;
import raster.Mesh;
import raster.Vector4f;

public class Tri extends Mesh {

    public Vector4f A, B, C;
    private Vector4f normal;

    public Tri(Vector4f a, Vector4f b, Vector4f c) {
        this(a, b, c, null);
    }

    public Tri(Vector4f a, Vector4f b, Vector4f c, Vector4f normal) {
        super();
        this.A = a;
        this.B = b;
        this.C = c;
        this.normal = normal;
        this.normal = normal();
    }

    public Vector4f normal() {
        if (normal == null) {
            Vector4f a = B.Sub(A), b = C.Sub(B);
            this.normal = a.Mul(b).normalizedSelf();
        }
        return this.normal;
    }

    public Tri transform(Matrix4f m) {
        m.transform(A, A);
        m.transform(B, B);
        m.transform(C, C);
        return this;
    }

    public Tri clone() {
        return new Tri(this.A, this.B, this.C);
    }
}
