package GlooKit.GlooAPI.DrawingObjects;


import GlooKit.GlooAPI.GlooBatch;
import GlooKit.GlooAPI.Vertex;
import GlooKit.GlooFramework.GlooApplication;

public class Ellipse extends Polygon {

    private int segments;
    private boolean centered;

    public Ellipse(GlooBatch context, int segments, boolean centered){
        super(context);
        load(segments);
        this.segments = segments;
        this.centered = centered;
    }

    public Ellipse exhume(float X, float Y, float W, float H, float Z){
        int segment = 0;
        if(centered){
            for(double i = 0; i < Math.PI * 2; i += Math.PI * 2 / segments){
                Vertex v = vertex(segment);
                v.set(v.X(), (float)(X+W*0.5*Math.cos(i)));
                v.set(v.Y(), (float)(Y+H*0.5*Math.sin(i)));
                v.set(v.Z(), Z);
                segment += 1;
            }
        } else {
            for(double i = 0; i < Math.PI * 2; i += Math.PI * 2 / segments){
                Vertex v = vertex(segment);
                v.set(v.X(), (float)(X+W*0.5-W*0.5*Math.cos(i)));
                v.set(v.Y(), (float)(Y+H*0.5-H*0.5*Math.sin(i)));
                v.set(v.Z(), Z);
                segment += 1;
            }
        }
        return this;
    }

}
