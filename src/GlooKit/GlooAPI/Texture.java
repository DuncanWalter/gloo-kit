package GlooKit.GlooAPI;


import GlooKit.GlooFramework.GlooApplication;
import GlooKit.Utils.Vector;
import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Texture work in tandem with TextureAtlases and can mostly be thought of as a ByteBuffer of pixels
 * Textures also know their width, height, and also possess a couple properties granted to them by their TextureAtlas
 * This includes the coordinates of the upper left and lower right corners of this Texture in the TextureAtlas,
 * as well as the handle of the Texture in the TextureAtlas
 * @see TextureAtlas
 *
 * Textures can be added with a String to the filepath of the .png file they come from
 * Textures can only come from .png files
 *
 * Author: Eli Jergensen and Duncan Walter
 * Documenter: Eli Jergensen
 * */
public final class Texture implements Comparable<Texture> {

    // TODO addBatch access to batch owner, atlas owner, aspect ratio

    // Properties of the Buffer
    private int width;
    private int height;
    private ByteBuffer byteBuffer;
    // Properties granted by the TextureAtlas
    private Vector offset = new Vector(0, 0, 0);
    private Vector ST = new Vector(0, 0, 0);
    private Vector UV = new Vector(0, 0, 0);
    private int W = 0;

    public final int handle;
    public final GlooApplication app;

    private boolean hasAlpha;

    /**
     * Constructs a Texture by loading a .png file from a filepath
     * @param app the GlooApplication of the TextureAtlas of this Texture
     * @param textureHandle the handle of this Texture in the TextureAtlas
     * @param filepath a String of the filepath of the .png file to be loaded
     * */
    public Texture(GlooApplication app, int textureHandle, String filepath) {
        loadPNGTexture(filepath);
        this.handle = textureHandle;
        this.app = app;
    }

    /**
     * Constructs a Texture by accepting a ByteBuffer, width, and height
     * This is used primarily by GlooFontFamilies to load in GlooCharacters
     * @see GlooFontFamily
     * @see GlooCharacter
     *
     * @param app The GlooApplication of the TextureAtlas of this Texture
     * @param textureHandle the handle of this Texture in the TextureAtlas
     * @param buffer a ByteBuffer of the pixels of this Texture
     * @param width an int of the number of pixels in a row of the Texture
     * @param height an int of the number of pixels in a column of the Texture
     * */
    public Texture(GlooApplication app, int textureHandle, ByteBuffer buffer, int width, int height) {
        this.handle = textureHandle;
        this.app = app;

        this.byteBuffer = buffer;
        this.width = width;
        this.height = height;
    }

    /**
     * Constructs a Texture of width and height that is a purely white, full opacity image
     * This is the Default Texture of the GlooCore
     * A new TextureAtlas is initialized with only this texture in it
     *
     * @param app The GlooApplication of the TextureAtlas of this Texture
     * @param width an int of the number of pixels in a row of the Texture
     * @param height an int of the number of pixels in a column of the Texture
     *
     * */
    protected Texture(GlooApplication app, int width, int height) {

        this.app = app;
        this.handle = 0;

        this.width = width;
        this.height = height;

        this.byteBuffer = ByteBuffer.allocateDirect(width * height * 4);

        for(int i = 0; i < width * height * 4; i++){
            byteBuffer.put((byte)255);
        }

        hasAlpha = false;
    }

    /**
     * Load in a ByteBuffer from a filepath using the PNGDecoder library
     * @param filename a String of the filepath of the .png file to be loaded
     * */
    private void loadPNGTexture(String filename) {
        ByteBuffer buffer = null;
        int width = 0;
        int height = 0;

        try {
            // open the PNG file as an InputStream
            InputStream in  = new FileInputStream(filename);
            // Link the PNG decoder to this stream
            PNGDecoder decoder = new PNGDecoder(in);

            // get the width and height of the texture
            width = decoder.getWidth();
            height = decoder.getHeight();

            // Decode the PNG file into a ByteBuffer
            buffer = ByteBuffer.allocateDirect(4 * width * height);
            decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
            buffer.flip(); // NOTE THAT THE BUFFER WAS MOST DEFINITELY FLIPPED!

            in.close();
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-350);
        }

        hasAlpha = false;

        /* Determine whether any of the pixels have an alpha less than 1 */
        for(int i = 0; i < width * height * 4; i++){
            i += 3;
            if(buffer.get(i) != (byte)255){
                hasAlpha = true;
                break;
            }
        }

        // Copy over the fields into the Texture
        this.byteBuffer = buffer;
        this.width = width;
        this.height = height;

    }

    /**
     * Notifies this Texture of the offset that it has in the TextureAtlas
     * (This is essentially the upper left corner of the Texture in the TextureAtlas)
     * This is called by the TextureAtlas while texture packing
     * @see TextureAtlas#packTextures()
     * */
    final void useOffset(Vector offset){
        this.offset = offset;
    }

    /**
     * Notifies this Texture of the pointSize of the final TextureAtlas
     * This is called by the TextureAtlas after finishing texture packing
     * @see TextureAtlas#packTextures()
     *
     * The Texture then figures out its upper left and lower right corners as well as the width of the TextureAtlas
     * */
    final void useSize(Vector size){
        ST = new Vector(offset.x() / size.x(), offset.y() / size.y(), 0);
        UV = new Vector(ST.x() + width / size.x(), ST.y() + height / size.y(), 0);
        W = (int)size.x();
    }

    /**
     * This is a dedicated call that only gets called for the 'atlas' Texture of the TextureAtlas,
     * which has coordinates of the upper left and lower right corners of the TextureAtlas itself
     * */
    final void useAtlas(){
        ST = new Vector(0, 0, 0);
        UV = new Vector(1, 1, 0);
    }

    /** These are dedicated getter functions that read more naturally */
    public float S(){return ST.x();}
    public float T(){return ST.y();}
    public float U(){return UV.x();}
    public float V(){return UV.y();}

    public int width (){return width ;}
    public int height(){return height;}

    public boolean hasAlpha(){
        return hasAlpha;

    }

    /**
     * These methods are used to help construct the giant ByteBuffer of the packed TextureAtlas
     * @see TextureAtlas#constructTextureFromBuffer(ByteBuffer, int, int, int)
     * */
    int getIndex(int y) {return 4 * y * width;}
    int putIndex(int y) {return 4 * (int)(offset.x() + (offset.y() + y) * W);}

    protected ByteBuffer getBuffer(){return byteBuffer;}
    
    /**
     * This comparator is used by the TextureAtlas to sort Textures first by height and then by width
     * This comparator returns values that are backwards from what you think
     *
     * @return +1 if this Texture is "less than" other,
     *          0 if this Texture is "equal to" other,
     *         -1 if this Texture is "greater than" other
     * */
    public int compareTo(Texture other) {

        if (this.height > other.height) {
            return -1; // this is larger (this has a larger height than other)
        } else if (this.height < other.height) {
            return 1; // this is smaller (this has a smaller height than other)
        } else {
            if (this.width > other.width) {
                return -1; // this is larger (this has a larger width than other)
            } else if (this.width < other.width) {
                return 1; // this is smaller (this has a smaller width than other)
            } else {
                return 0; // Both this and other have the same height and width
            }
        }

    }

}
