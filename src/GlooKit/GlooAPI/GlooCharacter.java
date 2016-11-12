package GlooKit.GlooAPI;


import GlooKit.Utils.Vector;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public final class GlooCharacter {

    private Texture texture;
    private Vector offset;
    private float advance;
    public final float pointSize;
    private Vector size;

    //private float baselineSpace;
//    private float baselineHeight;

    public GlooCharacter (FontRenderContext fontRenderContext, Font font, GlooFontFamily fontFamily, int character, boolean antiAlias) {

        long currentTime = System.nanoTime();

        char[] chars = new char[]{(char) character}; // this must be converted from an int to a char[] for getting the glyphVector
//        String charString = new String(chars);


        LineMetrics lineMetrics = font.getLineMetrics(new String(chars), fontRenderContext);
        GlyphVector glyphVector = font.createGlyphVector(fontRenderContext, chars); // create the glyphVector using ch and the fontRenderContext
//        GlyphMetrics glyphMetrics = glyphVector.getGlyphMetrics(0); // create the glyphMetrics of the 1st (only) glyph in the glyphVector


        double xOffset = 0;
        double yOffset = 0;
        double w = 0;
        double h = 0;

        // There can be multiple glyphs in the glyphVector, so we need to loop over all of them to get the full bounds of the character
        for (int i = 0; i < glyphVector.getNumGlyphs(); i++) {
            Rectangle bounds = glyphVector.getGlyphOutline(i).getBounds();
            xOffset = Math.min(bounds.getX(), xOffset);
            yOffset = Math.min(bounds.getY(), yOffset);
            w = (bounds.getX() + bounds.getWidth() > xOffset + w) ? bounds.getX() + bounds.getWidth() - xOffset : w;
            h = (bounds.getY() + bounds.getHeight() > yOffset + h) ? bounds.getY() + bounds.getHeight() - yOffset : h;
        }

//            System.out.println((char)ch);
//            System.out.println("x: " + xOffset + " y: " + yOffset);
//            System.out.println("w: " + w + " h: " + h);
//            System.out.println();

//        advance = glyphMetrics.getAdvance(); // get the advance of the glyph using the glyphMetrics
//        int startX = (int)xOffset; //Math.max(0, (int)(xOffset));
        int startY = (int) (yOffset + h);
        offset = new Vector((float) xOffset, (float) (-h - yOffset), 0);

//        bearing = new Vector(glyphMetrics.getLSB(), lineMetrics.getDescent() , 0); // get the bearing of the glyph using the glyphMetrics
//        Rectangle pixelBounds = glyphVector.getPixelBounds(fontRenderContext, 0,0); // get the pixel bounds of the glyphVector
        int width = (int) w; // - Math.min(0, (int) xOffset); // save these as local variables for more frequent use
        int height = (int) h;
        size = new Vector(width, height, 0);
        advance = width;
        pointSize = fontFamily.pointSize;

        fontFamily.setLineHeight(lineMetrics.getHeight());
        fontFamily.setCharHeight(lineMetrics.getAscent());
        fontFamily.setBaseHeight(lineMetrics.getDescent() - lineMetrics.getLeading());

//        System.out.println(lineMetrics.getHeight());
//        System.out.println(lineMetrics.getDescent());
//        System.out.println(lineMetrics.getAscent());
//        System.out.println(lineMetrics.getLeading());

        ByteBuffer buffer;

        if (width <= 0 || height <= 0) {
            // if the width or height is (less than or)equal to 0, then we need to construct a nearly empty buffer directly

            buffer = BufferUtils.createByteBuffer(4); //4 for RGBA, 3 for RGB

            for (int i = 0; i < 4; i++) {
                buffer.put((byte) 0);
            }

            buffer.flip(); //FOR THE LOVE OF GOD DO NOT FORGET THIS


        } else {

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR); // create a buffered image of the right pointSize
            Graphics2D graphics2D = image.createGraphics(); // get the graphics of the image

            if (antiAlias) { // enable antiAliasing if it is set above
                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }

            graphics2D.setFont(font); // set the plainFont of the graphics2D

            graphics2D.drawGlyphVector(glyphVector, -(int) xOffset, height - startY); // draw the glyph vector using the graphics2D


            int[] pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);

            buffer = BufferUtils.createByteBuffer(width * height * 4); //4 for RGBA, 3 for RGB

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = pixels[y * width + x];
                    buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red   component
                    buffer.put((byte) ((pixel >>  8) & 0xFF)); // Green component
                    buffer.put((byte) ((pixel >>  0) & 0xFF)); // Blue  component
                    buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component
                }
            }

            buffer.flip(); // don't forget to flip the buffer

        }

        // Now that we have a buffer, we can send it off to the TextureAtlas
        int textureID = fontFamily.textureAtlas.addTexture(buffer, width, height);
        texture = fontFamily.textureAtlas.getTexture(textureID);
    }
    // TODO none of these should be pointSize specific... labels should just need to know their font
    public float advance(float pointSize){
        return advance * pointSize / this.pointSize;

    }
    public float w(float pointSize){
        return this.size.x() * pointSize / this.pointSize;

    }
    public float h(float pointSize){
        return this.size.y() * pointSize / this.pointSize;

    }
    public float x(float pointSize){
        return this.offset.x() * pointSize / this.pointSize;

    }
    public float y(float pointSize){
        return this.offset.y() * pointSize / this.pointSize;

    }
    public float s(){
        return texture.S();

    }
    public float t(){
        return texture.T();

    }
    public float u(){
        return texture.U();

    }
    public float v(){
        return texture.V();

    }
}
