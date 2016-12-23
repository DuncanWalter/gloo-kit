package Driver;

import GlooKit.GlooFramework.GlooApplication;
import GlooKit.Utils.JSONObject;

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

        JSONObject json = new JSONObject();

        json.add("name", "Fred");
        json.add("age", "13");
        json.add("height", 169);
        json.add("desert", new String[] {"cheesecake", "(gummi-)bears", "I fu[]{}ing h*te y,u"});
        json.add("evil string", "''");
        json.add("eviler string", "\"Quotes motherf*cker\"");
        json.add("\"true evil\"", "\"Do you hate me?\n How 'bout now?\"");
        json.add("bottle", new JSONObject().add("message", "\"Duncan\" said so,\n JSON format!\"").add("type", "wet"));
        json.add("friends", new JSONObject[] {
            new JSONObject().add("name", "Bob").add("job", "engineer"),
            new JSONObject().add("name", "Bill").add("job", "scientist").add("skills", new String[] {"SCIENCE!"}),
            new JSONObject().add("name", "Jebediah").add("job", "pilot").add("friends", new JSONObject[] {
                new JSONObject().add("name", "Mark").add("goal", "ManMode"),
                new JSONObject().add("name", "Stan").add("status", "deceased")
            })
        });

//        System.out.println(json.fetchJSONObject("bottle"));
//        System.out.println();
//        System.out.println(json.fetchString(new String[]{"bottle","message"}));
//        System.out.println();
//        System.out.println(json.fetchJSONObjects("friends")[0]);
//        System.out.println();
//        System.out.println(json.fetchStrings(new String[]{"friends","name"})[0]);
//        System.out.println();
//        System.out.println(json.fetchString(new String[]{"Ragnarock"}));
//
//        System.out.println();
//        System.out.println();

        json.writeToFile("jsontest.json");

        json.setInline(true);
        json.writeToFile("jsoninline.json");
        
        JSONObject incoming = new JSONObject();
        incoming.readFromFile("jsontest.json");
        incoming.writeToFile("jsonwrite.json");

//        System.out.println(incoming.fetchJSONObject("bottle"));
//        System.out.println();
//        System.out.println(incoming.fetchString(new String[]{"bottle","message"}));
//        System.out.println();
//        //System.out.println(incoming.fetchJSONObjects("friends")[0]);
//        System.out.println(incoming.fetchString(new String[]{"eviler string"}));
//        System.out.println();
//        //System.out.println(incoming.fetchStrings(new String[]{"friends","name"})[0]);
//        System.out.println();
//        System.out.println(incoming.fetchString(new String[]{"Ragnarock"}));
//
//        System.out.println();
//        System.out.println();

        JSONObject inlineIncoming = new JSONObject();
        inlineIncoming.readFromFile("jsoninline.json");
        inlineIncoming.writeToFile("jsoninlinewrite.json");

//        System.out.println(incoming.fetchString(new String[]{"eviler string"}));
//        System.out.println();
//        System.out.println();
//        System.out.println(incoming.fetchString(new String[]{"Ragnarock"}));


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
