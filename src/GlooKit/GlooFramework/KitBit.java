package GlooKit.GlooFramework;

import java.util.ArrayList;

import java.util.List;

import GlooKit.GlooFramework.Components.*;
import GlooKit.Utils.Vector;

/**
 * {@code KitBits} are the building blocks of the Visual Gloo Framework, designed to mimic HTML elements in many ways.
 * They are managed in a hierarchy of parents and children and are capable of multiple forms of responsive sizing.
 * {@code KitBits} are also made to be extendable; custom {@code KitBits} can serve as modules or perform complex tasks
 * while maintaining a uniform structure. A {@code KitBit} can have an x position, y position, width configuration,
 * height configuration, {@link Drawable frame}, {@link Label label}, {@code action}, parent {@code KitBit}, and
 * possibly many children, which are also {@code KitBits}. All of these components are described in the following
 * paragraphs.
 * <p>
 * Frames are the visual part of a {@code KitBit}, incorporating things like {@link GlooKit.GlooAPI.Texture Texture}.
 * Frames implement the {@link Drawable Drawable} interface, which allows them to specify how they draw and what they do
 * during a {@code stepFrame} or {@code calcFrame}. {@code Labels} handle the text and text formatting {@code KitBit}
 * and may also update during a {@code stepFrame} or {@code calcFrame}. {@code Actions} are lambda functions
 * ({@code Runnables}, specifically) that are triggered when the {@link KitBit#activate() activate()} method is called
 * on this {@code KitBit}.
 * <p>
 * Each {@code KitBit} will usually have a parent, which is set at construction. It may also posses multiple children,
 * which can either be set at construction or can be adopted later via the
 * {@link KitBit#setParent(KitBit) setParent(KitBit)} method.
 * <p>
 * {@code KitBits} can be left, right, or center justified and top, bottom, or center vertical-justified within their
 * parent elements. This allows for nine total position for a single element within its parent. In addition, they
 * have many formatting options for their width and height in the parent element. These include options to have
 * <ul>
 *     <li>a fixed number of pixels, which is set using the "xp" tag, where x is any number and p is the letter 'p';</li>
 *     <li>proportional dimension to the other dimension, which is set using the "equal" tag or the "xh" or "xw" tags,
 *     where x is any number and h and w are the letters 'h' and 'w';</li>
 *     <li>a fractional amount of the parent, which is set using the "x/y" tag, where x and y are any numbers and / is a
 *     literal slash;</li>
 *     <li>a variable fractional amount of the parent, which is set using the "x/n", where x is any number, / is the
 *     literal slash, and n is the letter 'n'; or</li>
 *     <li>size just large enough to encapsulate any of its children, which is set using the "wrap" tag.</li>
 * </ul>
 * In addition, '+' can be appended to any of the previous, which will prevent the {@code KitBit} from having spacing
 * between it and its neighbors. The size of fixed width or fixed height elements is determined first. Next,
 * proportional width and proportional height elements are calculated using proportionality constants as set in the
 * tags, where "xh" means the width is x times the height, "xw" means the height is x times the width, and "equal" means
 * the width or height is exactly equal to the height or width. After that, fractional width and fractional height
 * elements are calculated from the remaining space after the fixed and proportional width and height elements have been
 * allocated. Finally, variable fractional amounts are determined based on a claim system. The x in the fraction x/n is
 * the number of claims and n is the total number of claims of all children of a parent. Each variable fractional child
 * is therefore allotted (claim)/(total claims) amount of the space of the parent remaining after all fixed,
 * proportional, and fractional width and height element have been given space. Parents who are set to "wrap" their
 * children are calculated after all of their children have been calculated, meaning that their children must all be
 * fixed or proportional in that direction.
 * <p>
 * To see an example of this in action, consider 5 elements being placed in a space of 525 points where the 5 elements
 * are formatting for width as follows (ignoring spacing):
 * <ol>
 *     <li>     1/4</li>
 *     <li>     100p</li>
 *     <li>     0.25h (and the height is 100p)</li>
 *     <li>     5/n</li>
 *     <li>     1/n</li>
 * </ol>
 * As fixed width happens first, the second element is granted 100 points off the bat. Immediately afterward, the third
 * element is awarded 25 points. This leaves 400 points, of which the first element receives 100. Finally, we have 300
 * points to be allocated to the two variable fractional width elements. Together they have 5+1=6 claims, giving the
 * fourth element 5/6th of the 300 points and the last element the remaining 1/6th the 300 points. As such, each of the
 * elements will be awarded:
 * <ol>
 *     <li>     100 points</li>
 *     <li>     100 points</li>
 *     <li>     25  points</li>
 *     <li>     250 points</li>
 *     <li>     50  points</li>
 * </ol>
 * for this frame. If the amount of space were to change, then each of the element's widths would update accordingly.
 * These values are calculated through the various calculate methods below and can be accessed to help set the widths of
 * children of special {@code KitBits}, as is done by both the {@link RowSpan RowSpan} and {@link ColSpan ColSpan}
 * classes.
 *
 * <p>
 * Up to three different methods may be called on a {@code KitBit } per frame:
 * <ul>
 *     <li>{@link KitBit#drawFrame(float, float, float, float, float) drawFrame(float, float, float, float)}: <br>
 *         An element is prepared for being drawn on the screen by calling the
 *         {@link Drawable#draw(float, float, float, float, float) frame.draw(float, float, float, float, float)} and
 *         {@link Label#draw(float, float, float, float, float) label.draw(float, float, float, float, float)} methods.
 *         In addition, most {@code KitBits} override this method to recursively call this method on their children,
 *         which ensures that all visible elements get drawn.</li>
 *     <li>{@link KitBit#calcFrame(double, Input) calcFrame(double, Input)}: <br>
 *         An element processes any changes that have occurred due to user input by calling the
 *         {@link Drawable#calcFrame(double, Input) frame.calcFrame(double, Input)} and
 *         {@link Label#calcFrame(double, Input) label.calcFrame(double, Input)} methods only if this {@code KitBit}
 *         is not hidden. This is also recursively called on the {@code KitBit's} children. {@code CalcFrame} is only
 *         called when a {@code KitBit} or one of its ancestors is in focus and might have received user input, meaning
 *         that it will not happen every frame. As with {@code drawFrame}, this method can be overridden for more
 *         complex actions, such as button clicks.</li>
 *     <li>{@link KitBit#stepFrame(double) stepFrame(double)}: <br>
 *         An element processes any changes that have occurred between frames by calling the
 *         {@link Drawable#stepFrame(double) frame.stepFrame(double)} and
 *         {@link Label#stepFrame(double) label.stepFrame(double)} methods. This is also recursively called on the
 *         {@code KitBit's} children. Unlike {@code calcFrame}, this is called every frame. Like {@code calcFrame} and
 *         {@code drawFrame}, it is intended to be overridden to allow for more complex behavior, such as animation.</li>
 * </ul>
 *
 * <p>
 * The constructor for {@code KitBits} supports code formatted to resemble a markup language like HTML. For example:
 * <pre>
 *     new Room(app
 *       , new RowSpan(
 *            new ColSpan(
 *                 new Canvas(LEFT, CENTER, "1/n", "0.5w", new Rect(blue, DEFAULT))
 *                ,new Canvas(LEFT, CENTER, "1/n", "equal", new Rect(blue, DEFAULT))
 *                ,new Canvas(LEFT, CENTER, "1/n", "equal", new Rect(blue, DEFAULT))
 *            )
 *            ,new ColSpan(
 *                 new Canvas(LEFT, CENTER, "1/n", "1/2", new Rect(blue, DEFAULT))
 *                ,new Canvas(LEFT, CENTER, "1/n", "1/2", new Rect(blue, DEFAULT))
 *            )
 *            ,new ColSpan(
 *                 new Canvas(LEFT, CENTER, "1/n", "1/n", new Rect(blue, DEFAULT))
 *                ,new Canvas(LEFT, CENTER, "equal", "70p", new Rect(blue, DEFAULT))
 *                ,new Canvas(LEFT, CENTER, "1/n", "1/n", new Rect(blue, DEFAULT))
 *            )
 *        )
 *     ).configure();
 * </pre>
 *
 * <p>
 * {@code KitBit} is designed to be extended to allow for the writing of specialized elements that act in different
 * ways. Almost all of the fields of {@code KitBit} are publicly accessible or have public getter methods to allow for
 * reading. In addition, every method is public, allowing for overriding and extending as desired. Examples of simple
 * {@code KitBits} include {@link Canvas Canvas} and {@link Button Button}, whereas examples of more complicated
 * {@code KitBits} include {@link Nook Nook} and {@link ColSpan ColSpan} and {@link RowSpan RowSpan}. These are also
 * intended to be extendable, which allows for very complex behavior. In fact, extended {@code KitBits} can even serve
 * as modules. This paired with dedicated view-controller classes creates a very responsive foundation for developing
 * views.
 *
 * @see Canvas
 * @see Button
 * @see ColSpan
 * @see RowSpan
 * @see Nook
 * @see Room
 * @see GlooKit.GlooFramework.GlooApplication
 *
 * @author Duncan Walter
 * @author Eli Jergensen
 * @since 1.0
 * */
