package GlooKit.GlooFramework;


import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GlooApplicationWindow {

    /** Handle to tell GLFW which window the app is */
    private long windowHandle;

    /** Display mode of the screen */
    private GLFWVidMode videoMode;

    public float dpi;

    private GlooApplicationConfiguration config;

    /** A single int within an int array corresponding to the width of the videoMode panel (within the window), in pixels */
    private int[] w;

    /** A single int within an int array corresponding to the height of the videoMode panel (within the window), in pixels */
    private int[] h;



    private int monitorIndex;

    private long monitor;

    private boolean fullscreen;

    public GlooApplicationWindow (GlooApplicationConfiguration config) {
        this.config = config;

        setupWindow();
    }

    private void setupWindow() {

        monitorIndex = config.getMonitorIndex(); // grab the monitor index from the config file

        PointerBuffer monitorsBuffer = glfwGetMonitors();
        monitorsBuffer.position(0); // ensure that we are at the 0th position

        if (!(monitorIndex < monitorsBuffer.remaining() && monitorIndex >= 0)) {
            // if the monitorIndex is not a valid index, revert to 0 (the primary monitor)
            monitorIndex = 0;
            config.setMonitorIndex(monitorIndex); // inform the config file of the reversion
        }

        monitor = monitorsBuffer.get(monitorIndex); // grab the monitor from the buffer
        videoMode = glfwGetVideoMode(monitor); // grab the video mode from the buffer

        /////////////////////////////////////////////////////////////////////
        System.out.println("Video Modes of Monitor number " + monitorIndex);
        System.out.println("Actual video mode: " + videoMode.width() + " x " + videoMode.height());
        for(int i = 0; i < glfwGetVideoModes(monitor).limit(); i++) {
            System.out.println(glfwGetVideoModes(monitor).get(i).width() + " x " + glfwGetVideoModes(monitor).get(i).height());
        }
        /////////////////////////////////////////////////////////////////////

        fullscreen = config.isFullscreen(); // grab whether the window is fullscreen
        int w, h;
        long displayMonitor;
        if (fullscreen) {
            w = videoMode.width(); // if fullscreen, grab the entire width and height of the screen
            h = videoMode.height();
            displayMonitor = monitor; // if fullscreen, use the monitor specified
        } else {
            w = (int) config.getSize().x(); // if windowed, grab the width and height specified in the config
            h = (int) config.getSize().y();
            displayMonitor = NULL; // if windowed, use NULL as the monitor
        }

        // apply versioning and window hints
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, 1);
        glfwWindowHint(GLFW_STENCIL_BITS, 4);
        glfwWindowHint(GLFW_SAMPLES, 4);
//        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        glfwWindowHint(GLFW_RED_BITS, videoMode.redBits()); // ensure that the current video mode sticks around
        glfwWindowHint(GLFW_GREEN_BITS, videoMode.greenBits());
        glfwWindowHint(GLFW_BLUE_BITS, videoMode.blueBits());
        glfwWindowHint(GLFW_REFRESH_RATE, videoMode.refreshRate());
        //glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);

        // create the actual window (w, h, name, pass long for fullscreen, window to inherit properties from!!!!)
        windowHandle = glfwCreateWindow(w, h, "TEMP NAME", displayMonitor, NULL);

        videoMode = glfwGetVideoMode(monitor); // grab the video mode again, in case it changed when we made the window
        if (fullscreen) {
            w = videoMode.width(); // if fullscreen, grab the entire width and height of the screen
            h = videoMode.height();
        } else {
            w = (int) config.getSize().x(); // if windowed, grab the width and height specified in the config
            h = (int) config.getSize().y();
        }

        // Center the window on the monitor desired
        int[] monitorLeft = new int[1]; // x - coord of left side of monitor where window is on
        int[] monitorBottom = new int[1]; // y - coord of bottom of monitor where window is on
        glfwGetMonitorPos(monitor, monitorLeft, monitorBottom); // can't use monitor, cause monitor is NULL

        // Bottom-Left corner of screen plus half the width/height of the screen gives center of screen
        // Subtracting half of the size of the window itself gives the bottom-left corner of the window
        int windowXPos = monitorLeft[0] + videoMode.width()/2 - w/2;
        int windowYPos = monitorBottom[0] + videoMode.height()/2 - h/2;
        glfwSetWindowMonitor(windowHandle, displayMonitor, windowXPos, windowYPos, w, h, GLFW_DONT_CARE);
//        glfwSetWindowPos(windowHandle, windowXPos, windowYPos); // actually set the position of the window
//        glfwSetWindowSize(windowHandle, w, h); // actually set the size of the window

        videoMode = glfwGetVideoMode(monitor);
        System.out.println("Actual VideoMode: " + videoMode.width() + " x " + videoMode.height());


        ///////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////

        // Make the window focused and visible
        glfwMakeContextCurrent(windowHandle);
        glfwShowWindow(windowHandle);
        // sets up OpenGL context in THIS THREAD
        GL.createCapabilities();
        // Sets up an error callback service
//        GLFWErrorCallback.createPrint(System.err).set();


        // Stuff for trying to figure out screen resolution...
//        GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        for (int i = 0; i < graphics.getScreenDevices().length; i++) {
//            System.out.println(graphics.getScreenDevices()[i].getDisplayMode().getHeight());
//            System.out.println(graphics.getScreenDevices()[i].getDisplayMode().getWidth());
//            System.out.println();
//        }
//
//        System.out.println(Toolkit.getDefaultToolkit().getScreenResolution());

        int[] widthInMM = new int[1];
        int[] heightInMM = new int[1]; // not used, but needed for getting the monitor size
        glfwGetMonitorPhysicalSize(glfwGetMonitors().get(monitorIndex), widthInMM, heightInMM);

        for(int i = 0; i < widthInMM.length; i++) {
            System.out.println(widthInMM[i] + " x " + heightInMM[i]);
            System.out.println(videoMode.width() + " x " + videoMode.height());
        }

        dpi = (float) (videoMode.width() / (widthInMM[0] / 25.4));
        System.out.println(Toolkit.getDefaultToolkit().getScreenResolution());
        System.out.println(dpi);



        // TODO setup monitor closing callbacks!
        // glfwSetMonitorCallback()
        // glfwSetFramebufferSizeCallback()

    }


    public long getWindowHandle() {
        return windowHandle;
    }
}
