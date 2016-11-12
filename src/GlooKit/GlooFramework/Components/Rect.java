package GlooKit.GlooFramework.Components;

import GlooKit.GlooAPI.DrawingObjects.Quad;
import GlooKit.GlooAPI.Texture;
import GlooKit.GlooAPI.Vertex;
import GlooKit.GlooFramework.GlooApplication;
import GlooKit.GlooFramework.Input;

import static GlooKit.GlooAPI.GlooCore.DEFAULT;

public class Rect implements Drawable{

    private Quad quad;
    private Texture texture;
    private float r;
    private float g;
    private float b;
    private float a;

    private Rect(GlooApplication app, int batchHandle){
        quad = new Quad(app, batchHandle);
        texture = app.getBatch(DEFAULT).getTexture("null");
        r = 1.0f;
        b = 1.0f;
        g = 1.0f;
        a = 1.0f;
    }

    public Rect(Texture texture, int batchHandle){
        // TODO should textures be made to know if they contain any transparency?
        this(texture.app, batchHandle);
        this.texture = texture;
    }

    public Rect(GlooApplication app, float R, float G, float B, float A){
        this(app, DEFAULT);
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

    }

    public void calcFrame(double delta, Input input) {

    }

    public void stepFrame(double delta) {

    }
}