// TODO? separate spacing from just horizontal and vertical components to left, right, top, and bottom spacings
    // TODO? implement a "dirty" flag to reduce computations of width and height
public abstract class KitBit {
    /////////////////////
    // CLASS CONSTANTS //
    /////////////////////
    /** Shorter name for the constant {@code LEFT} */
    public static final int L = 1;

    /** Constant indicating that this {@code KitBit} should be left-justified within its container */
    public static final int LEFT = 1;

    /** Shorter name for the constant {@code RIGHT} */
    public static final int R = 2;

    /** Constant indicating that this {@code KitBit} should be right-justified within its container */
    public static final int RIGHT = 2;

    /** Shorter name for the constant {@code TOP} */
    public static final int T = 3;

    /** Constant indicating that this {@code KitBit} should top-justified within its container */
    public static final int TOP = 3;

    /** Shorter name for the constant {@code BOTTOM} */
    public static final int B = 4;

    /** Constant indicating that this {@code KitBit} should be bottom-justified within its container */
    public static final int BOTTOM = 4;

    /** Shorter name for the constant {@code CENTER} */
    public static final int C = 5;

    /** Constant indicating that this {@code KitBit} should be either centered horizontally or vertically, depending on
     * whether it is used in place of an x or y value*/
    public static final int CENTER = 5;
    ///////////////////
    // INSTANCE DATA //
    ///////////////////
    /** List of {@code KitBits} that are children of this element; Elements that are contained within this {@code KitBit}*/
    private List<KitBit> children;

    /** {@code KitBit} that this element is a child of; This is also the containing element of this {@code KitBit} */
    private KitBit parent;

    /** Int for the index of this element within its parent's list of children */
    private int childIndex;

    /** {@link Drawable Drawable}, which is the visual part of this {@code KitBit}, including the {@code Texture} and
     *  shape */
    private Drawable frame;

    /** {@link Label Label}, which is the text and format of the text of this element. */
    private Label label;

    /** Function to be run when this element is activated. For example, this is triggered when a {@code Button} gets
     * clicked */
    private Runnable action;

    /** Float of the number of pixels placed between elements that are not set to fill */
    private float spacing;

    /** Float of the number of pixels per point */
    private float pointSize;

    /** Int value for whether this element is left- or right-justified or centered */
    public final int x;

    /** Int value for whether this element is top- or bottom-justified or centered */
    public final int y;

    /** Boolean for whether this element is visible */
    private boolean hidden;

    /* Values for determining width and height */
    /** Float specifying the width of this element in points */
    public Float wPoints;

    /** Float used to measure fractional width of the containing element that this {@code KitBit} will occupy. This is
     * the numerator of the relative width claimed by this element.
     * @see KitBit#wPool */
    public Float wSize;

    /** Float used to store available width of the containing element. This is the denominator of the relative width
     * claimed by this element. If this is null, then this is a variable fractional width element.
     * @see KitBit#wSize */
    public Float wPool;

    /** Float for how proportional this element is to its height (0.5 means width is half the height), where null means
     * not proportional */
    public Float wEqual;

    /** Boolean for whether this element is drawn based on relative width or {@code wPoints} (true means {@code wPoints}) */
    private boolean wRigid;

    /** Boolean for whether this element has spacing around it */
    private boolean wSpaced;

    /** Boolean for whether this element will be the smallest possible container of the width of its children */
    private boolean wWraps;

    /** Float specifying the height of this element in points */
    public Float hPoints;

    /** Float used to measure fractional height of the containing element that this {@code KitBit} will occupy. This is
     * the numerator of the relative height claimed by this element
     * @see KitBit#hPool */
    public Float hSize;

