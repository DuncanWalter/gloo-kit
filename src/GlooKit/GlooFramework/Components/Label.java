package GlooKit.GlooFramework.Components;

import GlooKit.GlooAPI.DrawingObjects.Quad;
import GlooKit.GlooAPI.GlooCharacter;
import GlooKit.GlooAPI.GlooFontFamily;
import GlooKit.GlooAPI.Vertex;
import GlooKit.GlooFramework.GlooApplication;
import GlooKit.GlooFramework.Input;
import GlooKit.GlooFramework.TextBatch;
import GlooKit.Utils.Vector;

import java.util.ArrayList;
import java.util.LinkedList;

import static GlooKit.GlooAPI.GlooCore.TEXT;
import static GlooKit.GlooAPI.GlooFontFamily.GLOOFONT_PLAIN;



/**
 * Label have text
 * Label are a component of a KitBit.
 * @see GlooKit.GlooFramework.KitBit
 *
 * Label can be dynamic by overriding the getText
 * Label may also have a stepFrame and calcFrame method, which are called when the KitBit has stepFrame or calcFrame called on it
 * @see GlooKit.GlooFramework.KitBit#stepFrame(double)
 * @see GlooKit.GlooFramework.KitBit#calcFrame(double, Input)
 *
 *
 * Authors: Duncan Walter, Eli Jergensen
 * Documenter: Eli Jergensen
 * */
public class Label implements Drawable{

    //TODO implement text <requires implementing textures>

    //TODO consider letting labels also contain some sort of formatting

    // ANONYMOUS CLASS BOILERPLATE

    private GlooApplication app;
    private GlooFontFamily font;

    private boolean formatted;
    private boolean monoLined;
    private int pointSize;
    private int fontStyle;
    private TextBatch batch;

    private float[] validator;


    private LinkedList<Character> text;
    private ArrayList<Quad> quads;

    public Label(GlooApplication app, String font, String text){
        this.app = app;
        this.batch = (TextBatch)app.getBatch(TEXT);
        this.quads = new ArrayList<>();
        this.font = batch.getFontFamily(font);
        this.text = new LinkedList<>();
        for(int i = 0; i < text.length(); i++){
            this.text.add(text.charAt(i));
        }
        this.validator = new float[4];

        this.pointSize = 24;
        this.fontStyle = GLOOFONT_PLAIN;

        formatted = false;
    }

    public void draw(float X, float Y, float W, float H, float Z){
        format(X, Y, W, H, Z);

        formatted = (X==validator[0]) && (Y==validator[1])
                 && (W==validator[2]) && (H==validator[3]);
        validator[0] = X;
        validator[1] = Y;
        validator[2] = W;
        validator[3] = H;

        for(Quad q : quads){
            if(q != null){
                q.draw();
            }
        }

    }

    public void format(float X, float Y, float W, float H, float Z){
        // TODO multi line, line detection, overflow ellipses,
        if(!formatted){
            formatted = true;
            int i = 0; // injected index iterator
            Vector offset = new Vector(app.getSpacing(), H - app.getSpacing() - font.getCharHeight(pointSize), 0);
            for(Character c : text){
                if(quads.size() == i){quads.add(null);}
                if(quads.get(i) == null){quads.set(i, new Quad(app, TEXT));}

                Quad quad = quads.get(i);
                GlooCharacter character = font.getCharacter(fontStyle, c);
                quad.exhume(X + offset.x() + character.x(pointSize)
                           ,Y + offset.y() + character.y(pointSize)
                           ,character.w(pointSize)
                           ,character.h(pointSize)
                           ,Z + 0.1f);

                quad.apply(0, (Vertex v) -> {v.set(v.S(), character.s()); v.set(v.T(), character.v());});
                quad.apply(1, (Vertex v) -> {v.set(v.S(), character.s()); v.set(v.T(), character.t());});
                quad.apply(2, (Vertex v) -> {v.set(v.S(), character.u()); v.set(v.T(), character.t());});
                quad.apply(3, (Vertex v) -> {v.set(v.S(), character.u()); v.set(v.T(), character.v());});

                quad.apply((Vertex v) -> {
                    v.set(v.R(), 1);
                    v.set(v.G(), 1);
                    v.set(v.B(), 1);
                    v.set(v.A(), 1);
                });

                offset = new Vector(offset.x() + character.advance(pointSize), offset.y(), 0);

                i += 1;
            }
        }
    }

    public void stepFrame(double delta){}
    public void calcFrame(double delta, Input input){}


    public float calculateH(){
        return font.getCharHeight(pointSize);

    }

    public float calculateW(){
        // TODO
        System.out.println("Label Calc Width is undefined");
        return Float.NaN;
    }

//    public String getText(){
//        return text;
//
//    }
}
