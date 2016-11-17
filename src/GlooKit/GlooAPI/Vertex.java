package GlooKit.GlooAPI;


/**
 * The abstract Vertex class represents a vertex type of object that can be implemented.
 *
 * A Vertex, at its core, is merely an array of floats
 *
 * see "Game.Batches.UserVertex"
 * for an example of implementation
 *
 * Author: Eli Jergensen
 * Documenter: Eli Jergensen
 * */

public abstract class Vertex {

    private final float[] attributes;

    public static final String[]  parameterNames   = null;
    public static final Integer[] parameterLengths = null;
    // standard coordinates
    public Integer X(){return null;}
    public Integer Y(){return null;}
    public Integer Z(){return null;}
    // color attributes
    public Integer R(){return null;}
    public Integer G(){return null;}
    public Integer B(){return null;}
    public Integer A(){return null;}
    // texture coordinates
    public Integer S(){return null;}
    public Integer T(){return null;}
    // bill-boarding texture coordinates for particles
    public Integer U(){return null;}
    public Integer V(){return null;}
    // bill-boarding coordinates for particles
    public Integer P(){return null;}
    public Integer Q(){return null;}

    public abstract int size();
    public abstract String[] getParameterNames();
    public abstract int[]  getParameterLengths();

    public float[] attributes() {
        return attributes;

    }

    /** Basic Constructor*/
    public Vertex(){
        attributes = new float[this.size()];

    }

    /** Default toString operator for a vertex that prints out the attributes as an n-tuple*/
    public String toString() {

        String string = "<";

        // for each attribute but the last, round to 3 decimals
        for (int i = 0; i < attributes.length - 1; i++) {
            string += ((int) attributes[i] * 100)/100 + ", ";
        }

        string += ((int) attributes[attributes.length - 1] * 100)/100 + ">";

        return string;

    }

    public Vertex set(Integer attribute, float value){
        if(attribute != null){
            attributes[attribute] = value;
        }
        return this;
    }

    public float get(int attribute){
        return attributes[attribute];

    }

}
