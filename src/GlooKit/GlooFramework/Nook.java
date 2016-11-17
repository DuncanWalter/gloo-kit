package GlooKit.GlooFramework;

/**
 * A {@code Nook} is a {@link KitBit} that is input-opaque with a default z-coordinate of 1. That is, when a
 * {@code Nook} is in focus, user input will be captured by the {@code Nook} and children of the {@code Nook} and not
 * passed on to element located behind the {@code Nook}. The default z-coordinate of 1 means that they and their
 * children will appear in front of other {@code KitBits} on the screen.
 *
 * @see GlooKit.GlooFramework.KitBit
 * @see GlooKit.GlooFramework.Room
 *
 * @author Duncan Walter
 * @author Eli Jergensen
 * @since 1.0
 * */
public class Nook extends KitBit {

    /** Unlike many other {@code KitBits}, {@code Nooks} actually have a z-coordinate, which determines whether this
     * {@code Nook} gets drawn in front of or behind other {@code KitBits}. Can range from -1 to +1, with +1 being the
     * most in front */
    protected float z;

    /**
     * Constructs a {@code Nook} given the {@code GlooApplication} of this {@code Nook} in addition to the normal
     * parameters of the default {@link KitBit#KitBit(int, int, String, String, KitBit...) KitBit constructor}. This is
     * required to ensure that the {@code Nook} can receive user input
     *
     * @param X int of the horizontal justification, as defined in the KitBit class
     * @param Y int of the vertical justification, as defined in the KitBit class
     * @param W a valid formatting String for setting the width, as defined in the KitBit class
     * @param H a valid formatting String for setting the height, as defined in the KitBit class
     * @param app the GlooApplication of this Nook, which allows Nook to capture input
     * @param children possibly many KitBits, which are children of this Nook. Can take any number of KitBits (null for
     *                 none)
     *
     * @see KitBit#KitBit(int, int, String, String)
     * */
    public Nook(int X, int Y, String W, String H, GlooApplication app, KitBit... children){
        super(X, Y, W, H, children);
        super.setParent(app); // calls the non-overridden method to ensure the app is the nook's parent
        setZ(-0.1f); // initially set the z of a nook to be 1
    }

    /**
     * Overrides the
     * {@link KitBit#drawFrame(float, float, float, float, float) drawFrame(float, float, float, float, float)}
     * method of {@code KitBit} to also call the {@link KitBit#drawChildren(float) drawChildren(float)} method of
     * {@code KitBit}, which ensures that {@code drawFrame()} is recursively called on the children of this {@code Nook}
     *
     * @param X float for the horizontal position of the bottom left corner of this Nook on the screen, measured in
     *          pixels from the left side of the screen
     * @param Y float for the vertical position of the bottom left corner of this Nook on the screen, measured in
     *          pixels from the bottom of the screen
     * @param W float for the width of this Nook on the screen, measured in pixels
     * @param H float for the height of this Nook on the screen, measured in pixels
     * @param Z float for the z-coordinate of this Nook which is actually ignored in favor of the z-coordinate of this
     *          Nook
     * */
    public void drawFrame(float X, float Y, float W, float H, float Z){
        drawChildren(z);
        super.drawFrame(X, Y, W, H, z);

    }

    /**
     * Overrides the {@link KitBit#setParent(KitBit) setParent(KitBit)} method of {@code KitBit} to add this {@code Nook}
     * as the child of the parent, but not set the parent as the parent of this {@code Nook}. This essentially sets the
     * parent as a virtual of this {@code Nook}.
     *
     * @param parent a KitBit, which will set this Nook as its child, but will not be set as the parent of this Nook
     * */
    public void setParent(KitBit parent) {
        parent.addChild(this); // overrides the normal method to not notify this Nook of the parent
    }

    /**
     * Sets the z-coordinate of this {@code Nook}, which can take values from -1 to 1, where 1 is "closest" to the
     * screen.
     *
     * @param Z float for the z-coordinate of this Nook, which can be between -1 and 1, inclusive
     * */
    public void setZ(float Z) {
        this.z = Z;
    }

}
