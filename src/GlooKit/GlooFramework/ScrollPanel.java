package GlooKit.GlooFramework;

import GlooKit.GlooFramework.Components.Drawable;
import GlooKit.GlooFramework.Components.Label;

/**
 * ScrollPanel is a Canvas, but can scroll
 *
 *
 * ScrollPanel extends Canvas
 * @see Canvas
 *
 * @author James Terry
 * Documentor: James Terry
 */
public class ScrollPanel extends Canvas {

    //private Consumer<T>;
    //private List<T>;
    public ScrollPanel(int X, int Y, String W, String H)  {
        super(X, Y, W, H);
    }

    public ScrollPanel(int X, int Y, String W, String H, Label label){
        super(X, Y, W, H, null, label);
    }

    public ScrollPanel(int X, int Y, String W, String H, Drawable frame){
        super(X, Y, W, H, frame, null);
    }

    public ScrollPanel(int X, int Y, String W, String H, Drawable frame, Label label){
        super(X, Y, W, H);
        setFrame(frame);
        setLabel(label);
    }



}
