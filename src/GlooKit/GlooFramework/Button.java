package GlooKit.GlooFramework;

import GlooKit.GlooFramework.Components.Drawable;
import GlooKit.GlooFramework.Components.Label;

import static org.lwjgl.glfw.GLFW.*;

/**
 * A {@code Button} is a {@link KitBit KitBit} that can perform an action when clicked on. A {@code Button} may actually
 * have up to three different functions: an {@code onClick()} method that is activated when this {@code Button} is
 * clicked (when the mouse is pressed and released over this {@code Button}), an {@code onHover()} method that is called
 * every frame the mouse is over this {@code Button}, and an {@code onMouseDown()} method, which is triggered the first
 * frame the mouse is pressed down on this {@code Button}. {@code Button} can be overridden and extended to allow for
 * more complex behavior, if desired, including more actions.
 *
 * @see KitBit
 *
 * @author Duncan Walter
 * @author Eli Jergensen
 * @since 1.0
 * */
public class Button extends KitBit {

    /** Boolean for whether this {@code Button} has been clicked (but not yet released) */
    private boolean clicked;

    /** Action to be called when the mouse is over this {@code Button} */
    private Runnable onHover;

    /** Action to be called when the mouse is initially pressed down on this {@code Button} */
    private Runnable onMouseDown;

    /**
     * Overloads the default
     * {@link Button#Button(int, int, String, String, Drawable, Label, Runnable, Runnable, Runnable) Button constructor}
     * to take only an {@code onClick()} method and not an {@code onHover()} method or {@code onMouseDown()} method.
     *
     * @param X int of the horizontal justification, as defined in the KitBit class
     * @param Y int of the vertical justification, as defined in the KitBit class
     * @param W a valid formatting String for setting the width, as defined in the KitBit class
     * @param H a valid formatting String for setting the height, as defined in the KitBit class
     * @param frame a Drawable, which determines how to draw this object on the screen
     * @param label a Label, which handles the the text and formatting of text of this element
     * @param onClick a Runnable to be called when this Button is clicked
     * */
    public Button(int X, int Y, String W, String H, Drawable frame, Label label, Runnable onClick) {
        this(X, Y, W, H, frame, label, onClick, null, null);
    }

    /**
     * Overloads the default
     * {@link Button#Button(int, int, String, String, Drawable, Label, Runnable, Runnable, Runnable) Button constructor}
     * to take an {@code onClick()} method and {@code onHover()} method, but not an {@code onMouseDown()} method.
     *
     * @param X int of the horizontal justification, as defined in the KitBit class
     * @param Y int of the vertical justification, as defined in the KitBit class
     * @param W a valid formatting String for setting the width, as defined in the KitBit class
     * @param H a valid formatting String for setting the height, as defined in the KitBit class
     * @param frame a Drawable, which determines how to draw this object on the screen
     * @param label a Label, which handles the the text and formatting of text of this element
     * @param onClick a Runnable to be called when this Button is clicked
     * @param onHover a Runnable to be called when the mouse is over this Button
     * */
    public Button(int X, int Y, String W, String H, Drawable frame, Label label, Runnable onClick, Runnable onHover) {
        this(X, Y, W, H, frame, label, onClick, onHover, null);
    }

    /**
     * Constructs a {@code Button} given the normal parameters of the default
     * {@link KitBit#KitBit(int, int, String, String) KitBit constructor} and a {@code frame}, a {@code label}, and up to
     * three {@code Runnables} to be called when the {@code Button} is clicked, has the mouse over it, or when the mouse
     * presses down on it. A {@code Button} does not have any children.
     *
     * @param X int of the horizontal justification, as defined in the KitBit class
     * @param Y int of the vertical justification, as defined in the KitBit class
     * @param W a valid formatting String for setting the width, as defined in the KitBit class
     * @param H a valid formatting String for setting the height, as defined in the KitBit class
     * @param frame a Drawable, which determines how to draw this object on the screen
     * @param label a Label, which handles the the text and formatting of text of this element
     * @param onClick a Runnable to be called when this Button is clicked
     * @param onHover a Runnable to be called when the mouse is over this Button
     * @param onMouseDown a Runnable to be called when the mouse is initially pressed down on this Button
     *
     * @see KitBit#KitBit(int, int, String, String)
     * */
    public Button(int X, int Y, String W, String H, Drawable frame, Label label, Runnable onClick, Runnable onHover, Runnable onMouseDown) {
        super(X, Y, W, H);
        setFrame(frame);
        setLabel(label);
        setAction(onClick);
        this.onHover = onHover;
        this.onMouseDown = onMouseDown;
    }

