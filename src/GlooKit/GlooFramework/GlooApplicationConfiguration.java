package GlooKit.GlooFramework;

import GlooKit.Utils.JSONObject;
import GlooKit.Utils.JSONable;
import GlooKit.Utils.Vector;
import org.lwjgl.glfw.GLFWVidMode;

import java.awt.*;
import java.io.*;

/**
 * GlooApplicationConfiguration is essentially a handle for accessing config files (.cfg) for the GlooApplication
 *
 * Allows for the configuration of fullscreen mode, window pointSize, and frames per second
 *
 * @author Duncan Walter
 * @author Eli Jergensen
 * @since 1.0
 * */
public class GlooApplicationConfiguration implements Serializable, JSONable<GlooApplicationConfiguration> {

    // toggles fullscreen / windowed mode
    private boolean fullscreen;


    private Vector size;

    // application spacing between elements by default
    private float spacingPoints;

    // accepts values from 28 - 180
    private int framesPerSecond;

    private String fileName;

    private int monitorIndex;


    /**
     * Attempts to load a config file. If the file is not found, it makes a new default one
     *
     * @param fileName A String representing the filepath
     * */
    public GlooApplicationConfiguration(String fileName){

        this.fileName = fileName;

        // load in the json file
        JSONObject json = new JSONObject();
        json.readFromFile(fileName);

        constructFromJSON(json);

//        try {
//            // try to load the config file
//
//
//
//            FileInputStream fileIn = new FileInputStream(fileName);
//            ObjectInputStream in = new ObjectInputStream(fileIn);
//            GlooApplicationConfiguration config  = (GlooApplicationConfiguration)in.readObject();
//            in.close();
//            fileIn.close();
//
////            System.out.println(config);
//
//            fullscreen = config.fullscreen;
//            displayMode = config.displayMode;
//            size = config.size;
//            framesPerSecond = config.framesPerSecond;
//            this.spacingPoints = config.spacingPoints;
//
//        }catch(IOException e) {
//            // if unable to load the config file (it probably doesn't exist), make a new default one
//
////            System.out.println(fileName + " not found; Creating a default config file");
//
//            fullscreen = false;
//            displayMode = null;
//            framesPerSecond = 60;
//            size = new Vector(960, 520, 0);
//            spacingPoints = 5f;
//            save();
//
//        }catch(ClassNotFoundException e) {
//
//            e.printStackTrace();
//
//        }

    }

    public JSONObject constructJSONObject() {
        JSONObject json = new JSONObject();

        json.add("fullscreen", fullscreen);  // boolean gets converted to string
        json.add("max fps", framesPerSecond); // number gets converted to string
        // We want to store the size as an array of strings
        String[] sizeAsStrings = new String[] {Float.toString(size.x()), Float.toString(size.y()), Float.toString(size.z())};
        json.add("size", sizeAsStrings);
        json.add("spacing points", spacingPoints); // number gets converted to string
        json.add("monitor number", monitorIndex); // number gets converted to string

        return json;
    }

    public GlooApplicationConfiguration constructFromJSON(JSONObject json) {

        fullscreen = json.fetchBoolean(false, "fullscreen"); // default to not fullscreen
        framesPerSecond = json.fetchInt(60, "max fps"); // default to 60fps
        spacingPoints = (float) json.fetchDouble(5.0, "spacing points"); // default to a spacing of 5f
        Double[] sizeAsDoubles = json.fetchDoubles("size");
        if (sizeAsDoubles == new Double[]{}) {
            size = new Vector(960, 520, 0); // default to 960 by 520
        } else {

            float x, y, z;

            if (sizeAsDoubles[0] == null) { // carefully copy over each field
                x = 960;
            } else {
                x = (float) (double) sizeAsDoubles[0];
            }

            if (sizeAsDoubles[1] == null) { // carefully copy over each field
                y = 520;
            } else {
                y = (float) (double) sizeAsDoubles[1];
            }

            if (sizeAsDoubles[2] == null) { // carefully copy over each field
                z = 0;
            } else {
                z = (float) (double) sizeAsDoubles[2];
            }

            size = new Vector(x, y, z);
        }
        monitorIndex = json.fetchInt(0, "monitor number");

        return this;
    }

    /**
     * Attempts to save a configuration to a .cfg file
     *
     * @return false if it failed to save, true if it saved
     * */
    public boolean save(){

        System.out.println("Saving " + fileName);

        JSONObject json = constructJSONObject();
        return json.writeToFile(fileName); // returns true only if it succeeds
//        try {
//
//
//            FileOutputStream fileOut = new FileOutputStream(fileName);
//            ObjectOutputStream out = new ObjectOutputStream(fileOut);
//            out.writeObject(this);
//            out.close();
//            fileOut.close();
//            return true;
//        }catch(IOException e) {
//            e.printStackTrace();
//            return false;
//        }
    }
    public boolean isFullscreen(){
        return fullscreen;

    }
    public void setFullscreen(boolean fullscreen){
        this.fullscreen = fullscreen;

    }
    public int getFramesPerSecond(){
        return framesPerSecond;

    }
    public void setFramesPerSecond(int framesPerSecond){
        this.framesPerSecond = framesPerSecond;

    }
    public Vector getSize() {
        return size;

    }
    public void setSize(Vector size) {
        this.size = size;

    }
    public float getSpacingPoints() {
        return spacingPoints;

    }
    public void setSpacingPoints(float spacingPoints) {
        this.spacingPoints = spacingPoints;

    }

    public void setMonitorIndex(int monitorIndex) {
        this.monitorIndex = monitorIndex;
    }
    public int getMonitorIndex() {
        return monitorIndex;
    }
}
