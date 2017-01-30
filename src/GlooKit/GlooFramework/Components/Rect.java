package GlooKit.GlooFramework.Components;

import GlooKit.GlooAPI.DrawingObjects.Quad;
import GlooKit.GlooAPI.GlooBatch;
import GlooKit.GlooAPI.Texture;
import GlooKit.GlooAPI.Vertex;
import GlooKit.GlooFramework.Input;

public class Rect implements Drawable{

    private Quad quad;
    private Texture texture;
    private float r;
    private float g;
    private float b;
    private float a;

    private Rect(GlooBatch context){
        quad = new Quad(context);
        texture = context.getTexture("null");
        r = 1.0f;
        b = 1.0f;
        g = 1.0f;
        a = 1.0f;
    }

    public Rect(GlooBatch context, Texture texture){
        this(context);
        this.texture = texture;
    }

    public Rect(GlooBatch context, float R, float G, float B, float A){
        this(context);
        r = R;
        b = B;
        g = G;
        a = A;
    }

    public void draw(float X, float Y, float W, float H, float Z) {

        quad.exhume(X, Y, W, H, Z);

        Vertex v;

        v = quad.vertex(0);
        v.set(v.S(), texture.S());
        v.set(v.T(), texture.V());
        v.set(v.R(), r).set(v.G(), g);
        v.set(v.B(), b).set(v.A(), a);

        v = quad.vertex(1);
        v.set(v.S(), texture.S());
        v.set(v.T(), texture.T());
        v.set(v.R(), r).set(v.G(), g);
        v.set(v.B(), b).set(v.A(), a);

        v = quad.vertex(2);
        v.set(v.S(), texture.U());
        v.set(v.T(), texture.T());
        v.set(v.R(), r).set(v.G(), g);
        v.set(v.B(), b).set(v.A(), a);

        v = quad.vertex(3);
        v.set(v.S(), texture.U());
        v.set(v.T(), texture.V());
        v.set(v.R(), r).set(v.G(), g);
        v.set(v.B(), b).set(v.A(), a);

        quad.draw();
        quad.send();

    }

    public void calcFrame(double delta, Input input) {

    }

    public void stepFrame(double delta) {

    }
}
