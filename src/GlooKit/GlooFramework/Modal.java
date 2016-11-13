package GlooKit.GlooFramework;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

/**
 * A {@code Modal} is a {@link Nook Nook} that disappears when it is clicked off of. They are identical to {@code Nooks},
 * except that they override the {@link KitBit#checkFocus(Input) checkFocus(Input)} method of {@link KitBit} to call the
 * {@link KitBit#destroy() destroy()} method when a click occurs but this {@code Modal} is not under the mouse.
 *
 * @see Nook
 *
 * @author Eli Jergensen
 * @author Duncan Walter
 * @since 1.0
 * */
public class Modal extends Nook{

    /**
     * Constructs a Modal with the exact same parameters as a {@code Nook}.
     *
     * @param X int of the horizontal justification, as defined in the KitBit class
     * @param Y int of the vertical justification, as defined in the KitBit class
     * @param W a valid formatting String for setting the width, as defined in the KitBit class
     * @param H a valid formatting String for setting the height, as defined in the KitBit class
     * @param app the GlooApplication of this Nook, which allows Nook to capture input
     * @param children possibly many KitBits, which are children of this Nook. Can take any number of KitBits (null for
     *                 none)
     *
     * @see Nook#Nook(int, int, String, String, GlooApplication, KitBit...)
     * */
    public Modal(int X, int Y, String W, String H, GlooApplication app, KitBit... children) {
        super(X, Y, W, H, app, children);
    }

    /** Overrides the default {@link KitBit#checkFocus(Input) checkFocus(Input)} method of {@code KitBit} to close this
     * {@code Modal} if a click occurs when this {@code Modal} is active and the click happens somewhere other than the
     * Modal.
     * */
    public boolean checkFocus(Input input) {
        if(!isUnder(input.getCursorLocation())){
            // cursor is not over room
            if(input.pollEvent(GLFW_MOUSE_BUTTON_LEFT, GLFW_PRESS)){
                destroy(); // Modals close if a click occurs, but not on them
            }
            return false; // return true if the cursor is over this Modal
        } else {
            return true; // return true if the cursor is over this Modal
        }
    }
}
