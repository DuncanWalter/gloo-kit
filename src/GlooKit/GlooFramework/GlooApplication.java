package GlooKit.GlooFramework;

import GlooKit.GlooAPI.GlooBatch;
import GlooKit.GlooAPI.GlooCore;
import GlooKit.GlooAPI.Worker;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * A GlooApplication is a game (or other application) within a GLWF window that handles the game loop for the whole
 * application. It has
 * <ul>
 *     <li>a window handle, which is a {@code} long that is the GLWF identifier</li>
 *     <li>an {@link Input Input}, which handles user input from keyboards, mice, and controllers</li>
 *     <li>a {@link Room Room}, which is the state that the game is in currently, like the Main Menu, Settings Screen,
 *     or a Single-Player Battle</li>
 *     <li>children (observers), which are {@link Nook Nooks} (and the one {@code Room}) that are present in the
 *     {@code GlooApplication} in the current state.</li>
 *     <li>and various settings which can be set by using the {@link GlooApplicationConfiguration GlooApplicationConfiguration}
 *     class. The config file for a given GlooApplication is named {@code name.cfg}, where name is the String passed
 *     to the constructor.</li>
 * </ul>
 * {@code GlooApplication} also extends {@link KitBit KitBit}, as it is the parent of all other {@code KitBits} in the
 * application, by virtue of being the parent of all {@code Nooks} and {@code Rooms}.
 * <p>
 * A GlooApplication also handles the game loop, running until the {@link GlooApplication#close() close()} method is
 * called. The game loop runs four main methods each frame:
 *     <p>
 *     First {@link GlooApplication#drawFrame() drawFrame()} handles the pre-rendering of all screen
 *     elements by recursively calling
 *     {@link KitBit#drawFrame(float, float, float, float, float) drawFrame(float, float, float, float, float)} on each
 *     of the {@code KitBits} on the screen. {@code drawFrame()} also handles all of the rendering by instructing the
 *     {@link GlooCore GlooCore} to {@link GlooCore#renderFrame(int, int) renderFrame(int, int)}. This, in turn tells
 *     all {@link GlooBatch GlooBatches} to render all of their {@link GlooKit.GlooAPI.DrawingObjects.DrawingObject DrawingObjects}
 *     to the {@code RenderBuffer}. This does not actually draw the rendered frame to the screen.
 *     <p>
 *     {@link GlooApplication#calcFrame(double) calcFrame(double)} is the first half of the update loop, which
 *     processes changes due to user input. This happens by calling
 *     {@link KitBit#calcFrame(double, Input) calcFrame(double, Input)} on the first {@code Nook} that is in focus. This
 *     ensures that {@code Nooks} are input blocking. If no such {@code Nook} exists, {@code calcFrame} is instead
 *     called on the {@code Room} of this {@code GlooApplication}. This, in turn, recursively gets called on any
 *     children of the {@code Nook}.
 *     <p>
 *     {@link GlooApplication#stepFrame(double) stepFrame(double)} is the second half of the update loop, which
 *      processes changes to all screen elements due to the passage of time between two frames.
 *     {@link KitBit#stepFrame(double) stepFrame(double)} is first called on the {@code Room} of this {@code GlooApplication},
 *     which recursively calls it on all screen elements, as all elements are descendants of the {@code Room}, in one
 *     way or another. Whereas {@code calcFrame} is not guaranteed to happen on a given element during a frame,
 *     {@code stepFrame} will happen every frame to every element.
 *     <p>
 *     Finally {@code glfwSwapBuffers(long)} is called which actually draws the rendered frame to the screen. The number
 *     of nanoseconds in a frame is the {@code FRAME_LENGTH} variable, which can be set in the config file or through
 *     the {@code GlooApplicationConfiguration} class by specifying a {@code framesPerSecond}. Any spare time will
 *     simply be spent waiting, which will ensure a maximum number of frames per second. (No such guarantee is given for
 *     the minimum number of frames per second.
 * <p>
 * Rendering happens primarily through the {@link GlooCore GlooCore}, which handles everything from
 * {@link GlooKit.GlooAPI.Texture Textures} to {@link GlooKit.GlooAPI.GlooFontFamily FontFamilies} through
 * {@link GlooBatch GlooBatches}. Multiple {@code GlooApplications} can be run at the same time by employing the
 * {@link GlooApplication#runConcurrent(String, Consumer) runConcurrent(String, Consumer)} method. See
 * {@link Driver.Main Driver.Main} for an example implementation of running {@code GlooApplications}.
 *
 * @author Duncan Walter
 * @author Eli Jergensen
 * @since 1.0
 * */
public class GlooApplication extends KitBit {

    /** Boolean for whether an app has finished running yet (used to close the app) */
    private boolean complete;

    /** Handle to tell GLFW which window the app is */
    private long window;

    /** Display mode of the screen */
    private GLFWVidMode display;

    /** Name of the window and of the corresponding configuration file */
    public final String name;

    // eventual settings
    /** Number of nanoseconds in a frame */
    private final long FRAME_LENGTH;

    /** Size of a point (1/72 of an inch), measured in pixels */
    public final float pointSize;

    /** Width of the default spacing in an application, measured in pixels */
    private final float spacing;


    // for tracking and logging fps information
    /** Time that the last {@code stepFrame(double)} occurred, in nanoseconds */
    private long lastStep;

    /** Time that the last measurement of FPS occurred, in nanoseconds */
    private long lastFpsTime = 0;

    /** Number of frames that have occurred in the last second */
    private long fps = 0;


    // glooKit Hardware
    /** The {@code GlooCore} for this application that handles all render calls */
    private GlooCore core;

    /** The {@code Input} for this application that handles all mouse, keyboard, and controller inputs */
    private Input input;

    /** The "state" that the application is in. For example, this could be the main menu, the settings screen, or a
     * single-player battle */
    private Room room;

    /** A single int within an int array corresponding to the width of the display panel (within the window), in pixels */
    private int[] w;

    /** A single int within an int array corresponding to the height of the display panel (within the window), in pixels */
    private int[] h;


    // KitBit pieces
    /** List of children of the application, all of which must be {@code Nooks} (and one of which is a {@code Room}) */
    private List<Nook> children;

    ////////////////////////////////////////////
    ////////////////////////////////////////////

    /**
     * Creates a new {@code GlooApplication} given a name, which creates a GLFW window for the application with the
     * specified configurations in the config file (which is the file with the name {@code name.cfg}). If the config
     * file is empty, it creates a new config file. This defaults to specific configurations, which can be seen in the
     * {@link GlooApplicationConfiguration GlooApplicationConfiguration} file.
     *
     * @param name String that will be displayed in the name bar at the top of the window. Title is also used to
     *              specify the config file, {@code name.cfg}
     * */
    public GlooApplication(String name){
        super(0, 0, "0p+", "0p+");

        this.name = name;

        // loads in any existing configuration (or creates one from default)
        GlooApplicationConfiguration config = new GlooApplicationConfiguration(name + ".cfg");

        if(!glfwInit()){
            System.out.println("ERROR: could not instantiate glfw");
            System.exit(401);
        } else {
            display = glfwGetVideoMode(glfwGetPrimaryMonitor());
        }

        ///////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////

        // load in config sizing information
        GLFWVidMode mode = config.getDisplayMode();
        if(mode == null){mode = display;}
        int w = config.isFullscreen() ? mode.width() : (int)config.getSize().x();
        int h = config.isFullscreen() ? mode.height() : (int)config.getSize().y();
        long monitor = config.isFullscreen() ? glfwGetPrimaryMonitor() : NULL;
        // apply versioning and window hints
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, 1);
        glfwWindowHint(GLFW_STENCIL_BITS, 4);
        glfwWindowHint(GLFW_SAMPLES, 4);
//        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        // create the actual window (w, h, name, pass long for fullscreen, ??????)
        window = glfwCreateWindow(w, h, name, monitor, NULL);
        // get the pointSize of the instantiated window (not requested values when windowed)
        this.w = new int[1];
        this.h = new int[1];
        input = new Input(window);

        ///////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////

        // Make the window focused and visible
        glfwMakeContextCurrent(window);
        glfwShowWindow(window);
        // sets up OpenGL context in THIS THREAD
        GL.createCapabilities();
        // Sets up an error callback service
//        GLFWErrorCallback.createPrint(System.err).set();

        core = new GlooCore();
        core.init(this);

        ///////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////

        FRAME_LENGTH = 1000000000 / config.getFramesPerSecond();

        lastStep = System.nanoTime();
        children = new ArrayList<>();
        room = null;

        pointSize = Toolkit.getDefaultToolkit().getScreenResolution()/(float)72.272;
        spacing = config.getSpacingPoints() * pointSize;

    }

    /**
     * Overrides the {@link KitBit#addChild(KitBit) addChild(KitBit)} method of KitBit to ensure that only one {@code Room}
     * can be a child at a time. {@code Nooks} can still be added as desired, but all {@code Nooks } that are children
     * of a {@code Room} will be removed if a different {@code Room} is added.
     * <p>
     * Any added {@code Nook} will take any available slot or extend the list as necessary. Any {@code Room} added will
     * be added in the same manner, but will immediately call the recursive {@link KitBit#destroy() destroy()} method
     * on the previously existing {@code Room}.
     *
     * @param newChild a KitBit that can be either a Room or Nook (or an extension thereof), but not anything else
     * @return int the index of the child in the children list
     * */
    public int addChild(KitBit newChild){

        // ensure that only Nooks (and therefore Rooms) get added to the list
        if(!(newChild instanceof Nook)) {
            new Exception("newChild " + newChild + " invalid; not of types nook or room").printStackTrace();
            System.exit(300);
        }

        // first check to see if this child has already been added
        for(int i = 0; i < children.size(); i++) {
            if(children.get(i) == newChild) {
                System.out.println("This child is already a child of this parent");
                return i;
            }
        }

        // check to see if any slots are open
        for(int i = 0; i < children.size(); i++) {
            if(children.get(i) == null) {
                children.set(i, (Nook)newChild);
                if (newChild instanceof Room) { // if we added a new room, we need to remove the old one (and its children)
                    if (room != null) {
                        room.destroy();
                    }
                    room = (Room)newChild;
                }
                return i;
            }
        }

        // add to the next slot, since no slots were open
        children.add((Nook)newChild);
        if (newChild instanceof Room) { // if we added a new room, we need to remove the old one (and its children)
            if (room != null) {
                room.destroy();
            }
            room = (Room)newChild;
        }
        return children.size() - 1;
    }

    /**
     * Replacement of the {@link KitBit#calcFrame(double, Input) calcFrame(double, Input)} method of {@code KitBit}.
     * Calls {@code calcFrame} on only the first {@code Nook} that exists, is in focus, and is not a {@code Room}.
     * If no {@code Nooks} are in focus, then {@code calcFrame} is called on the {@code Room} of this {@code GlooApplication}
     * instead. In any case, {@code calcFrame} is eventually called on either one {@code Nook} or one {@code Room}.
     *
     * @param delta double for the number of seconds that have elapsed since the last calcFrame
     * @see GlooKit.GlooFramework.Nook#checkFocus(Input)
     * */
    private void calcFrame(double delta){
        Integer index = null;
        int i = 0;
        for(Nook child : children){
            if(child != null && child != room && child.checkFocus(input)){ // check to make sure the child exists, isn't a room, and the mouse is over a child
                index = i;
            }
            i += 1;
        }
        if(index == null){
            room.calcFrame(delta, input);
        } else {
            if (children.get(index) != null) { // if the child is null, it is because it was a modal that was not clicked on, so we the calc frame was (essentially) to close the modal
                children.get(index).calcFrame(delta, input);
            }
        }
    }

    /**
     * Overrides the {@link KitBit#calcFrame(double, Input) calcFrame(double, Input)} method of {@code KitBit} to call the
     * {@link GlooApplication#calcFrame(double) calcFrame(double)} method of {@code GlooApplication} instead.
     * This function should never be called and will spit out warnings if it is called, but will nonetheless call the
     * correct function, which calls {@code calcFrame} on only the first {@code Nook} that exists, is in focus, and is
     * not a {@code Room}. If no {@code Nooks} are in focus, then {@code calcFrame} is called on the {@code Room} of this
     * {@code GlooApplication} instead. In any case, {@code calcFrame} is eventually called on either one {@code Nook}
     * or one {@code Room}.
     *
     * @param delta double for the number of seconds that have elapsed since the last calcFrame
     * @param input an Input that does not actually get used
     * @see GlooKit.GlooFramework.GlooApplication#calcFrame(double)
     * @see GlooKit.GlooFramework.Nook#checkFocus(Input)
     * */
    public void calcFrame(double delta, Input input) {
        System.out.print("WARNING: calcFrame(double, Input) of GlooApplication was called instead of calcFrame(double)\n Please Fix!\n");
        (new Exception()).printStackTrace();
        System.out.println("Calling correct function");
        calcFrame(delta);
    }

    /**
     * Sets the {@code complete} boolean to true so that the game loop will exit.
     * */
    public void close(){
        complete = true;

    }

    /**
     * Overrides the {@link KitBit#destroy() destroy()} method of {@code KitBit} to prevent the {@code GlooApplication}
     * from trying to remove itself from its parent (it has none).
     * */
    public void destroy(){
        children.forEach(KitBit::destroy);
        children = null;
    }

    /**
     * Initiates the process of rendering to the screen.
     * <p>
     * First calls on the
     * {@link Room#drawFrame(float, float, float, float, float) drawFrame(float, float, float, float, float)} method of
     * {@code Room} to load the information necessary for rendering into arrays. This method recursively calls on all
     * children to ensure that all {@code KitBits} prepare themselves for rendering.
     * <p>
     * Then calls on the
     * {@link GlooCore#renderFrame(int, int) renderFrame(int, int)} method of {@code GlooCore} to actually render to the
     * screen. This method delegates the task of rendering to various {@code GlooBatches}. Rendering actually happens
     * not directly to the screen but instead to a {@code RenderBuffer}. This gets rendered to the screen during the
     * {@code glfwSwapBuffers(long)} call, which happens later in the game loop.
     * */
    private void drawFrame(){
        glfwGetFramebufferSize(window, w, h);
        room.drawFrame(0, 0, w[0], h[0], 0);
        core.renderFrame(w[0], h[0]);
    }

    /**
     * Gets this {@code GlooApplication} itself. This overrides the {@link KitBit#getApp() getApp()} method of
     * {@code KitBit} to prevent this {@code GlooApplication} from trying to ask its parent for its parent's
     * {@code GlooApplication}, as this {@code GlooApplication} is already the one requested.
     *
     * @return this GlooApplication itself
     * */
    public GlooApplication getApp(){
        return this;
    }

    /** Gets the {@code GlooBatch} of the {@code GlooCore} of this application, given an integer handle.
     *
     * @param batchHandle int handle of the GlooBatch that was returned when the GlooBatch was constructed
     * @return GlooBatch of this application matching the given handle */
    public GlooBatch getBatch(int batchHandle){
        return core.getBatch(batchHandle);
    }

    /** Gets the {@link GlooBatch GlooBatch} of the {@code GlooCore} of this application, given a name of the batch.
     *
     * @param batch String corresponding to the name of the GlooBatch
     * @return GlooBatch of this application matching the given name */
    public GlooBatch getBatch(String batch){
        return core.getBatch(core.getHandle(batch));
    }

    /**
     * Overrides the {@link KitBit#getChildren() getChildren()} method of {@code KitBit} to cast the children list of
     * {@code GlooApplication} from a list of {@code Nooks} to a list of {@code KitBits}.
     *
     * @return the children list of this GlooApplication as a list of KitBits
     * */
    public List<KitBit> getChildren(){
        List<KitBit> childrenAsKitBits = new ArrayList<>();

        // Manually copy the list to cast the children array to a KitBit array from the Nook array
        for(int i = 0; i < children.size(); i++) {
            childrenAsKitBits.set(i, children.get(i));
        }

        return childrenAsKitBits;
    }

    /**
     * Gets the {@code GlooCore} of this {@code GlooApplication}, which is responsible for handling all of the rendering
     * calls.
     *
     * @return the primary GlooCore of this GlooApplication
     * */
    public GlooCore getCore(){
        return core;
    }

    /**
     * Gets the {@link Worker WorkerPool} of this {@code GlooApplication}, which is the {@code ThreadPool} of the
     * {@code GlooCore}. The {@code WorkerPool} is used to greatly expedite the process of pre-rendering through the
     * power of multithreading!
     *
     * @return the {@code WorkerPool}({@code ThreadPool}) of this {@code GlooApplication} for use in multithreading
     * */
    public Worker getPool(){
        return core.getPool();
    }

    /**
     * Gets the number of pixels in the width of the default spacing of this {@code GlooApplication}.
     *
     * @return width of the spacing of this GlooApplication, measured in pixels
     * */
    public float getSpacing(){
        return spacing;
    }

    /**
     * Overrides the {@link KitBit#ripChild(int) ripChild(int)} method of {@code KitBit} to use {@code Nooks} instead
     * of general {@code KitBits}. Removes and returns the {@code Nook} at the requested position of the {@code children}
     * list.
     *
     * @param index int of the position of the Nook in the children list
     * @return the Nook in the children list at index
     * */
    public Nook ripChild(int index){
        Nook extracting = children.get(index);
        children.set(index, null);
        return extracting;
    }

    /**
     * <p>
     * Starts the Application by running the game loop, which will call {@code drawFrame()}, {@code calcFrame(double)},
     * and {@code stepFrame(double)} every frame until told to stop. The three ways the game loop will exit are:
     * <ol>
     *     <li>An error occurs, printing a stacktrace and stopping the program</li>
     *     <li>The window is set to close by calling {@code glfwWindowShouldClose(long)} or </li>
     *     <li>The {@code close()} method of {@code GlooApplication} is called.</li>
     * </ol>
     * It is preferred that the {@code close()} method be called instead of {@code glfwWindowShouldClose(long)}.
     * <p>
     * Every frame, four primary methods are called:
     * <ol>
     *     <li>{@code drawFrame()}, which handles pre-rendering and rendering, but does not draw directly to the screen</li>
     *     <li>{@code calcFrame(double)}, which processes changes to screen elements due to user input</li>
     *     <li>{@code stepFrame(double)}, which processes changes to all screen elements due to the passage of time
     *     between frames</li>
     *     <li>{@code glfwSwapBuffers(long)}, which actually draws the rendered frame to the screen</li>
     * </ol>
     * The game loop also keeps track of fps. If the amount of time it takes a frame to handle these methods in less than
     * the number of nanoseconds in {@code FRAME_LENGTH}, then the thread sleeps for the remainder of time to ensure that
     * there is a maximum fps. No such assurance can be given on minimum fps.
     * */
    public void run(){
        // keep looping round til the application is complete
        Worker pool = new Worker(1);
        Future update;
        float work = 0;

        try{
            while(!complete){
                // tracking our fps
                long frameTimer = System.nanoTime();
                if (frameTimer - lastFpsTime >= 1000000000) {
                    System.out.println("\n" + fps + " fps @ " + Math.round(work / fps * 100) + "%");
                    lastFpsTime = System.nanoTime();
                    fps = 0;
                    work = 0;
                }
                drawFrame();
                long clockTimer = System.nanoTime();
                double delta = (clockTimer - lastStep) / 1000000000.0;
                lastStep = clockTimer;

                input.update();
                update = pool.task(()->{
                    calcFrame(delta);
                    stepFrame(delta);
                });
                glfwSwapBuffers(window);
                pool.await(update);

                fps += 1;
                long remainder = frameTimer + FRAME_LENGTH - System.nanoTime();
                work += (float)(System.nanoTime() - frameTimer) / FRAME_LENGTH;

                if (remainder > 0) {
                    try {
                        Thread.sleep(remainder / 1000000);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(-500);
                    }
                }

                if (glfwWindowShouldClose(window)) {
                    close();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Free the window callbacks and destroy the window
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
            core.destroy();
            pool.destroy();
            complete = true;
        }
    }

    /**
     * Spins off a new GlooApplication to run independently of a previously existing one in a new thread, given a name
     * and a setup {@code Consumer}. The thread cleans itself up when the {@code GlooApplication} has finished running.
     *
     * @param name String that will be passed on to the GlooApplication constructor. This is the name of the window and
     *             of the config file, name.cfg
     * @param setup a Consumer that, while possibly any function whatsoever, typically is a function that specifies the
     *              Room that will initially be loaded.
     * @return a Future used to monitor the state of the GlooApplication
     * */
    public static Future runConcurrent(String name, Consumer<GlooApplication> setup){
        Worker w  = new Worker(1);
        return w.task(()->{
            GlooApplication app = new GlooApplication(name);
            setup.accept(app);
            app.run();
            w.destroy();
        });
    }

    /**
     * Overrides the {@link KitBit#stepFrame(double) stepFrame(double)} method of {@code KitBit} to call {@code stepFrame}
     * on only the {@code Room} of the {@code GlooApplication}, instead of all children. Since all {@code KitBits} are
     * descendants of the {@code Room} in one way or another and the {@code stepFrame(double)} method of {@code KitBit}
     * is recursive, {@code stepFrame} will eventually be called on all {@code KitBits}.
     *
     * @param delta double for the number of seconds that have elapsed since the last stepFrame
     * */
    public void stepFrame(double delta){
        room.stepFrame(delta);
    }

}
