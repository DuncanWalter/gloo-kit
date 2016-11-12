package GlooKit.GlooAPI;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.io.FileInputStream;
import java.io.InputStream;

public final class GlooFontFamily {

    public static final String STANDARD_TEXT_SET =
            "\t\n\rqwertyuiopasdfghjklzxc" +
            "vbnmQWERTYUIOPASDFGHJKLZXCVB" +
            "NM 1234567890~!@#$%^&*()-=_+" +
            "[]{}\\|:\";'<>,.?/"           ;
    public static final String COMPLETE_TEXT_SET = null;

    public static final int GLOOFONT_PLAIN = 0;
    public static final int GLOOFONT_ITALIC = 1;
    public static final int GLOOFONT_BOLD = 2;
    private static final int NUM_STYLES = 3;

    private boolean hasPlain = false;
    private boolean hasItalic = false;
    private boolean hasBold = false;

    private GlooCharacter[][] characterMaps;

    TextureAtlas textureAtlas;
    public final float pointSize;
    private String fontName;

    private float lineHeight;
    private float charHeight;
    private float baseHeight;

    /*
    * TODO: Load in an entire font family at once using a name string and a directory
    * */
    public GlooFontFamily(TextureAtlas textureAtlas, float size, String fontName) {
        this.characterMaps = new GlooCharacter[NUM_STYLES][];
        this.textureAtlas = textureAtlas;
        this.pointSize = size;
        this.fontName = fontName;
    }
    public GlooFontFamily(TextureAtlas textureAtlas, float size, String fontName, String filePath) {
        this(textureAtlas, size, fontName, filePath, STANDARD_TEXT_SET);

    }
    public GlooFontFamily(TextureAtlas textureAtlas, float size, String fontName, String filePath, String set) {
        this(textureAtlas, size, fontName);
        addFont(filePath, set);
    }
    public GlooFontFamily(TextureAtlas textureAtlas, float size, String fontName, String[] filePaths, String[] sets) {
        this(textureAtlas, size, fontName);
        if (filePaths.length != sets.length) {
            System.out.println("Number of filePaths and number of character sets not of same length!");
            (new Exception("ERROR: Unable to construct GlooFontFamily")).printStackTrace();
            System.exit(300);
        }
        for (int i =0; i < filePaths.length; i++) {
            addFont(filePaths[i], sets[i]);
        }
    }
    public int addFont(String filePath, String set) {
        return addFont(filePath, set, true);

    }
    public int addFont(String filePath, String set, boolean antiAlias) {
        try {

            InputStream in = new FileInputStream(filePath);
            Font awtFont = Font.createFont(Font.TRUETYPE_FONT, in);

            return addFont(awtFont, set, antiAlias);

        } catch (Exception e) {

            System.out.println("Unable to create font.");
            e.printStackTrace();
            System.exit(400);

            return 0;
        }
    }
    public int addFont(Font font, String set) {
        return addFont(font, set, true);

    }
    public int addFont(Font font, String set, boolean antiAlias) {

        /* First, we have to derive the font to be of the correct pointSize */
        font = font.deriveFont(pointSize * 1.66f); // TODO 1.66 is a hardcoded constant specific to my screensize...

        int type = 0; // this is for speed when putting the characters in the arraylist

        /* Figure out the actual style of the font. Cause apparently the font.getStyle call doesn't work */
        String name = font.getFontName(); // This will usually be equal to the family name + " Bold" or " Italic"
        int familyNameLength = font.getFamily().length();
        String styleString = (name.substring(familyNameLength)).toLowerCase(); // subtracting off the family name should leave us with "", " Bold" or " Italic"

        if (styleString.contains("bold")) {
            type = GLOOFONT_BOLD;

            if (hasBold) {
                System.out.println("WARNING: FontFamily already has font of type BOLD");
                System.out.println("Keeping previous font and not adding new font");
                return type;
            }

            hasBold = true;
        } else if (styleString.contains("italic")) {
            type = GLOOFONT_ITALIC;

            if (hasItalic) {
                System.out.println("WARNING: FontFamily already has font of type ITALIC");
                System.out.println("Keeping previous font and not adding new font");
                return type;
            }

            hasItalic = true;
        } else {
            type = GLOOFONT_PLAIN;

            if (hasPlain) {
                System.out.println("WARNING: FontFamily already has font of type PLAIN");
                System.out.println("Keeping previous font and not adding new font");
                return type;
            }

            hasPlain = true;
        }



        // First create a FontRenderContext
        FontRenderContext fontRenderContext = new FontRenderContext(null, antiAlias, false);

//        System.out.println("type " + type);
//        System.out.println("maps " + characterMaps);

        if(set == null){
            characterMaps[type] = new GlooCharacter[font.getNumGlyphs()];
            for(int i = 0; i < font.getNumGlyphs(); i++){
                GlooCharacter character = new GlooCharacter(fontRenderContext, font, this, i, true); // create the character
                characterMaps[type][i] = character;
            }
        } else {
            char[] chars = set.toCharArray();
            int max = 0;
            for(char ch : chars){
                max = ((int)ch > max) ? (int)ch + 1 : max;
            }
            characterMaps[type] = new GlooCharacter[max];
            for(char ch : chars){
                GlooCharacter character = new GlooCharacter(fontRenderContext, font, this, (int)ch, true); // create the character
                characterMaps[type][(int)ch] = character;
            }
        }



        return type;
    }

    final void setCharHeight(float height){
        this.charHeight = height;

    }
    final void setBaseHeight(float height){
        this.baseHeight = height;

    }
    final void setLineHeight(float height){
        this.lineHeight = height;

    }
    public float getCharHeight(float pointSize){
        return charHeight * pointSize / this.pointSize;

    }
    public float getBaseHeight(float pointSize){
        return baseHeight * pointSize / this.pointSize;

    }
    public float getLineHeight(float pointSize){
        return lineHeight * pointSize / this.pointSize;

    }
    public String getFontName() {
        return fontName;

    }

    public String toString() {
        String result = fontName + ": ";
        result += "hasPlain? " + (hasPlain) + ", ";
        result += "hasItalic? " + (hasItalic) + ", ";
        result += "hasBold? " + (hasBold);
        return  result;
    }

    public GlooCharacter getCharacter(int style, char ch){
        return characterMaps[style][(int)ch];

    }

}





