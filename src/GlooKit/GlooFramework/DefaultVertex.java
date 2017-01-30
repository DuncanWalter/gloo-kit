package GlooKit.GlooFramework;


import GlooKit.GlooAPI.Vertex;

public class DefaultVertex extends Vertex {

    /* Attributes */
    public String[]  getParameterNames(){
        return new String[]{"in_Position", "in_Color", "in_TextureCoord"};

    }
    public int[] getParameterLengths(){
        return new int[]{3, 4, 2};

    }
    public int size(){return 9;}
    // location
    public Integer X(){return 0;}
    public Integer Y(){return 1;}
    public Integer Z(){return 2;}
    // color
    public Integer R(){return 3;}
    public Integer G(){return 4;}
    public Integer B(){return 5;}
    public Integer A(){return 6;}
    // texture
    public Integer S(){return 7;}
    public Integer T(){return 8;}

//    public Vertex create(){
//        return new DefaultVertex();
//
//    }

    public DefaultVertex() {
        super();

        float[] attributes = attributes();
        // default to origin
        attributes[X()] = 0;
        attributes[Y()] = 0;
        attributes[Y()] = 0;
        // default to white (full opacity)
        attributes[R()] = 1;
        attributes[G()] = 1;
        attributes[B()] = 1;
        attributes[A()] = 1;
        // default to origin
        attributes[S()] = 0;
        attributes[T()] = 0;
    }

}
