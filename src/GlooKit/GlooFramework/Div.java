package GlooKit.GlooFramework;

/**
 * A {@code Div} is like an HTML Div. They have children and ensure that their children get drawn by calling
 * {@link KitBit#drawChildren(float) drawChildren(float)} every time
 * {@link KitBit#drawFrame(float, float, float, float, float) drawFrame(float, float, float, float)} is called on them.
 *
 * @see KitBit
 *
 * @author Eli Jergensen
 * @author Duncan Walter
 * @since 1.0
 * */
public class Div extends KitBit{

    /**
     * Constructs a {@code Div} with the normal parameters of the default
     * {@link KitBit#KitBit(int, int, String, String, KitBit...) KitBit constructor}.
     *
     * @param X int of the horizontal justification, as defined in the KitBit class
     * @param Y int of the vertical justification, as defined in the KitBit class
     * @param W a valid formatting String for setting the width, as defined in the KitBit class
     * @param H a valid formatting String for setting the height, as defined in the KitBit class
     * @param children possibly many KitBits, which are children of this Div. Can take any number of KitBits
     * */
    public Div(int X, int Y, String W, String H, KitBit... children){
        super(X, Y, W, H, children);
    }

    /**
     * Overrides the
     * {@link KitBit#drawFrame(float, float, float, float, float) drawFrame(float, float, float, float, float)}
     * method of {@code KitBit} to also call the {@link KitBit#drawChildren(float) drawChildren(float)} method of
     * {@code KitBit}, which ensures that {@code drawFrame()} is recursively called on the children of this {@code Div}
     * */
    public void drawFrame(float X, float Y, float W, float H, float Z){
        super.drawFrame(X, Y, W, H, Z);

        drawChildren(Z);
    }
}
