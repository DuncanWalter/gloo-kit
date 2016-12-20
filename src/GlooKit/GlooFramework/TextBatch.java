package GlooKit.GlooFramework;

import GlooKit.GlooAPI.GlooBatch;
import GlooKit.GlooAPI.GlooFontFamily;
import GlooKit.Utils.Matrix;
import org.lwjgl.opengl.*;

import java.awt.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static GlooKit.GlooAPI.GlooFontFamily.STANDARD_TEXT_SET;

public class TextBatch extends GlooBatch {

    private Map<String, Integer> handles;
    private List<GlooFontFamily> fontFamilies;

    private int projMatrixLocation;

    // TODO target for simplification
    public TextBatch(GlooApplication app){
        super(app, null);

        fontFamilies = new ArrayList<>();
        handles = new HashMap<>();

        describeShaders("src/GlooKit/GlooShaders/", "quadVertex.glsl", "quadFragment.glsl", null);
//        describeVertices(new int[] {3, 4, 2}, "in_Position", "in_Color", "in_TextureCoord");
        projMatrixLocation = describeUniform("projectionMatrix");

    }

    public DefaultVertex createVertex(){
        return new DefaultVertex();

    }

    public void render(){
//        GL11.glEnable(GL_MULTISAMPLE);
//        // enable depth testing
        // enable depth testing
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        // tell the shader to enable blending
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        // send the projection matrix
        assignUniform(FLOAT_MAT4x4, projMatrixLocation, app().getPanel().toFloatBuffer());

        super.render(GL11.GL_TRIANGLES);
    }
    public int addFont(String filePath) {
        return addFont(filePath, STANDARD_TEXT_SET, 72);

    }
    public int addFont(String filePath, float size) {
        return addFont(filePath, STANDARD_TEXT_SET, size);

    }
    public int addFont(String filePath, String set) {
        return addFont(filePath, set, 72);

    }
    public int addFont(String filePath, String set, float size){

        if (Files.isDirectory(Paths.get(filePath))) {
            /* This is a directory of files */

            try {
                Stream<Path> paths = Files.walk(Paths.get(filePath)); // get the full list of all filePaths in this directory (or subdirectories)
                paths.forEach( (file) -> {
                    if (Files.isRegularFile(file)) { // make sure the file is regular
                        String filePathString = file.toString();
                        if(filePathString.substring(filePathString.length() - 4).equals(".ttf")) { // check to see if the file is a .ttf
                            addFont(filePathString, set, size); // recursively call this function to add each font
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(400);
            }

            return -1; // since it is a directory of files we just added, we can't return a single useful number, so we return a -1 again

        } else {
            /* This is not a directory of files, but a file itself */

            // Figure out the name of the font
            try {
                InputStream in = new FileInputStream(filePath);
                Font awtFont = Font.createFont(Font.TRUETYPE_FONT, in);

                String key = awtFont.getFamily();
                int handle;

                /* Check to see if a font of this fontFamily has already been added */
                if (handles.get(key) != null) {
                    // if one already has been added, get that font family and add a new font to it
                    handle = handles.get(key);
                    fontFamilies.get(handle).addFont(awtFont, set);
                    return handle;
                } else {
                    // if none exists, make a new font family and add the key to the map
                    fontFamilies.add(new GlooFontFamily(atlas, size * app().pointSize, key, filePath, set));
                    handle = fontFamilies.size()-1;
                    handles.put(key, handle);
                    return handle;
                }

            } catch (Exception e) {
                System.out.println("Unable to create Font to get name of font.");
                e.printStackTrace();
                System.exit(400);

                return 0; // this will never happen
            }

        }

    }

    public GlooFontFamily getFontFamily(String key){
        return getFontFamily(handles.get(key));

    }
    public GlooFontFamily getFontFamily(int handle){
        return fontFamilies.get(handle);

    }
    public List<GlooFontFamily> getFontFamilies() {
        return fontFamilies;

    }

}