    /** Float used to store available height of the containing element. This is the denominator of the relative height
     * claimed by this element. If this is null, then this is a variable fractional height element.
     * @see KitBit#hSize */
    public Float hPool;

    /** Float for how proportional this element is to its width (0.5 means height is half the width), where null means
     * not proportional */
    public Float hEqual;

    /** Boolean for whether this element is drawn based on relative height or {@code hPoints} (true means {@code hPoints}) */
    private boolean hRigid;

    /** Boolean for whether this element has spacing around it */
    private boolean hSpaced;

    /** Boolean for whether this element will be the smallest possible container of the height of its children */
    private boolean hWraps;

    /* Variables for storing on-screen location */
    /** Float of the on-screen x-coordinate of this {@code KitBit} for the last frame, measured from the left in pixels */
    private float xStore;

    /** Float of the on-screen y-coordinate of this {@code KitBit} for the last frame, measured from the bottom in pixels */
    private float yStore;

    /** Float of the on-screen width of this {@code KitBit} for the last frame, measured in pixels */
    private float wStore;

    /** Float of the on-screen height of this {@code KitBit} for the last frame, measured in pixels */
    private float hStore;

    ////////////////////////////////////////////
    ////////////////////////////////////////////

    /**
     * Creates a new {@code KitBit} given a horizontal and vertical justification and width and height formatting. Overloads
     * the other {@link KitBit#KitBit(int, int, String, String, KitBit...) constructor} to not have any children. See
     * the other constructor for a full description of the parameters.
     *
     * @param x int of the horizontal justification, which can take values of LEFT, RIGHT, or CENTER (defaults to CENTER)
     * @param y int of the vertical justification, which can take values of TOP, BOTTOM, or CENTER (defaults to CENTER)
     * @param w a valid formatting String for setting the width.
     * @param h a valid formatting String for setting the height.
     *
     * @see KitBit#KitBit(int, int, String, String, KitBit...)
     */
    public KitBit(int x, int y, String w, String h){
        this(x, y, w, h, (KitBit[]) null);
    }

