package GlooKit.GlooAPI;

import GlooKit.GlooFramework.GlooApplication;
import GlooKit.Utils.Vector;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * A TextureAtlas is essentially a spritesheet. It is a collection of textures that are packed together
 * in one megalithic texture. Textures can be added to the TextureAtlas.
 *
 * When the TextureAtlas is bound (sent to the GPU), each of the textures is packed into the TextureAtlas in a
 * fairly efficient, yet very fast method. Each texture is then told enough information for the texture to figure out
 * its own texture coordinates (Upper left and bottom right corners).
 *
 * A TextureAtlas is usually owned by a Batch (and can be shared by Batches), although FontFamilies also interact fairly directly with TextureAtlases
 * @see GlooBatch
 * @see Texture
 * @see GlooFontFamily
 *
 * The TextureAtlas contains a Map of filepaths (Strings) to texture handles (int),
 * A List of textures
 * And is a texture itself
 *
 * Authors: Duncan Walter and Eli Jergensen
 * Documenter: Eli Jergensen
 * */
public class TextureAtlas {

    private static int ATLAS_TEXTURE = -12341445; // The ID of the full texture of the atlas itself (given a really low number so no actual textures might receive it)

    /* Structures for storing textures */
    private Map<String, Integer> handles;
    private List<Texture> textures;
    private Texture atlasTexture;

    private boolean isBound = false;
    private int textureUnit;

    private int atlasW;
    private int atlasH;
    private int handle;

    private int batchHandle;
    private GlooApplication app;

    /**
     * Overloader for constructing a TextureAtlas with the default textureUnit (GL13.GL_TEXTURE0)
     *
     * @param app A GlooApplication that is the true parent of this atlas (as batches can change)
     * */
    public TextureAtlas(GlooApplication app){
        this(app, GL13.GL_TEXTURE0);

    }

    /**
     * Constructor that constructs the "empty" TextureAtlas
     *
     * Includes the "null" texture, which is a 10x10 pixel white image
     *
     * @param app A GlooApplication that is the true parent of this atlas (as batches can change)
     * @param textureUnit One of the dedicated GL ints corresponding to a specific texture
     *                    GL13.GL_TEXTURE0
     *                    up to
     *                    GL13.GL_TEXTURE31
     *       WARNING: Graphics Cards only guarantee the first two textures, but many may support up to all 32
     * */
    public TextureAtlas(GlooApplication app, int textureUnit) {
        this.textureUnit = textureUnit;
        this.handles = new HashMap<>();
        this.textures = new ArrayList<>();
        this.handles.put("null", 0);
        this.textures.add(new Texture(app, 10, 10));
        atlasTexture = new Texture(app, 10, 10);
        atlasTexture.useAtlas();
        this.app = app;
    }

    /**
     * @return an integer corresponding to the GL Texture handle of the TextureAtlas itself
     * */
    public int handle(){
        return handle;

    }

    /**
     * This is not the general purpose method for adding a Texture to the TextureAtlas.
     * The general purpose method is
     * @see TextureAtlas#addTexture(String)
     * This method is used primarily by GlooFontFamily to load in individual GlooCharacters
     * @see GlooFontFamily
     * @see GlooCharacter
     *
     * Adds a texture to the TextureAtlas by taking the ByteBuffer, along with the width and height of the buffer
     * @param buffer a ByteBuffer that is the (already filled) ByteBuffer containing the pixels (RGBA)
     * @param width an integer corresponding to the number of pixels in a row of the texture
     * @param height an integer corresponding to the number of pixels in a column of the texture
     *
     * @return an integer corresponding to the handle of this Texture in the TextureAtlas
     * Handles can be used in the getTexture method to retrieve the Texture from the TextureAtlas
     * */
    public synchronized int addTexture(ByteBuffer buffer, int width, int height) {

        /* Set isBound to false if any state changes */
        isBound = false;

        Texture texture = new Texture(app, textures.size(), buffer, width, height); // make a new texture from a buffer
        textures.add(texture); // addBatch it to the arraylist
        //handles.put(key, textures.pointSize() - 1); // addBatch the filePath to the HashMap
        return textures.size() - 1;
    }

