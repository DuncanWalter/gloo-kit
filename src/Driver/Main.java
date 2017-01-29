package Driver;

import GlooKit.GlooFramework.GlooApplication;

public class Main{

    /*
    * The TODO list is back!
    *
    * Add tools for seeing the bounding boxes of KitBits
    * Add a diagnostics mode or something
    *
    * */

    public static void main (String[] args) {

        try{

            GlooApplication Launcher = new GlooApplication("Launcher");
            LauncherMenu.open(Launcher);
            Launcher.run();

        } catch(Exception e){

            e.printStackTrace();
            System.exit(-350);

        }

    }

}
