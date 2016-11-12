package GlooKit.GlooFramework;

import GlooKit.GlooFramework.Components.Drawable;
import GlooKit.GlooFramework.Components.Label;

/**
 * A {@code Canvas} is a {@link KitBit KitBit} with a {@link Drawable frame} and a {@link Label label} that does not
 * have any children. By default, a {@code Canvas} can display text, an image, or both, but with specially designed
 * {@code frames} or {@code labels}, a {@code Canvas} can do practically anything.
 *
 * @see GlooKit.GlooFramework.KitBit
 *
 * @author Duncan Walter
 * @author Eli Jergensen
 * @since 1.0
 * */
public class Canvas extends KitBit {

    /**
     * Overloads the default {@link Canvas#Canvas(int, int, String, String, Drawable, Label) Canvas constructor} to not
     * take either a {@code frame} or a {@code label}.
     *
     * @param X int of the horizontal justification, as defined in the KitBit class
     * @param Y int of the vertical justification, as defined in the KitBit class
     * @param W a valid formatting String for setting the width, as defined in the KitBit class
     * @param H a valid formatting String for setting the height, as defined in the KitBit class
     * */
    public Canvas(int X, int Y, String W, String H){
        this(X, Y, W, H, null, null);
    }

    /**
     * Overloads the default {@link Canvas#Canvas(int, int, String, String, Drawable, Label) Canvas constructor} to take
     * a {@code frame}, but not a {@code label}.
     *
     * @param X int of the horizontal justification, as defined in the KitBit class
     * @param Y int of the vertical justification, as defined in the KitBit class
     * @param W a valid formatting String for setting the width, as defined in the KitBit class
     * @param H a valid formatting String for setting the height, as defined in the KitBit class
     * @param frame a Drawable, which determines how to draw this object on the screen
     * */
    public Canvas(int X, int Y, String W, String H, Drawable frame){
        this(X, Y, W, H, frame, null);
    }

    /**
     * Constructs a {@code Canvas} given a {@code frame} and a {@code label} in addition to the normal parameters of the
     * default {@link KitBit#KitBit(int, int, String, String) KitBit constructor}. {@code Canvas} does not take any
     * children.
     *
     * @param X int of the horizontal justification, as defined in the KitBit class
     * @param Y int of the vertical justification, as defined in the KitBit class
     * @param W a valid formatting String for setting the width, as defined in the KitBit class
     * @param H a valid formatting String for setting the height, as defined in the KitBit class
     * @param frame a Drawable, which determines how to draw this object on the screen
     * @param label a Label, which handles the the text and formatting of text of this element
     *
     * @see KitBit#KitBit(int, int, String, String)
     * */
    public Canvas(int X, int Y, String W, String H, Drawable frame, Label label){
        super(X, Y, W, H);
        setFrame(frame);
        setLabel(label);
    }

    /**
     * Overloads the default {@link Canvas#Canvas(int, int, String, String, Drawable, Label) Canvas constructor} to take
     * a {@code label}, but not a {@code frame}.
     *
     * @param X int of the horizontal justification, as defined in the KitBit class
     * @param Y int of the vertical justification, as defined in the KitBit class
     * @param W a valid formatting String for setting the width, as defined in the KitBit class
     * @param H a valid formatting String for setting the height, as defined in the KitBit class
     * @param label a Label, which handles the the text and formatting of text of this element
     * */
    public Canvas(int X, int Y, String W, String H, Label label){
        this(X, Y, W, H, null, label);
    }

}
