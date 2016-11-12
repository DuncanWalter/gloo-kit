package GlooKit.Utils;

import java.io.Serializable;

public class Vector implements Serializable {

    // 3D immutable vectors
    private float x;
    private float y;
    private float z;

    public float x(){return x;}
    public float y(){return y;}
    public float z(){return z;}

//    private Vector(float X, float Y, float Z, float L){
//        // cheap and dirty constructor for optimization... shouldn't be used frequently
//        x = X;
//        y = Y;
//        z = Z;
//    }

    public Vector(float X, float Y, float Z){
        // coordinate transform interpretation of a vector
        x = X;
        y = Y;
        z = Z;
    }

//    public Vector(float A){
//        // constructs a unit vector towards angle A
//        x = (float)Math.cos(A);
//        y = (float)Math.sin(A);
//        z = 0;
//        l = 1;
//    }

//    public Vector(float A, float P){
//        // constructs a unit vector towards angle A and angle P
//        x = (float)(Math.cos(A)*Math.sin(P));
//        y = (float)(Math.sin(A)*Math.sin(P));
//        z = (float)Math.cos(P);
//        l = (float)1;
//    }

//    public Vector(Vector direction){
//        // direction / angle interpretation of a vector
//        if(direction == 0){
//            System.out.println("ERROR: vector provided has ambiguous direction (length = 0)");
//            try{throw (new Exception());}catch(Exception e){e.printStackTrace();}
//        } else {
//            x = direction.x / direction.l * L;
//            y = direction.y / direction.l * L;
//            z = direction.z / direction.l * L;
//        }
////        inits++;
//    }

//    public Vector exhume(float X, float Y, float Z){
//        // coordinate transform interpretation of a vector
//        // CAUTION exhume is a mutating call
//        x = X;
//        y = Y;
//        z = Z;
//        return this;
//    }


//    public Vector exhume(Vector V, float L){
//        // direction / angle interpretation of a vector
//        // CAUTION exhume is a mutating call
//        if(V.l == 0){
//            System.out.println("ERROR: vector provided has ambiguous direction (length = 0)");
//            try{throw (new Exception());}catch(Exception e){e.printStackTrace();}
//        } else {
//            x = V.x / V.l * L;
//            y = V.y / V.l * L;
//            z = V.z / V.l * L;
//            l = L;
//        }
//        return this;
//    }

//    public Vector exhume(float A){
//        // constructs a unit vector towards angle A
//        // CAUTION exhume is a mutating call
//        x = Math.cos(A);
//        y = Math.sin(A);
//        l = 1;
//        return this;
//    }

//    public Vector exhume(Vector V){
//        // clones a vector
//        // CAUTION exhume is a mutating call
//        x = V.x;
//        y = V.y;
//        z = V.z;
//        return this;
//    }

    public Vector scale(float S){
        // scalar multiplier
        return new Vector(x * S, y * S, z * S);
    }

//    public Vector SCALE(float S){
//        // scalar multiplier
//        // CAUTION SCALE is a mutating call
//        x *= S;
//        y *= S;
//        z *= S;
//        return this;
//    }

    public Vector add(Vector V){
        // vector addition
        return new Vector(x + V.x, y + V.y, z + V.z);
    }

//    public Vector ADD(Vector V){
//        // vector addition
//        // CAUTION ADD is a mutating call
//        return exhume(x + V.x, y + V.y, z + V.z);
//    }

    public Vector sub(Vector V){
        // vector subtraction
        return new Vector(this.x - V.x, this.y - V.y, this.z - V.z);
    }

//    public Vector SUB(Vector V){
//        // vector subtraction
//        // CAUTION SUB is a mutating call
//        return exhume(this.x - V.x, this.y - V.y, this.z - V.z);
//    }

    public Vector to(Vector V){
        // vector subtraction inverter
        return new Vector(V.x - this.x, V.y - this.y, V.z - this.z);
    }

//    public Vector TO(Vector V){
//        // vector subtraction inverter
//        // CAUTION TO is a mutating call
//        return exhume(V.x - this.x, V.y - this.y, V.z - this.z);
//    }

    public float dot(Vector V){
        // vector dot product
        return x * V.x + y * V.y + z * V.z;
    }

//    public Vector projectOn(Vector target){
//        // vector projection
//        if(target.l == 0){
//            System.out.println("ERROR: vector provided has ambiguous direction (length = 0)");
//            try{throw (new Exception());}catch(Exception e){e.printStackTrace();}
//            return target;
//        }
//        return new Vector(target.x / target.l * l, target.y / target.l * l, target.z / target.l * l, l);
//    }


//    public Vector projectOff(Vector V){
//        // projects one vector off of another
//        if(V.l == 0){
//            System.out.println("ERROR: vector provided has ambiguous direction (length = 0)");
//            try{throw (new Exception());}catch(Exception e){e.printStackTrace();}
//            return this;
//        }
//        float tl = dot(V) / V.l;
//        if(tl == 0){
//            return this;
//        }
//        float tx = V.x / V.l * tl;
//        float ty = V.y / V.l * tl;
//        float tz = V.z / V.l * tl;
//        return new Vector(x - tx, y - ty, z - tz);
//    }
//
//    public Vector PROJECT_OFF(Vector V){
//        // projects one vector off of another
//        // CAUTION PROJECT_OFF is a mutating call
//        if(V.l == 0){
//            System.out.println("ERROR: vector provided has ambiguous direction (length = 0) ");
//            try{throw (new Exception());}catch(Exception e){e.printStackTrace();}
//            return this;
//        }
//        float tl = dot(V) / V.l;
//        if(tl == 0){
//            return this;
//        }
//        float tx = V.x / V.l * tl;
//        float ty = V.y / V.l * tl;
//        float tz = V.z / V.l * tl;
//        return exhume(x - tx, y - ty, z - tz);
//    }

//    public float angle(){
//        // DEPRECATED: consider implementing a matrix to batch transforms
//        // returns the radian angle (slowest method in class by a wide margin)
//        if(x == 0){
//            if (this.y < 0) {
//                return 3 * Math.PI / 2;
//            } else {
//                return Math.PI / 2;
//            }
//        } else if (x < 0) {
//            // flip to negative side of unit circle
//            return Angle.format(Math.atan(y / x) + Math.PI);
//        } else {
//            // standard method
//            return Angle.format(Math.atan(y / x));
//        }
//    }

    public String toString(){
        return "<" + Math.round(x*10)/10.0 + ", " + Math.round(y*10)/10.0 + ", " + Math.round(z*10)/10.0 + ">";
    }

    private float hypot(float a, float b, float c){
        return (float)Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2) + Math.pow(c, 2));
    }

}
