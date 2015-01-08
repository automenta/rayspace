package raster;


/**
 * @file @author Benny Bobaganoosh <thebennybox@gmail.com>
 * @section LICENSE
 *
 * Copyright (c) 2014, Benny Bobaganoosh All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
public class Vertex {

    private Vector4f m_pos;
    private Vector4f m_texCoords;
    private Vector4f m_normal;

    public Vertex(Vector4f position) {
        this(position, new Vector4f(), new Vector4f(0,0,1));        
    }
    
    /**
     * Basic Getter
     */
    public final float x() {
        return m_pos.x();
    }

    /**
     * Basic Getter
     */
    public final float y() {
        return m_pos.y();
    }

    public final Vector4f pos() {
        return m_pos;
    }

    public final Vector4f GetTexCoords() {
        return m_texCoords;
    }

    public final Vector4f GetNormal() {
        return m_normal;
    }

    /**
     * empty
     */
    public Vertex() {
        m_pos = new Vector4f();
        m_texCoords = new Vector4f();
        m_normal = new Vector4f();
    }

    /**
     * Creates a new Vertex in a usable state.
     */
    public Vertex(Vector4f pos, Vector4f texCoords, Vector4f normal) {
        m_pos = pos;
        m_texCoords = texCoords;
        m_normal = normal;
    }

    public Vertex set(Vector4f pos, Vector4f texCoords, Vector4f normal) {
        m_pos.set(pos);
        m_texCoords.set(texCoords);
        m_normal.set(normal);
        return this;
    }
    public Vertex set(Vertex other) {
        m_pos.set(other.m_pos);
        m_texCoords.set(other.m_texCoords);
        m_normal.set(other.m_normal);
        return this;
    }

    public Vertex transform(Matrix4f transform, Matrix4f normalTransform) {
        // The normalized here is important if you're doing scaling.
        return new Vertex(transform.Transform(m_pos), m_texCoords,
                normalTransform.Transform(m_normal).normalized());
    }

    public Vertex transform(Matrix4f transform, Matrix4f normalTransform, Vertex result) {
        if (result == null)
            result = new Vertex();

        // The normalized here is important if you're doing scaling.
        return result.set(transform.transform(m_pos, result.m_pos), m_texCoords,
                normalTransform.transform(m_normal, result.m_normal).normalized());

    }

    public Vertex PerspectiveDivide() {
        return PerspectiveDivide(this);
    }

    public Vertex PerspectiveDivide(Vertex result) {
        result.m_pos.set(m_pos.x() / m_pos.w(), m_pos.y() / m_pos.w(), m_pos.z() / m_pos.w(), m_pos.w());
        if (result!=this) {
            result.m_texCoords.set(m_texCoords);
            result.m_normal.set(m_normal);
        }
        return result;
    }

    public float TriangleAreaTimesTwo(Vertex b, Vertex c) {
        float x1 = b.x() - m_pos.x();
        float y1 = b.y() - m_pos.y();

        float x2 = c.x() - m_pos.x();
        float y2 = c.y() - m_pos.y();

        return (x1 * y2 - x2 * y1);
    }

    public Vertex lerp(Vertex other, float lerpAmt) {
        return new Vertex(
                m_pos.Lerp(other.pos(), lerpAmt),
                m_texCoords.Lerp(other.GetTexCoords(), lerpAmt),
                m_normal.Lerp(other.GetNormal(), lerpAmt)
        );
    }

    public boolean IsInsideViewFrustum() {
        return Math.abs(m_pos.x()) <= Math.abs(m_pos.w())
                && Math.abs(m_pos.y()) <= Math.abs(m_pos.w())
                && Math.abs(m_pos.z()) <= Math.abs(m_pos.w());
    }

    public float get(int index) {
        switch (index) {
            case 0:
                return m_pos.x();
            case 1:
                return m_pos.y();
            case 2:
                return m_pos.z();
            case 3:
                return m_pos.w();
            default:
                throw new IndexOutOfBoundsException();
        }
    }
}
