package GlooKit.GlooFramework;

/**
 * A {@code RowSpan} is a {@link Div Div} which has a list of children that will be drawn horizontally across from left
 * to right in the order added.
 *
 * @see Div
 *
 * @author Duncan Walter
 * @author Eli Jergensen
 * @since 1.0
 */
// TODO clipping off-screen draws during drawFrame
public class RowSpan extends Div{

    /**
     * Overloads the default {@link RowSpan#RowSpan(int, int, String, String, KitBit...) RowSpan constructor} to only
     * take a list of children. Defaults to being right and bottom justified, having no spacing, and taking up all the
     * width and height it can.
     *
     * @param children possibly many KitBits, which are children of this RowSpan. Can take any number of KitBits
     * */
    public RowSpan(KitBit... children){
        this(R, B, "1/n+", "1/n+", children);
    }

    /**
     * Overloads the default {@link RowSpan#RowSpan(int, int, String, String, KitBit...) RowSpan constructor} to only
     * take a vertical justification, a formatting String for setting the height, and a list of children. Defaults to
     * being right justified, having no horizontal spacing, and taking up all the width it can.
     *
     * @param Y int of the vertical justification, as defined in the KitBit class
     * @param H a valid formatting String for setting the height, as defined in the KitBit class
     * @param children possibly many KitBits, which are children of this RowSpan. Can take any number of KitBits
     * */
    public RowSpan(int Y, String H, KitBit... children){
        this(R, Y, "1/n+", H, children);
    }

    /**
     * Constructs a {@code RowSpan} with the normal parameters of the default
     * {@link KitBit#KitBit(int, int, String, String, KitBit...) KitBit constructor}.
     *
     * @param X int of the horizontal justification, as defined in the KitBit class
     * @param Y int of the vertical justification, as defined in the KitBit class
     * @param W a valid formatting String for setting the width, as defined in the KitBit class
     * @param H a valid formatting String for setting the height, as defined in the KitBit class
     * @param children possibly many KitBits, which are children of this RowSpan. Can take any number of KitBits
     * */
    public RowSpan(int X, int Y, String W, String H, KitBit... children){
        super(X, Y, W, H, children);
    }

    /**
     * Overrides the
     * {@link Div#drawFrame(float, float, float, float, float) drawFrame(float, float, float, float, float)} method
     * of {@code Div} to properly calculate the position of all of the children of this {@code RowSpan} before calling
     * {@code drawFrame} on each of the children.
     *
     *
     * */
    public void drawFrame(float X, float Y, float W, float H, float Z){
        super.drawFrame(X, Y, W, H, Z);

        float spanPoints = calculateWidthSpanPoints(getChildren()); // Calculate the number of points for fixed width children
        float spanSpace = calculateWidthSpanSpace(getChildren()); // Calculate the fraction of width available for flex width children

        float offset = 0;

        for (KitBit child : getChildren()){

            if(!child.isHidden()){

                float w = calculateW(child, spanPoints, spanSpace); // Calculate the width of the child, taking neighbors into account
                float h = calculateH(child); // Calculate the height of the child without taking neighbors into account
                float y = calculateY(child, h); // Calculate the Y coordinate of the child based on the height of the child

                if (child.isWSpaced()){ // Draw the child in slightly different places depending on whether it has spacing
                    child.drawFrame(X + offset + getSpacing(), y, w, h, Z);
                } else {
                    child.drawFrame(X + offset, y, w, h, Z);
                }

                offset += child.isWSpaced() ? w + getSpacing() : w - getSpacing();

            }
        }
    }
}