    /**
     * Creates a new {@code KitBit} given a horizontal and vertical justification, width and height formatting, and possibly
     * multiple children. There are nine possible combinations of values for the horizontal and vertical justification in
     * addition to many formatting options for {@code w} (width) and {@code h} (height), as specified below. Note that
     * {@code w} and {@code h} cannot both be {@code equal} or {@code xh} and {@code xw}.
     * <p>
     * The formatting of taking multiple children allows for {@code KitBits} within a menu or screen to be written in a
     * vaguely HTML-like way. For example:
     * <pre>
     *     new Room(app
     *       , new RowSpan(
     *            new ColSpan(
     *                 new Canvas(LEFT, CENTER, "1/n", "0.5w", new Rect(blue, DEFAULT))
     *                ,new Canvas(LEFT, CENTER, "1/n", "equal", new Rect(blue, DEFAULT))
     *                ,new Canvas(LEFT, CENTER, "1/n", "equal", new Rect(blue, DEFAULT))
     *            )
     *            ,new ColSpan(
     *                 new Canvas(LEFT, CENTER, "1/n", "1/2", new Rect(blue, DEFAULT))
     *                ,new Canvas(LEFT, CENTER, "1/n", "1/2", new Rect(blue, DEFAULT))
     *            )
     *            ,new ColSpan(
     *                 new Canvas(LEFT, CENTER, "1/n", "1/n", new Rect(blue, DEFAULT))
     *                ,new Canvas(LEFT, CENTER, "equal", "70p", new Rect(blue, DEFAULT))
     *                ,new Canvas(LEFT, CENTER, "1/n", "1/n", new Rect(blue, DEFAULT))
     *            )
     *        )
     *     ).configure();
     * </pre>
     *
     * @param x int of the horizontal justification, which can take values of LEFT, RIGHT, or CENTER (defaults to CENTER)
     * @param y int of the vertical justification, which can take values of TOP, BOTTOM, or CENTER (defaults to CENTER)
     * @param w a valid formatting String for setting the width. Can take several values:
     *          <ul>
     *              <li>xp</li>
     *              <li>x/y</li>
     *              <li>x/n</li>
     *              <li>equal</li>
     *              <li>xh</li>
     *              <li>wrap</li>
     *              <li>+ appended to any of the previous</li>
     *          </ul>
     *          where x and y are integers and every other character is the literal character (except the words in the
     *          last item). Each of these has a different meaning:
     *          <ul>
     *              <li>A specific number of points (200p means 200 points wide). One point is roughly equivalent to 1/72
     *              inches. </li>
     *              <li>Some fraction of the available width (2/5 means 40% the available width) after points have been
     *              allocated.</li>
     *              <li>Primarily useful in RowSpans, where multiple elements are in a row. After points have been
     *              allocated and specified fractions have been allocated (see above), there will be some amount of space
     *              left. Each remaining element that specified some x/n will receive x shares out of the remaining space.
     *              For example, two elements with 1/n and 2/n with a remaining space of 600 points will receive 200 and
     *              400 points, respectively.</li>
     *              <li>Width is exactly equal to the height</li>
     *              <li>Width is proportional to height (0.5h means width is half the height)</li>
     *              <li>Width is the smallest possible container for the largest width amongst its children</li>
     *              <li>Appending + to any of these means that the KitBit will not have spacing around it in the x
     *              direction. By default the KitBit will have spacing.</li>
     *          </ul>
     *          For a full example of this, consider 4 elements being placed in a space of 400 points where one element
     *          requests 100p, one element requests 1/3, one element requests 2/n, and the last element requests 3/n. For
     *          simplicity, spacing is neglected in this example. In this case, the first element is immediately granted
     *          100 points, leaving 300 points. The second element is granted 1/3rd of the remaining 300 points, giving
     *          it another 100 points. The last two elements requested a total of 5 shares of the remaining 200 points,
     *          giving the first of the two 2/5ths of 200 points and the second 3/5ths of 200, giving them 80 and 120
     *          points, respectively.
     * @param h a valid formatting String for setting the height. Can take several values:
     *          <ul>
     *              <li>xp</li>
     *              <li>x/y</li>
     *              <li>x/n</li>
     *              <li>equal</li>
     *              <li>xw</li>
     *              <li>wrap</li>
     *              <li>+ appended to any of the previous</li>
     *          </ul>
     *          where x and y are integers and every other character is the literal character (except the words in the
     *          last item). Each of these has a different meaning:
     *          <ul>
     *              <li>A specific number of points (200p means 200 points tall). One point is roughly equivalent to 1/72
     *              inches.</li>
     *              <li>Some fraction of the available height (2/5 means 40% the available height) after points have been
     *              allocated.</li>
     *              <li>Primarily useful in ColSpans, where multiple elements are in a column. After points have been
     *              allocated and specified fractions have been allocated (see above), there will be some amount of space
     *              left. Each remaining element that specified some x/n will receive x shares out of the remaining space.
     *              For example, two elements with 1/n and 2/n with a remaining space of 600 points will receive 200 and
     *              400 points, respectively.</li>
     *              <li>Height is exactly equal to the width</li>
     *              <li>Height is proportional to width (0.25w means height is one quarter the width)</li>
     *              <li>Height is the smallest possible container for the largest height amongst its children</li>
     *              <li>Appending + to any of these means that the KitBit will not have spacing around it in the y
     *              direction. By default the KitBit will have spacing.</li>
     *          </ul>
     *          For a full example of this, consider 4 elements being placed in a space of 400 points where one element
     *          requests 100p, one element requests 1/3, one element requests 2/n, and the last element requests 3/n. For
     *          simplicity, spacing is neglected in this example. In this case, the first element is immediately granted
     *          100 points, leaving 300 points. The second element is granted 1/3rd of the remaining 300 points, giving
     *          it another 100 points. The last two elements requested a total of 5 shares of the remaining 200 points,
     *          giving the first of the two 2/5ths of 200 points and the second 3/5ths of 200, giving them 80 and 120
     *          points, respectively.
     * @param children possibly many KitBits, which are children of this KitBit. Can take any number of KitBits.
     */
    public KitBit(int x, int y, String w, String h, KitBit... children){

        hidden = false; // set the KitBit to visible

        this.x = x; // store the horizontal and vertical alignments
        this.y = y;

        ////////////////////////////////////////////
        //////////// Width /////////////////////////
        ////////////////////////////////////////////

        /* Figure out the state of any booleans*/
        wRigid = w.contains("p") || w.contains("equal") || w.contains("h"); // this also catches the case of "wrap"
        wSpaced = !w.contains("+");
        wWraps = w.contains("wrap");
        wEqual = w.contains("equal") ? 1f : null;

        if(w.contains("/")){ // check to see if this element has a fractional width

            wSize = (float) Integer.parseInt(w.substring(0, w.indexOf("/"))); // parse the numerator
            if(w.contains("n")){
                wPool = null; // delay computing the denominator until later
            } else {
                wPool = (float) Integer.parseInt(w.substring(w.indexOf("/") + 1, (w + "+").indexOf("+"))); // parse the denominator
            }
            wPoints = null; // element does not have a fixed width

        } else if(w.contains("p") && !w.contains("wrap")){ // check to see if this element has a fixed width

            wPoints = Float.parseFloat(w.substring(0, w.indexOf("p"))); // parse the width
            wSize = null; // element does not have a fractional width
            wPool = null;

        } else if(w.contains("h")){ // check to see if this element has a width proportional to height

            wEqual = Float.parseFloat(w.substring(0, w.indexOf("h"))); // parse the proportionality constant
            wPoints = null; // element does not have a fixed width
            wSize = null; // element does not have a fractional width
            wPool = null;

        }else if(w.equals("")){ // empty quotes means 1/n

            wPoints = null; // element does not have a fixed width
            wSize = 1f; // set numerator to 1
            wPool = null; // delay computing denominator until later

        } else { // w was not a valid formatting string or was "wraps" or "equal"

            wPoints = null;
            wSize = null;
            wPool = null;

        }

        ////////////////////////////////////////////
        //////////// Height ////////////////////////
        ////////////////////////////////////////////

        /* Figure out the state of any booleans*/
        hRigid = h.contains("p") || h.contains("equal") || h.contains("w"); // this also catches the case of "wrap"
        hSpaced = !h.contains("+");
        hWraps = h.contains("wrap");
        hEqual = h.contains("equal") ? 1f: null;

        if(h.contains("/")){ // check to see if this element has a fractional height

            hSize = (float) Integer.parseInt(h.substring(0, h.indexOf("/"))); // parse the numerator
            if(h.contains("n")){
                hPool = null; // delay computing the denominator until later
            } else {
                hPool = (float) Integer.parseInt(h.substring(h.indexOf("/") + 1, (h + "+").indexOf("+"))); // parse the denominator
            }
            hPoints = null; // element does not have a fixed height

        } else if(h.contains("p") && !h.contains("wrap")){ // check to see if this element has a fixed height

            hPoints = Float.parseFloat(h.substring(0, h.indexOf("p"))); // parse the height
            hSize = null; // element does not have a fractional height
            hPool = null;

        } else if(h.contains("w") && !h.contains("wrap")){ // check to see if this element has a height proportional to width

            hEqual = Float.parseFloat(h.substring(0, h.indexOf("w"))); // parse the proportionality constant
            hPoints = null; // element does not have a fixed height
            hSize = null; // element does not have a fractional height
            hPool = null;

        } else if(h.equals("")){ // empty quotes mean 1/n

            hPoints = null; // element does not have a fixed height
            hSize = 1f; // set numerator to 1
            hPool = null; // delay computing denominator until later

        } else { // h was not a valid formatting string or was "wraps" or "equal"

            hSize = null;
            hPool = null;
            hPoints = null;

        }

        ////////////////////////////////////////////
        //////////// Children //////////////////////
        ////////////////////////////////////////////

        this.children = new ArrayList<>();

        if(children != null){
            for(KitBit child : children){
                if(child != null){
                    child.setParent(this); // set this to be the parent of all children (which will add it to the children list)
                }
            }
        }
    }

    /**
     * Adds a {@code KitBit} to the list of children of this {@code KitBit}, returning the index of the child in the
     * children list. If the child has previously been added, returns the index where the child already was. Added
     * children go in the first empty slot of the list, only appending to the list if necessary.
     *
     * @param newChild a KitBit to add to the list of children of this KitBit
     * @return int for the index of the child in the list of children
     * */
    public int addChild(KitBit newChild){

        // first check to see if this child has already been added
        for(int i = 0; i < children.size(); i++) {
            if(children.get(i) == newChild) {
                return i;
            }
        }

        // if not, then cycle through to see if any indices were null and put the child in the first one of those
        for(int i = 0; i < children.size(); i++){
            if(children.get(i) == null){
                children.set(i, newChild);
                return i;
            }
        }
        children.add(newChild); // if we get all the way here, just add the child to the end of the list
        return children.size() - 1;
    }

