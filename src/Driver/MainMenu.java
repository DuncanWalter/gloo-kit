package Driver;

import GlooKit.GlooAPI.DrawingObjects.Polygon;

import GlooKit.GlooAPI.Texture;
import GlooKit.GlooAPI.Vertex;
import GlooKit.GlooFramework.*;
import GlooKit.GlooFramework.Components.Drawable;
import GlooKit.GlooFramework.Components.Rect;

import java.util.List;

import static GlooKit.GlooFramework.KitBit.*;

public class MainMenu {
    public static void open(GlooApplication app){

        DefaultBatch batch = new DefaultBatch(app);
        batch.addTexture("Assets");
        batch.bindTextures();

        Texture blue          = batch.getTexture("ColorSheet");
        Texture physicsButton = batch.getTexture("Physics-Button");


        Drawable morpher = new Drawable(){
            // example of a REALLY complex anon class for you guys

            private double[] lengths = new double[200];
            private double[] growths = new double[200];
            private Polygon poly = new Polygon(batch);

            Drawable init(){

                poly.addVertex();
                for(int i = 0; i < 200; i++){
                    lengths[i] = 400.0;
                    growths[i] = 0.0;
                    poly.addVertex();
                }

                poly.close();

                poly.apply((Vertex v)->{
                    v.set(v.R(), 0.22f);
                    v.set(v.G(), 0.55f);
                    v.set(v.B(), 0.67f);
                    v.set(v.A(), 1f);
                });

                poly.vertices().get(0).set(poly.vertices().get(0).R(), 1);
                poly.vertices().get(0).set(poly.vertices().get(0).G(), 1);
                poly.vertices().get(0).set(poly.vertices().get(0).B(), 1);

                return this;

            }

            public void calcFrame(double delta, Input input){

            }

            public void stepFrame(double delta){
                // mutate the growths
                for (int i = 0; i < 200; i++){
                    growths[i] = (growths[i] + 65 * delta * (Math.random() * 2 - 1)) * 0.995;
                }
                // smooth it over a bit
                for (int i = 0; i < 200; i++){
                    growths[i%200] = (growths[i] * 3
                            + growths[(i+201)%200] * 2 + growths[(i+199)%200] * 2
                            + growths[(i+202)%200]     + growths[(i+198)%200]) / 9;
                }
                // grow the lengths
                for (int i = 0; i < 200; i++){
                    lengths[i] = lengths[i] + growths[i];
                }
                // smooth it over a bit
                for (int i = 0; i < 200; i++){
                    lengths[i%200] = (lengths[i] * 3
                            + lengths[(i+201)%200] * 2 + lengths[(i+199)%200] * 2
                            + lengths[(i+202)%200]     + lengths[(i+198)%200]) / 9;
                    lengths[i%200] = Math.max(Math.min(lengths[i], 500), 200);
                }

            }

            public void draw(float X, float Y, float W, float H, float Z){

                List<Vertex> vertices = poly.vertices();

                Vertex v = vertices.get(0);
                v.set(v.X(), X + W / 2).set(v.Y(), Y + H / 2).set(v.A(), 0.5f);

                double a = 0;
                for(int i = 1; i < 201; i++){
                    a += Math.PI * 2 / 200;
                    v = vertices.get(i);
                    v.set(v.X(), (float)(X + W / 2 + lengths[i-1] * Math.cos(a)));
                    v.set(v.Y(), (float)(Y + H / 2 + lengths[i-1] * Math.sin(a)));
                    v.set(v.Z(), Z);
                }

                poly.draw();
                poly.send();

            }

        }.init();

        new Room(app
                ,new Canvas(CENTER, CENTER, "1/n+", "1/n+", new Rect(batch, blue), null)
                ,new Canvas(CENTER, CENTER, "1/3", "1/3", morpher, null)
                ,new Nook(RIGHT, TOP, "1/5", "1/5", app
                    ,new Canvas(CENTER, CENTER, "1/n+", "1/n+", new Rect(batch, blue), null)
                )
                ,new Button(LEFT, BOTTOM, "100p", "equal", new Rect(batch, physicsButton), null, () ->{})
        ).configure();

    }
}