    /**
     * This is the general purpose method for adding a Texture to the TextureAtlas.
     *
     * Adds a texture (or directory of textures) to the TextureAtlas by taking a String for the filePath
     *  filePath a String that is either a file path or a directory
     *
     * if filePath is a file path,
     * adds the file to the TextureAtlas only if it is a .png file
     *
     * if filePath is instead a directory,
     * recursively adds all files in all subdirectories to the TextureAtlas only if they are .png files
     *
     * @return an integer corresponding to the handle of this Texture in the TextureAtlas or -1
     * Returns the handle if filePath is a file path
     * Returns -1 if filePath is instead a directory
     * Handles can be used in the getTexture method to retrieve the Texture from the TextureAtlas
     * */
    protected int addTexture(String filePath){

        if (Files.isDirectory(Paths.get(filePath))) {
            /* This is a directory of files */

            try {
                Stream<Path> paths = Files.walk(Paths.get(filePath)); // get the full list of all filePaths in this directory (or subdirectories)
                paths.forEach( (file) -> {
                    if (Files.isRegularFile(file)) { // make sure the file is regular
                        String filePathString = file.toString();
                        if(filePathString.substring(filePathString.length() - 4).equals(".png")) { // check to see the file is a .png
                            addTexture(filePathString); // recursively call this same function to handle the individual file
                        }
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
                System.exit(400);
            }

            return -1; // since it is a directory of files we just added, we can't return a single useful number, so we return a -1 again

        } else {
            /* This is not a directory of files, but a file itself */

            // The Math.max() call allows this to work on both Windows and Unix filesystems
            String key = filePath.substring(Math.max(filePath.lastIndexOf("\\") + 1, filePath.lastIndexOf("/") + 1), filePath.lastIndexOf("."));

            /* Check to see if the filePath has already been added */
            if (handles.get(key) != null) {
                return handles.get(key); // if its already been added, just return the handle previously given
            }

            /* Set isBound to false if any state changes */
            isBound = false;

            Texture texture = new Texture(app, textures.size(), filePath); // make a new texture
            textures.add(texture); // addBatch it to the arraylist
            handles.put(key, textures.size() - 1); // addBatch the filePath to the HashMap
            return textures.size() - 1;
        }
    }

    /**
     * Gets the handle of a texture
     * @param texture a String that corresponds to the name of a texture (fileName minus the .png)
     *                e.g. /.../.../.../Blue.png corresponds to "Blue"
     * @return an integer corresponding to the handle of this Texture in the TextureAtlas
     * Handles can be used in the getTexture method to retrieve the Texture from the TextureAtlas
     * */
    public int getHandle(String texture){
        Integer response = handles.get(texture);
        if(response == null){
            if(texture.equals("atlas")){
                return ATLAS_TEXTURE;
            } else {
                new Exception("texture file name did not match that of any found").printStackTrace();
                System.out.println(handles);
                return ATLAS_TEXTURE;
            }
        } else {
            return response;
        }
    }

    /**
     * Gets a Texture in the TextureAtlas given the Texture's handle in the TextureAtlas
     *
     * In the special case where the handle is ATLAS_TEXTURE, it returns the Texture corresponding to the entire TextureAtlas
     *
     * @param handle an integer corresponding to handle of a Texture
     *               (this is returned by either the addTexture or getHandle methods)
     * @return a Texture
     * */
    public synchronized Texture getTexture(int handle){
        return handle == ATLAS_TEXTURE ? atlasTexture : textures.get(handle);

    }

    /**
     * Binds the TextureAtlas to the GPU, thereby creating a concrete implementation
     * This call can be rather intensive (up to half a frame) because it will pack the entire TextureAtlas
     *
     * @return an integer corresponding to the GPU's handle for the texture
     * To use this texture, call the GL11.glBindTexture(int target, int texture) method
     * */
    public int bindAtlas(){
        if(!isBound){
            isBound = true; // set the boolean for being bound to true
            destroy();
            handle = constructTextureFromBuffer(packTextures(), atlasW, atlasH, textureUnit);
        }
        return handle;
    }

    /**
     * Packs all given Textures into a single ByteBuffer using a heuristic algorithm focused on speed.
     * Also notifies all given textures of their coordinated within this new byte buffer.
     *
     *
     * @return A ByteBuffer that is the final packed TextureAtlas (which can then be passed on to the GPU)
     * @see TextureAtlas#constructTextureFromBuffer(ByteBuffer, int, int, int)
     * */
    private ByteBuffer packTextures() {

        long time = System.nanoTime();

        int atlasA = 0; // atlas area
        atlasW = 0; // atlas width
        atlasH = 0;

        List<Texture> elements = new ArrayList<>();

        for (Texture texture: textures) { // loop through all of the textures...
            atlasA += texture.height() * texture.width(); // ...sum the areas
            if(texture.width() > atlasW){
                atlasW = texture.width(); // ...keep track of the largest width of a texture thus far
            }
            elements.add(texture); // ...and copy the textures over to a new list
        }

        atlasW = Math.max(atlasW, (int)Math.sqrt(atlasA * 1.11)); // multiply by 1.11 to help get a squarer end result

        Collections.sort(elements); // sort the arraylist (see Texture#compareTo)

        Vector offset = new Vector(0, 0, 0);
        /* ----------------------------- Assign Coordinates to Textures ----------------------------------------------*/
        while(elements.size() > 0){ // loop through all of the textures in elements

            atlasH += elements.get(0).height(); // the first element of a row sets the height of the row

            /* Assign textures along a row until nothing fits */
            for(int i = 0; i < elements.size(); i++){
                ////////////////////////////
                Texture t = elements.get(i);
                ////////////////////////////
                if(offset.x() + t.width() <= atlasW){ // Check to see if this texture's width fits in the row
                    // this texture may be added to the atlas here
                    t.useOffset(new Vector(offset.x(), offset.y(), 0)); // let the texture know its offsets in the atlas
                    elements.remove(i);
                    i -= 1;
                    ///////////////////
                    // DIRTY PACKING //
                    ///////////////////
                    /* Dirty packing refers to the process of placing a smaller element on top of a larger one in a row
                    *
                    * Example:
                    *
                    * ████████████████ ████████████ ████████████
                    * ████████████████ ████████████ ████████████
                    * ████████████████ ████████████ XXXXXX
                    * ████████████████               XXXXXX
                    *
                    * The texture marked by the x's is dirty packed
                    * */
                    int grime = -1;
                    for(int j = elements.size() - 1; j >= 0; j--){
                        if(elements.get(j).height() <= atlasH - offset.y() - t.height() && elements.get(j).width() <= t.width()){
                            grime = j;
                        }
                    }
                    if(grime != -1){
                        elements.get(grime).useOffset(new Vector(offset.x(), offset.y() + t.height(), 0));
                        elements.remove(grime);
                    }
                    offset = new Vector(offset.x() + t.width(), offset.y(), 0);
                }
            }

            offset = new Vector(0, atlasH, 0);

        }

        for(Texture t : textures){
            t.useSize(new Vector(atlasW, atlasH, 0)); // loop through all the textures once more and let them know the pointSize of the texture atlas
        }

        /* ------------------------- End Assign Coordinates to Textures ----------------------------------------------*/

        /* ------------------------ Make the Buffer to send off to the GPU -------------------------------------------*/
        ByteBuffer aBuffer = ByteBuffer.allocateDirect(4 * atlasW * atlasH);
        ByteBuffer tBuffer;

        for(Texture t : textures){

            tBuffer = t.getBuffer();

            byte[] rowOfBytes = new byte[4*t.width()];

            for (int i = 0; i < t.height(); i++){

                tBuffer.position(t.getIndex(i)); // set the position of the tBuffer to the first byte of the row
                tBuffer.get(rowOfBytes, 0, 4*t.width()); // copy the entire row of bytes into the bytearray

                aBuffer.position(t.putIndex(i)); // set the position of the aBuffer to the correct spot
                aBuffer.put(rowOfBytes, 0, 4*t.width()); // copy the entire bytearray into aBuffer

            }
        }

        aBuffer.flip();

        /* -------------------- End Make the Buffer to send off to the GPU -------------------------------------------*/
        long packTime = System.nanoTime() - time; // Keep track of the time it took to do this process
        System.out.println("Took " + packTime/1000000 + "ms to pack an atlas at " + 100 * atlasA / atlasW / atlasH + "% efficiency");

        return aBuffer;
    }

    /**
     * Sends the ByteBuffer of the TextureAtlas off to the GPU
     * In the process, the GPU Texture creates its own mipmaps
     * Uses the nearest pixel when drawing the texture larger
     * Uses linear interpolation between mipmaps and linear interpolation between pixels when drawing the texture smaller
     *
     * @param buffer a ByteBuffer of the packed TextureAtlas
     *               @see TextureAtlas#packTextures()
     * @param width the number of pixels in a row of the ByteBuffer
     * @param height the number of pixels in a column of the ByteBuffer
     * @param textureUnit One of the dedicated GL ints corresponding to a specific texture
     *                    GL13.GL_TEXTURE0
     *                    up to
     *                    GL13.GL_TEXTURE31
     *       WARNING: Graphics Cards only guarantee the first two textures, but many may support up to all 32
     * */
    private int constructTextureFromBuffer(ByteBuffer buffer, int width, int height, int textureUnit) {
        // Create a new texture object in memory and bind it
        int textureID = GL11.glGenTextures();
        GL13.glActiveTexture(textureUnit);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

        // All RGB- bytes are aligned to each other and each component is 1 byte
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        // Upload the texture data and create mip maps for scaling
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        // Set the ST coordinate system
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        // Define what to do when the texture has to be scaled
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);

        return textureID; // return the textureID
    }

    /**
     * Deletes the existing GPU texture by calling the GL11.glDeleteTextures(int texture) method
     * This does not destroy the TextureAtlas, but merely deallocates its memory in the GPU
     * */
    public void destroy() {
        GL11.glDeleteTextures(handle); // tell the GPU to delete the texture of the textureAtlas
    }


}