    /**
     * Calls the action of this function, if there is any. This method should be overridden if the action is not a
     * {@code Runnable}, but instead something like a {@code Consumer} or {@code BiConsumer}.
     * */
    public void activate(){
        if (action != null){action.run();}
    }

    /**
     * Calls {@link KitBit#calcFrame(double, Input) calcFrame(double, Input)} on each of the children of this {@code KitBit}.
     * This method is here primarily to allow for possible overrides, if desired.
     *
     * @param delta double for the number of seconds between the previous frame and the current frame
     * @param input an Input, which handles user input from keyboards, mice and controllers and is typically passed down
     *              by the parent of this {@code KitBit}
     *
     * @see GlooKit.GlooFramework.KitBit#calcFrame(double, Input)
     * */
    public void calcChildren(double delta, Input input){
        for(KitBit child : children){
            if(child != null) {
                child.calcFrame(delta, input);
            }
        }
    }

    /**
     * Process any changes to this element due to user input by calling the
     * {@link Drawable#calcFrame(double, Input) frame.calcFrame(double, Input)} and
     * {@link Label#calcFrame(double, Input) label.calcFrame(double, Input)} methods only if this {@code KitBit} is not
     * hidden. Also calls {@link KitBit#calcChildren(double, Input) calcChildren(double, Input)}, which recursively
     * calls {@code calcFrame} on each of the children of this {@code KitBit}.
     * <p>
     * {@code CalcFrame} is only called when a {@code KitBit} or one of its ancestors is in focus and might have
     * received user input. This default method does not actually do anything but pass the call on to the {@code frame}
     * and {@code label} and the children of this {@code  KitBit}, but it can be overloaded to do something fancier. For
     * an example of this, see {@link Button#calcFrame(double, Input) Button.calcFrame(double, Input)}.
     *
     * @param delta double for the number of seconds between the previous frame and the current frame
     * @param input an Input, which handles user input from keyboards, mice and controllers and is typically passed down
     *              by the parent of this KitBit
     *
     * @see KitBit#stepFrame(double)
     * */
    public void calcFrame(double delta, Input input){
        if(!hidden){ // check to make sure this element is not invisible
            if(frame != null){frame.calcFrame(delta, input);} // pass on the calcFrame call to the frame and label, if they exist
            if(label != null){label.calcFrame(delta, input);}
            calcChildren(delta, input); // pass on the calcFrame call to the children of this element
        }
    }

    /**
     * Calculates the height of a child assuming that the children has no neighbors in the vertical direction. This
     * method is called by a parent to figure out the height of its children.
     *
     * @param child the KitBit child of this KitBit whose height is being calculated
     * @return float for the height of this child, measured in pixels
     *
     * @see KitBit#calculateH(KitBit, float, float)
     * */
    public float calculateH(KitBit child) {
        return calculateH(child, 0, 1);
    }

    /**
     * Calculates the height of a child given
     * <ul>
     *     <li>the number of points already taken by children that have fixed height and</li>
     *     <li>the fraction of the height of the parent available after fixed height and fractional height children have had their
     * heights allocated.</li>
     * </ul>
     * Called by a parent to figure out the heights of its children.
     *
     * @param child the KitBit child of this KitBit whose height is being calculated
     * @param spanPoints the number of points already taken by children of this KitBit who have fixed height, calculated
     *                   by {@link KitBit#calculateHeightSpanPoints(List) calculateHeightSpanPoints(List)}
     * @param spanSpace the fraction of the height of this KitBit available after children with fixed or fractional heights
     *                  have been allocated, calculated by
     *                  {@link KitBit#calculateHeightSpanSpace(List) calculateHeightSpanSpace(List)}
     * @return float for the height of this child, measured in pixels
     * */
    public float calculateH(KitBit child, float spanPoints, float spanSpace){

        float h;
        if(child.hRigid && (child.hEqual == null) && !(child.hWraps)){
            // this child's height is a fixed point value, so simply calculate it
            h = child.hPoints * pointSize;

        } else if(child.hWraps) {
            // this child's height is wrapping
            h = 0;
            if(child.label != null){ // check to see if this child has a label
                h = child.label.calculateH() + 2 * spacing; // if so, w is the height of the label plus two spacing
            }
            // then check all of the children of this child
            for (KitBit c : child.children){
                if(c.hRigid && !c.isHidden()){ // if a child is rigid and not hidden...
                    h = Math.max(h, c.hPoints * pointSize + spacing * 2); // take the larger of h and this child's height
                }
            }

        } else if(child.hEqual != null) {
            // this child's height is proportional to its width
            if(child.wEqual == null){
                h = child.hEqual * calculateW(child); // set the height of the child to be the proportionality constant
                // times the width of the child, assuming the child has no neighbors
            } else {
                System.out.println("WARNING: Width and height of a KitBit cannot both be proportional to each other!");
                System.out.println("Setting the height of " + child + " to be 0");
                h = 0; // if both width and height were set to be proportional to each other, set h = 0 to catch the error
            }
        } else {
            // this child's width is fractional
            float childPool = child.hPool == null ? spanSpace : child.hPool;
            // if the child has requested x/n, then take the spanSpace to be the denominator. Otherwise take hPool to be the denominator.

            h = child.hSize * (hStore - spanPoints - (childPool + 1) * spacing) / childPool;
            // Set the height to be the number of claims of this child over the total number of claims times the remaining height available of the parent

            h += spacing * (child.hSize - 1); // add any extra spacings that were eaten up if this child spans multiple claims
            if(!child.hSpaced){
                h += 2 * spacing; // if the child is not spaced, then add two spacings to make it fill the space
            }
        }
        return h;
    }

    /**
     * Calculates the total number of points of this {@code KitBit} that are reserved for fixed height children.
     *
     * @param children the list of children of this parent whose heights are being calculated
     * @return the total number of points taken by fixed height children as a float
     * */
    public float calculateHeightSpanPoints(List<KitBit> children){
        float points = 0;
        for (KitBit child : children){ // loop over all the children of this KitBit
            if(!child.isHidden()){ // Check to make sure the child is not hidden
                points += child.hPoints == null ? 0 : child.hPoints * pointSize + spacing;
                // if this child has a fixed height, add it to the total amount of points taken (with an additional spacing for this child)
            }
        }
        return points; // return the total number of points taken by the fixed height children
    }

