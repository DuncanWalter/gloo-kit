package Driver;

import GlooKit.GlooFramework.GlooApplication;
import java.util.Random;

public class Main{

    /*
    * The TODO list is back!
    *
    * Add tools for seeing the bounding boxes of KitBits
    * Add a diagnostics mode or something
    *
    * */

    public static void main (String[] args) {

        // Pick a random title from the list below
        Random random = new Random();
        int rand = random.nextInt(7); // change this if you add a new title
        String gameTitle;

        switch (0) {
            case 1:
                gameTitle = "\"Badass Game Shit!\"- Captain Robert Jack Daro";
                break;
            case 2:
                gameTitle = "\"Truly the Greatest\"- Captain Robert Jack Daro";
                break;
            case 3:
                gameTitle = "Top Down is Best";
                break;
            case 4:
                gameTitle = "\"For Frederick!\" -Battlecry from early 21st century";
                break;
            case 5:
                gameTitle = "\"Lovers in a Dangerous Spacetime\"/\"Guns of Icarus\"/\"FTL: Faster Than Light\"";
                break;
            case 6:
                gameTitle = "\"Istrolid\"/\"Captain Forever\"/\"Cell Lab: Evolution Sandbox\"";
                break;
            default: // case 0, actually
                gameTitle = "Game";
                break;
            // <- this funky formatting is half the reason I avoid switches- how awful is that?
        }


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
