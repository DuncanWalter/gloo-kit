package GlooKit.GlooFramework;


import GlooKit.GlooAPI.Vertex;

public class DefaultVertex extends Vertex {

    /* Attributes */
    public static final Integer SIZE = 8;
    // location
    public Integer X(){return 0;}
    public Integer Y(){return 1;}
    // color
    public Integer R(){return 2;}
    public Integer G(){return 3;}
    public Integer B(){return 4;}
    public Integer A(){return 5;}
    // texture
    public Integer S(){return 6;}
    public Integer T(){return 7;}

//    public Vertex create(){
//        return new DefaultVertex();
//
//    }

    public DefaultVertex() {
        super(8);

        float[] attributes = attributes();
        // default to origin
        attributes[X()] = 0;
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