    /**
     * Calculates the fraction of the height of this {@code KitBit} that is available for variable fractional height
     * children after fixed and fractional height children have had their space allocated.
     *
     * @param children the list of children of this parent whose heights are being calculated
     * @return the fraction of the height available for variable fractional height children as a float
     * */
    public float calculateHeightSpanSpace(List<KitBit> children){
        float claims = 0;
        float space = 1;
        for (KitBit child : children){ // loop over all the children of this KitBit
            if(!child.isHidden()){ // Check to make sure the child is not hidden
                if(child.hSize != null){ // Check to make sure the child has fractional height
                    if(child.hPool==null) {
                        claims += child.hSize; // if the child wants x/n, increase the number of claims by the x
                    } else {
                        space -= (float)child.hSize / child.hPool; // if the child wants x/y, decrease the space by that fraction
                    }
                }
            }
        }
        return claims/space; // return the fraction of claims to space
    }

    /**
     * Calculates the width of a child assuming that the children has no neighbors in the horizontal direction. This
     * method is called by a parent to figure out the width of its children.
     *
     * @param child the KitBit child of this KitBit whose width is being calculated
     * @return float for the width of this child, measured in pixels
     *
     * @see KitBit#calculateW(KitBit, float, float)
     * */
    public float calculateW(KitBit child) {
        return calculateW(child, 0, 1);
    }

    /**
     * Calculates the width of a child given the number of points already taken by children that have fixed width and
     * the fraction of the width of the parent available after fixed width and fractional width children have had their
     * widths allocated. Called by a parent to figure out the widths of its children.
     *
     * @param child the KitBit child of this KitBit whose width is being calculated
     * @param spanPoints the number of points already taken by children of this KitBit who have fixed width, calculated
     *                   by {@link KitBit#calculateWidthSpanPoints(List) calculateWidthSpanPoints(List)}
     * @param spanSpace the fraction of the width of this KitBit available after children with fixed or fractional widths
     *                  have been allocated, calculated by
     *                  {@link KitBit#calculateWidthSpanSpace(List) calculateWidthSpanSpace(List)}
     * @return float for the width of this child, measured in pixels
     * */
    public float calculateW(KitBit child, float spanPoints, float spanSpace){

        float w;
        if(child.wRigid && (child.wEqual == null) && !(child.wWraps)){
            // this child's width is a fixed point value, so simply calculate it
            w = child.wPoints * pointSize;

        } else if(child.wWraps) {
            // this child's width is wrapping
            w = 0;
            if(child.label != null){ // check to see if this child has a label
                w = child.label.calculateW() + 2 * spacing; // if so, w is the width of the label plus two spacing
            }
            // then check all of the children of this child
            for (KitBit c : child.children){
                if(c.wRigid && !c.isHidden()){ // if a child is rigid and not hidden...
                    w = Math.max(w, c.wPoints * pointSize + spacing * 2 ); // take the larger of w and this child's width
                }
            }

        } else if(child.wEqual != null) {
            // this child's width is proportional to its height
            if(child.hEqual == null){
                w = child.wEqual * calculateH(child); // set the width of the child to be the proportionality constant
                // times the height of the child, assuming the child has no neighbors
            } else {
                System.out.println("WARNING: Width and height of a KitBit cannot both be proportional to each other!");
                System.out.println("Setting the width of " + child + " to be 0");
                w = 0; // if both width and height were set to be proportional to each other, set w = 0 to catch the error
            }
        } else {
            // this child's width is fractional
            float childPool = child.wPool == null ? spanSpace : child.wPool;
            // if the child has requested x/n, then take the spanSpace to be the denominator. Otherwise take wPool to be the denominator.

            w = child.wSize * (wStore - spanPoints - (childPool + 1) * spacing) / childPool;
            // Set the width to be the number of claims of this child over the total number of claims times the remaining width available of the parent

            w += spacing * (child.wSize - 1); // add any extra spacings that were eaten up if this child spans multiple claims
            if(!child.wSpaced){
                w += 2 * spacing; // if the child is not spaced, then add two spacings to make it fill the space
            }

        }
        return w;

    }

    /**
     * Calculates the total number of points of this {@code KitBit} that are reserved for fixed width children.
     *
     * @param children the list of children of this parent whose widths are being calculated
     * @return the total number of points taken by fixed width children as a float
     * */
    public float calculateWidthSpanPoints(List<KitBit> children){
        float points = 0;
        for (KitBit child : children){ // loop over all the children of this KitBit
            if(!child.isHidden()){ // Check to make sure the child is not hidden
                points += child.wPoints == null ? 0 : child.wPoints * pointSize + spacing;
                // if this child has a fixed width, add it to the total amount of points taken (with an additional spacing for this child)
            }
        }
        return points; // return the total number of points taken by the fixed width children
    }

    /**
     * Calculates the fraction of the width of this {@code KitBit} that is available for variable fractional width
     * children after fixed and fractional width children have had their space allocated.
     *
     * @param children the list of children of this parent whose widths are being calculated
     * @return the fraction of the width available for variable fractional width children as a float
     * */
    public float calculateWidthSpanSpace(List<KitBit> children){
        float claims = 0;
        float space = 1;
        for (KitBit child : children){ // loop over all the children of this KitBit
            if(!child.isHidden()){ // Check to make sure the child is not hidden
                if(child.wSize != null){ // Check to make sure the child has fractional width
                    if(child.wPool == null) {
                        claims += child.wSize; // if the child wants x/n, increase the number of claims by the x
                    } else {
                        space -= (float)child.wSize / child.wPool; // if the child wants x/y, decrease the space by that fraction
                    }
                }
            }
        }
        return claims/space; // return the fraction of claims to space
    }

    /**
     * Computes the x position of a child {@code KitBit} on the screen after taking into account its justification,
     * given the width of the child granted it by its parent.
     *
     * @param child a KitBit whose x position is being calculated
     * @param width float for the width given to the child by the parent, measured in pixels
     * @return the x position of the left side of the child on the screen, measured in pixels from the left side of the
     * screen
     * */
    public float calculateX(KitBit child, float width){
        float x;
        if(child.x == LEFT){ // if this child is left justified, it will be in the left most possible spot
            if(child.wSpaced){
                x = xStore + spacing;
            } else {
                x = xStore;
            }
        } else if (child.x == RIGHT){ // if this child is right justified, it's right edge will be at the right of the width allotted to it
            if(child.wSpaced){
                x = xStore + wStore - width - spacing;
            } else {
                x = xStore + wStore - width;
            }
        } else {
            x = xStore + 0.5f*wStore - 0.5f*width; // if this child is centered, its center will be in the center of the space
        }
        return x;
    }

