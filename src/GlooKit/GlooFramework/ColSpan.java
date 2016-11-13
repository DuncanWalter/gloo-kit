package GlooKit.GlooFramework;

/**
 * A {@code ColSpan} is a {@link Div Div} which has a list of children that will be drawn vertically from top to bottom
 * in the order added.
 *
 * @see Div
 *
 * @author Duncan Walter
 * @author Eli Jergensen
 * @since 1.0
 */
// TODO clipping off-screen draws during drawFrame
public class ColSpan extends Div{

    /**
     * Overloads the default {@link ColSpan#ColSpan(int, int, String, String, KitBit...) ColSpan constructor} to only
     * take a list of children. Defaults to being right and bottom justified, having no spacing, and taking up all the
     * width and height it can.
     *
     * @param children possibly many KitBits, which are children of this ColSpan. Can take any number of KitBits
     * */
    public ColSpan(KitBit... children){
        this(R, B, "1/n+", "1/n+", children);
    }

    /**
     * Overloads the default {@link ColSpan#ColSpan(int, int, String, String, KitBit...) ColSpan constructor} to only
     * take a horizontal justification, a formatting String for setting the width, and a list of children. Defaults to
     * being bottom justified, having no vertical spacing, and taking up all the height it can.
     *
     * @param X int of the horizontal justification, as defined in the KitBit class
     * @param W a valid formatting String for setting the width, as defined in the KitBit class
     * @param children possibly many KitBits, which are children of this ColSpan. Can take any number of KitBits
     * */
    public ColSpan(int X, String W, KitBit... children){
        this(X, B, W, "1/n+", children);
    }

    /**
     * Constructs a {@code ColSpan} with the normal parameters of the default
     * {@link KitBit#KitBit(int, int, String, String, KitBit...) KitBit constructor}.
     *
     * @param X int of the horizontal justification, as defined in the KitBit class
     * @param Y int of the vertical justification, as defined in the KitBit class
     * @param W a valid formatting String for setting the width, as defined in the KitBit class
     * @param H a valid formatting String for setting the height, as defined in the KitBit class
     * @param children possibly many KitBits, which are children of this ColSpan. Can take any number of KitBits
     * */
    public ColSpan(int X, int Y, String W, String H, KitBit... children){
        super(X, Y, W, H, children);
    }

    /**
     * Overrides the
     * {@link Div#drawFrame(float, float, float, float, float) drawFrame(float, float, float, float, float)} method
     * of {@code Div} to properly calculate the position of all of the children of this {@code ColSpan} before calling
     * {@code drawFrame} on each of the children.
     * */
    public void drawFrame(float X, float Y, float W, float H, float Z){
        super.drawFrame(X, Y, W, H, Z);

        float spanPoints = calculateHeightSpanPoints(getChildren()); // Calculate the number of points for fixed height children
        float spanSpace = calculateHeightSpanSpace(getChildren()); // Calculate the fraction of width available for flex height children

        float offset = H;

        for(KitBit child : getChildren()){
            if(!child.isHidden()){

                float w = calculateW(child); // Calculate the width of the child without taking neighbors into account
                float h = calculateH(child, spanPoints, spanSpace); // Calculate the height of the child, taking neighbors into account
                float x = calculateX(child, w); // Calculate the X coordinate of the child based on the width of the child

                if (child.isHSpaced()){ // Draw the child in slightly different places depending on whether it has spacing
                    child.drawFrame(x, Y + offset - h - getSpacing(), w, h, Z);
                } else {
                    child.drawFrame(x, Y + offset - h, w, h, Z);
                }

                offset -= child.isHSpaced() ? h + getSpacing() : h - getSpacing();

            }
        }

    }
}
