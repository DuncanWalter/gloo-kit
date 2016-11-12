package GlooKit.GlooAPI.DrawingObjects;

import GlooKit.GlooAPI.Vertex;
import GlooKit.GlooFramework.GlooApplication;

public class Quad extends Polygon {

    public Quad(GlooApplication app, int batchHandle) {
        super(app, batchHandle);

        addVertex();
        addVertex();
        addVertex();
        addVertex();

        // We want to draw the 4 vertices as two triangles:
        indices().add((short)0);
        indices().add((short)1);
        indices().add((short)2);
        indices().add((short)0);
        indices().add((short)2);
        indices().add((short)3);
    }

    public void exhume(float X, float Y, float W, float H, float Z){
        Vertex v;
        v = vertex(0);
        v.set(v.X(), X    ).set(v.Y(), Y    ).set(v.Z(), Z);
        v = vertex(1);
        v.set(v.X(), X    ).set(v.Y(), Y + H).set(v.Z(), Z);
        v = vertex(2);
        v.set(v.X(), X + W).set(v.Y(), Y + H).set(v.Z(), Z);
        v = vertex(3);
        v.set(v.X(), X + W).set(v.Y(), Y    ).set(v.Z(), Z);
    }

//    protected void setZ(float z) {
//        for (int i = 0; i < vertices.length; i++) {
//            vertices[i].setZ(z);
//        }
//    }
//    public void setColor(String hash) {
//        setColor(hash, 1.0f);
//
//    }
//    public void setColor(String hash, float a) {
//        Color color = Color.decode(hash);
//        float r = color.getRed()/255.0f;
//        float g = color.getGreen()/255.0f;
//        float b = color.getBlue()/255.0f;
//        setColor(r, g, b, a);
//    }
//    protected void setColor(float r, float g, float b, float a) {
//        for (int i = 0; i < vertices.length; i++) {
//            vertices[i].setRGBA(r, g, b, a);
//        }
//    }

}