    /**
     * Computes the y position of a child {@code KitBit} on the screen after taking into account its justification,
     * given the height of the child granted it by its parent.
     *
     * @param child a KitBit whose y position is being calculated
     * @param height float for the height given to the child by the parent, measured in pixels
     * @return the y position of the bottom of the child on the screen, measured in pixels from the bottom of the screen
     * */
    public float calculateY(KitBit child, float height){
        float y;
        if(child.y == BOTTOM){
            if(child.hSpaced){
                y = yStore + spacing;
            } else {
                y = yStore;
            }
        } else if (child.y == TOP){
            if(child.wSpaced){
                y = yStore + hStore - height - spacing;
            } else {
                y = yStore + hStore - height;
            }
        } else {
            y = yStore + 0.5f*hStore - 0.5f*height;
        }
        return y;
    }

    /**
     * Checks to see whether this {@code KitBit} is in focus. By default, this simply checks to see if the element is
     * under the cursor through the {@link KitBit#isUnder(Vector) isUnder(Vector)} method, but this can be overridden
     * for more complex behavior.
     *
     * @param input an Input, which handles user input from keyboards, mice and controllers and is typically passed down
     *              by the parent of this KitBit
     * @return true if this KitBit is in focus
     * */
    public boolean checkFocus(Input input){
        return isUnder(input.getCursorLocation());
    }

    /**
     * Sets the spacing and pointSize of this {@code KitBit} before recursively calling this method on all children of
     * this {@code KitBit}. This method can be overridden to change the pointsSize or spacing of a child relative to its
     * parent's.
     *
     * @param spacing float for the number of pixels in the spacing between this element and its neighbors
     * @param pointSize float for the number of pixels per point for this element
     * */
    public void configure(float spacing, float pointSize){
        this.spacing = spacing;
        this.pointSize = pointSize;
        for(KitBit child : children){
            child.configure(spacing, pointSize);
        }
    }

    /**
     * Destroy this {@code KitBit} by ripping (removing) it from its parent's list of children after recursively calling
     * {@code destroy()} on each of the children of this {@code KitBit}. Deletes the list of children referencing
     * this {@code KitBit} and reference of this {@code KitBit's} parent to this, thereby removing all references to
     * this {@code KitBit}. It will eventually be garbage collected.
     * */
    public void destroy(){

        if (childIndex == -1) {
            return; // this ensures that we never try to destroy the same child twice
        }

        if (children != null) {
            for (KitBit child : children) {
                if (child != null) {
                    child.destroy(); // recursively destroy all children
                }
            }
        }

        KitBit removedChild = parent.ripChild(childIndex);
        if (this != removedChild) { // ensure the correct child got removed
            System.out.println("WARNING: Removed the wrong child from its parent!!");
            System.out.println("List of parent's children: " + parent.children);
            System.out.println("Child that was actually removed: " + removedChild);
            System.out.println("Child that should have been removed: " + this);
            (new Exception()).printStackTrace();
        }

        hidden = true; // just 'cause
        childIndex = -1; // set the childIndex to -1 so that we can check later to see if this KitBit has already been destroyed
    }

    /**
     * Calls {@link KitBit#drawFrame(float, float, float, float, float) drawFrame(float, float, float, float, float)} on
     * each of the children of this {@code KitBit}. Calculates the position and size of each child under the assumption
     * that each child has no neighbors (or doesn't care about its neighbors). This method is here primarily to allow
     * for possible overrides, if desired.
     *
     * @param Z float for the z-coordinate of the this KitBit
     *
     * @see GlooKit.GlooFramework.KitBit#drawFrame(float, float, float, float, float)
     * */
    public void drawChildren(float Z){
        for(KitBit child : children){
            if (child != null) {
                if (!child.isHidden()) {
                    float w = calculateW(child);
                    float h = calculateH(child);
                    float x = calculateX(child, w);
                    float y = calculateY(child, h);
                    child.drawFrame(x, y, w, h, Z);
                }
            }
        }
    }

    /**
     * Prepares this element for drawing by instructing this {@code KitBit's frame} and {@code label} to draw themselves
     * through the {@link Drawable#draw(float, float, float, float, float) frame.draw(float, float, float, float, float)}
     * and {@link Label#draw(float, float, float, float, float) label.draw(float, float, float, float, float)} methods.
     * Although this method does not inherently call on all of the children of this {@code KitBit} to draw themselves,
     * it can be overridden to do so by also calling the {@link KitBit#drawChildren(float) drawChildren(float)}, which
     * is done by many specialized {@code KitBits}. This method is almost always called by the parent {@code KitBit},
     * meaning that the parent is responsible for determining and passing all variables to the children. This also
     * stores the values of X, Y, W and H that are passed for use later when checking focus or determining width and
     * height.
     *
     * @param X float for the horizontal position of the bottom left corner of this KitBit on the screen, measured in
     *          pixels from the left side of the screen
     * @param Y float for the vertical position of the bottom left corner of this KitBit on the screen, measured in
     *          pixels from the bottom of the screen
     * @param W float for the width of this KitBit on the screen, measured in pixels
     * @param H float for the height of this KitBit on the screen, measured in pixels
     * @param Z float for the z-coordinate of this KitBit on the screen, ranging from -1 to +1, where +1 is closest to
     *          the screen and -1 is furthest from the screen. This does not change the width or height of this KitBit
     *          but determines whether this KitBit is drawn in front of or behind another KitBit.
     * */
    public void drawFrame(float X, float Y, float W, float H, float Z){

        xStore = X; // store X, Y, W, H for use in checking focus
        yStore = Y;
        wStore = W;
        hStore = H;

        if(frame != null){frame.draw(X, Y, W, H, Z);} // pass on the draw call to the frame and label, if any exist
        if(label != null){label.draw(X, Y, W, H, Z);}

    }

    /**
     * Gets the {@code GlooApplication} of this {@code KitBit} by recursively calling {@code parent.getApp()} until
     * it reaches the {@code GlooApplication}
     *
     * @return the GlooApplication that this KitBit is an element in
     * */
    public GlooApplication getApp(){
        return parent.getApp();
    }