    /**
     * Calls the {@code onHover()} method of this {@code Button}, if there is any. This method should be overridden
     * if the action is not a {@code Runnable}, but instead something like a {@code Consumer} or {@code BiConsumer}.
     * */
    public void activateOnHover() {
        if (onHover != null){ onHover.run(); }
    }

    /**
     * Calls the {@code onMouseDown()} method of this {@code Button}, if there is any. This method should be overridden
     * if the action is not a {@code Runnable}, but instead something like a {@code Consumer} or {@code BiConsumer}.
     * */
    public void activateOnMouseDown() {
        if (onMouseDown != null){ onMouseDown.run(); }
    }

    /**
     * Overrides the {@link KitBit#calcFrame(double, Input) calcFrame(double, Input)} method of {@code KitBit} to check
     * if the {@code Button} was moused over, pressed, or clicked since the last frame. Triggers the {@code onHover()}
     * action every frame that the mouse is over this {@code Button}. Triggers the {@code onMouseDown()} action only the
     * first frame that the mouse was pressed down. Triggers the {@code onClick()} action when the mouse has been
     * pressed and released over this {@code Button} without moving off of it. This method can be overridden to change
     * this behavior if desired.
     * */
    public void calcFrame(double delta, Input input){

        if(isUnder(input.getCursorLocation())){ // check to see if this button is under the cursor

            activateOnHover(); // trigger the onHover() action every frame

            if(input.pollEvent(GLFW_MOUSE_BUTTON_LEFT, GLFW_PRESS)){ // see if the mouse was just pressed down this frame
                clicked = true;
                activateOnMouseDown(); // trigger the onMouseDown() action only when the mouse is initially pressed down
            }
            if(clicked && input.pollEvent(GLFW_MOUSE_BUTTON_LEFT, GLFW_RELEASE)){
                clicked = false;
                activate(); // trigger the onClick() action when the mouse has been pressed and released
            }

        } else if (input.pollEvent(GLFW_MOUSE_BUTTON_LEFT, GLFW_PRESS)){
            clicked = false;
        }

        super.calcFrame(delta, input); // call the default KitBit method, just in case
    }

    /**
     * Sets the {@code onClick()} method of this {@code KitBit}, which is a function that will be called when the
     * {@code Button} is clicked. This method can be overridden to allow for more complex functions like
     * {@code Consumers} and {@code BiConsumers}.
     *
     * @param onClick a Runnable to be called when this Button is clicked
     * */
    public void setOnClick(Runnable onClick) {
        setAction(onClick);
    }

    /**
     * Sets the {@code onHover()} method of this {@code KitBit}, which is a function called every frame the mouse is
     * over this {@code Button}. This method can be overridden to allow for more complex functions like {@code Consumers}
     * and {@code BiConsumers}.
     *
     * @param onHover a Runnable to be called when the mouse is over this Button
     * */
    public void setOnHover(Runnable onHover) {
        this.onHover = onHover;
    }

    /**
     * Sets the {@code onMouseDown()} method of this {@code KitBit}, which is a function called the first frame the
     * mouse is pressed down on this {@code Button}. This method can be overridden to allow for more complex functions
     * like {@code Consumers} and {@code BiConsumers}.
     *
     * @param onMouseDown a Runnable to be called when the mouse is initially pressed down on this Button
     * */
    public void setOnMouseDown(Runnable onMouseDown) {
        this.onMouseDown = onMouseDown;
    }

}
