package GlooKit.GlooFramework;
import GlooKit.Utils.Vector;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class Input {

    // TODO split into an interface and concrete class to put into GLOO and HTML packages

    private Vector cursorLocation;
    private Vector cursorVelocity;
    private Vector scrollVelocity;

    private Map<Integer, Integer> eventStates;

    private long window;
    private int[] w;
    private int[] h;

    // CONSTRUCTORS
    public Input(long Window){
        // TODO set up GLFW callbacks to capture input properly
        window = Window;

        w = new int[1];
        h = new int[1];

        glfwGetFramebufferSize(window, w, h);

        eventStates = new HashMap<>(200);

        cursorLocation = new Vector(0, 0, 0);
        cursorVelocity = new Vector(0, 0, 0);
        scrollVelocity = new Vector(0, 0, 0);

        glfwSetCursorPosCallback(Window, (window, x, y)->{
            cursorLocation = new Vector((float)x, h[0] - (float)y, 0);
        });
        glfwSetKeyCallback(Window, (window, key, scancode, action, mods)->{
            if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE){
                glfwSetWindowShouldClose(window, true);
            }
            if(eventStates.containsKey(key)){
                eventStates.replace(key, action);
            } else {
                eventStates.put(key, action);
            }
        });
        glfwSetMouseButtonCallback(Window, (window, button, action, mods)->{
            if(eventStates.containsKey(button)){
                eventStates.replace(button, action);
            } else {
                eventStates.put(button, action);
            }
        });
        glfwSetScrollCallback(Window, (window, dx, dy)->{
            scrollVelocity = new Vector((float)dx, (float)dy, 0);
        });
        glfwSetFramebufferSizeCallback(Window, (window, width, height)->{
            glViewport(0, 0, width, height);
            w[0] = width;
            h[0] = height;
        });
    }
    public Vector getCursorLocation(){
        return cursorLocation;

    }
    // METHODS
    public void update(){

        for(Integer i : eventStates.keySet()){
            eventStates.replace(i, null);
        }

        // will lock game up if any callbacks contain errors
        glfwPollEvents();

    }
    public boolean KeyState(int glfwKey) {
        return glfwGetKey(window, glfwKey) == GLFW_PRESS;

    }
    public boolean MouseState(int glfwMouseButton){
        return glfwGetMouseButton(window, glfwMouseButton) == GLFW_PRESS;

    }
    public boolean pollEvent(int glfwKey, int glfwAction){
        Integer eventState = eventStates.get(glfwKey);
        if(eventState == null){
            return false;
        } else {
            return eventState.equals(glfwAction);
        }
    }
}