    /**
     * Gets the spacing of this element, which is the number of pixels of space between this {@code KitBit} and any of
     * its neighbors, assuming that neither is set to fill
     *
     * @return the number of pixels between this KitBit and its neighbors
     * */
    public float getSpacing(){
        return spacing;
    }

    /** Gets the array list of children of this element, which is a {@code List} of {@code KitBits}.
     *
     * @return the children list of this KitBit*/
    public List<KitBit> getChildren() {
        return children;
    }

    /**
     * Sets the {@code hidden} boolean of this {@code KitBit} to be {@code true}, making it invisible.
     * */
    public void hide(){
        hidden = true;
    }

    /**
     * Returns whether this {@code KitBit} is not visible
     *
     * @return true if this KitBit is hidden (invisible)
     * */
    public boolean isHidden(){
        return hidden;
    }

    /**
     * Returns whether this {@code KitBit} has a fixed height measured in points
     * @return true if this KitBit has fixed height
     * */
    public boolean isHRigid(){
        return hRigid;
    }

    /**
     * Returns whether this {@code KitBit} will reserve spacing for its height
     * @return true if this KitBit has vertical spacing
     * */
    public boolean isHSpaced(){
        return hSpaced;
    }

    /**
     * Returns whether this {@code KitBit's} height will be the smallest possible container for the height of its child
     * with the largest rigid height
     * @return true if this KitBit will be the smallest encapsulating height, given its fixed height children's heights
     * */
    public boolean isHWraps(){
        return hWraps;
    }

    /**
     * Returns whether this {@code KitBit} is under the given cursor. This method can be overridden to query the
     * {@code frame} of this {@code KitBit} for more accurate position, if so desired.
     *
     * @param cursor a Vector which is the location of the cursor on the screen, measured in pixels from the bottom left
     *               corner
     * @return true if this KitBit is under the cursor
     * */
    public boolean isUnder(Vector cursor){
        if(xStore < cursor.x() && cursor.x() < wStore + xStore) {
            return yStore < cursor.y() && cursor.y() < hStore + yStore;
        }
        return false;
    }

    /**
     * Returns whether this {@code KitBit} has a fixed width measured in points
     * @return true if this KitBit has fixed width
     * */
    public boolean isWRigid(){
        return wRigid;
    }

    /**
     * Returns whether this {@code KitBit} will reserve spacing for its width
     * @return true if this KitBit has horizontal spacing
     * */
    public boolean isWSpaced(){
        return wSpaced;
    }

    /**
     * Returns whether this {@code KitBit's} width will be the smallest possible container for the width of its child
     * with the largest rigid width
     * @return true if this KitBit will be the smallest encapsulating width, given its fixed width children's widths
     * */
    public boolean isWWraps(){
        return wWraps;
    }

    /**
     * Remove and return a child from the list of children of this {@code KitBit}, given the index of the child in the
     * list of children.
     *
     * @param index int of the position of the child to be removed from the children list of this KitBit
     * @return a KitBit, which was the child at the index position of the children list of this KitBit
     * */
    public KitBit ripChild(int index){
        KitBit extracting = children.get(index);
        children.set(index, null);
        return extracting;
    }

    /**
     * Sets the {@code action} of this {@code KitBit}, which is a function that will be called when
     * {@link KitBit#activate() activate()} is called. This method can be overridden to allow for more complex functions
     * like {@code Consumers} and {@code BiConsumers}.
     *
     * @param action a Runnable, which is the function that will be called when activate() is triggered on this KitBit
     * */
    public void setAction(Runnable action){
        this.action = action;
    }

    /**
     * Sets the {@code frame} of this {@code KitBit}, which might include the shape, {@code Texture}, and other features
     *
     * @param frame a Drawable that is the visible part of this KitBit
     * */
    public void setFrame(Drawable frame){
        this.frame = frame;
    }

    /**
     * Sets the {@code label} of this {@code KitBit}, which is the text that is displayed on this element
     *
     * @param label a Label for this KitBit, which is the text and text formatting for this element
     * */
    public void setLabel(Label label){
        this.label = label;
    }

    /**
     * Sets the parent of this {@code KitBit} and the {@code childIndex}, which is the location of this {@code KitBit}
     * in the parent's children list by calling the {@link KitBit#addChild(KitBit) addChild(KitBit)} method of the
     * parent.
     *
     * @param parent the KitBit that this KitBit will be a child of
     * */
    public void setParent(KitBit parent){
        this.parent = parent;
        this.childIndex = parent.addChild(this);
    }

    /**
     * Sets the {@code hidden} boolean of this {@code KitBit} to be {@code false}, making it visible.
     * */
    public void show(){
        hidden = false;
    }

    /**
     * Calls {@link KitBit#stepFrame(double) stepFrame(double)} on each of the children of this {@code KitBit}. This
     * method is here primarily to allow for possible overrides, if desired.
     *
     * @param delta double of the number of seconds between the previous frame and the current frame
     *
     * @see GlooKit.GlooFramework.KitBit#stepFrame(double)
     * */
    public void stepChildren(double delta){
        for(KitBit child : children){
            if(child != null){
                child.stepFrame(delta);
            }
        }
    }

    /**
     * Process any changes that have happened to this element due to the passage of time between frames by calling the
     * {@link Drawable#stepFrame(double) frame.stepFrame(double)} and {@link Label#stepFrame(double) label.stepFrame(double)}
     * methods. Also calls {@link KitBit#stepChildren(double) stepChildren(double)}, which recursively calls
     * {@code stepFrame} on each of the children of this {@code KitBit}.
     * <p>
     * Unlike {@link KitBit#calcFrame(double, Input) calcFrame}, {@code stepFrame} is called on every object for every
     * frame, regardless of whether they are hidden or in focus through the recursive nature of the {@code stepChildren}
     * method.  This default method does not actually do anything but pass the call on to the {@code frame} and
     * {@code label} and the children of this {@code KitBit}, but it can be overloaded to do something fancier.
     *
     * @param delta double of the number of seconds between the previous frame and the current frame
     *
     * @see KitBit#calcFrame(double, Input)
     * */
    public void stepFrame(double delta){
        if(frame != null){frame.stepFrame(delta);}
        if(label != null){label.stepFrame(delta);}
        stepChildren(delta);
    }

}
