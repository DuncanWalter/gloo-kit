package GlooKit.GlooAPI.DrawingObjects;

import GlooKit.GlooAPI.GlooBatch;
import GlooKit.GlooAPI.Vertex;
import GlooKit.GlooFramework.GlooApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class DrawingObject {

    private final GlooBatch batch;
    private final List<Vertex> vertices;
    private final List<Short> indices;

    public DrawingObject(GlooApplication app, int batchHandle){
        this.batch = app.getCore().getBatch(batchHandle);
        vertices = new ArrayList<>();
        indices = new ArrayList<>();
    }

    public void apply(Consumer<Vertex> function){
        for(Vertex v : vertices){
            // TODO SUPER UNSAFE! IF THIS EVER BREAKS, ADD AN IF CHECK- THIS IS HERE TO TEST PROVABILITY
            function.accept(v);
        }
    }

    public void apply(int index, Consumer<Vertex> function){
        function.accept(vertex(index));

    }

    public List<Vertex> vertices(){
        return vertices;

    }
    public Vertex vertex(int index){
        return vertices.get(index);

    }
    public List<Short> indices(){
        return indices;

    }
    public Vertex newVertex(){
        return batch.createVertex();

    }

    public void draw(){
        batch.add(this);

    }

//    public void exhumeVertex(double X, double Y, double Z, Vertex V, GlooSocket context){
//        context.exhumeVertex(X, Y, Z, V);
//    }
//    public void shift(float X, float Y, float Z, GlooSocket context){
//        for(VertexType v : vertices){
//            context.shiftVertex(X, Y, Z, v);
//        }
//    }
//    public void scale(float X, float Y, float Z, GlooSocket context){
//        for(VertexType v : vertices){
//            context.scaleVertex(X, Y, Z, v);
//        }
//    }
//    public void scale(float X, float Y, float Z, float aboutX, float aboutY, float aboutZ, GlooSocket context){
//        for(VertexType v : vertices){
//            context.shiftVertex(-aboutX, -aboutY, -aboutZ, v);
//            context.scaleVertex(X, Y, Z, v);
//            context.shiftVertex(+aboutX, +aboutY, +aboutZ, v);
//        }
//    }
//    public void rotate(float X, float Y, float Z, float A){
//        // TODO (also a rotate about...)
//    }


    // Scale isn't useful if we use pre-defined x,y,z pointSize
//    protected void setModelScale(float scaleX, float scaleY, float scaleZ) {
//        modelScale = new Vector3f(scaleX, scaleY, scaleZ);
////        calculateModelMatrix(); // finally, calculate the model matrix after the change
//    }
//    protected void setModelAngle(float x, float y, float z, float s) {
//        /* okay, so now to explain how a quaternion works:
//        *
//        * A quaternion consists of 4 numbers, but is essentially a 3D vector + a "spin" number
//        * <x,y,z,s>
//        *
//        *       +y
//        *      |    -z
//        *      |   /      . Vector (v)
//        *      |  /    .
//        *      | /  .
//        *      |/____________ +x
//        *       .
//        *        .
//        *         . Spin direction (s)
//        *
//        * x,y,z define the axis of rotation as a vector
//        * s is φ, an angle (between 0 and 2pi, please)
//        */
//        modelAngle = new Vector4f(x,y,z,s);
////        calculateModelMatrix(); // finally, calculate the model matrix after the change
//    }

//    protected void setModelPos(double X, double Y, double Z){
////        modelPos = new Vector3f((float)X, (float)Y, (float)Z);
////        calculateModelMatrix();
//    }
//    protected void setModelPos(double X, double Y){
//        setModelPos((float)X, (float)Y, Z);
////        calculateModelMatrix();
//    }

//    protected void calculateModelMatrix() {
//        // this is the complicated bit...
//        /* So, given the the model scale, model angle, and model pos, we can figure out the entire matrix that defines transformations of this object itself
//        *
//        * These must be done in a specific order: if not, then things go wrong.
//        * First: scale,
//        * next: angle,
//        * last: position
//        * (Think about this: if we did position before scale, then the position vector would get scaled)
//        *
//        * Okay, so that's not too terrible, but we need to use this information to get a matrix...
//        *
//        * Scale is easy:
//        *
//        * We simply take the identity 4x4 matrix and scale x, y, z (Sx, Sy, Sz)
//        *
//        * S =
//        *   Sx  0   0   0
//        *   0   Sy  0   0
//        *   0   0   Sz  0
//        *   0   0   0   1
//        *
//        *
//        * Position isn't that bad either:
//        *
//        * We take the identity 4x4 matrix and use the last row to translate x, y, z (Tx, Ty, Tz)
//        * (This is why we don't use a 3x3 matrix)
//        *
//        * T =
//        *   1   0   0   Tx
//        *   0   1   0   Ty
//        *   0   0   1   Tz
//        *   0   0   0   1
//        *
//        *
//        * Rotation is the difficult one: We are using a quaternion, so, looking at quaternion math, we can see that,
//        * for the <x,y,z,φ> definition of a quaternion, q, the corresponding 4x4 matrix, R is: (Dutifully copied from wikipedia)
//        *
//        * R =
//        *   cos(φ) + (x^2)*(1-cos(φ))      x*y*(1-cos(φ)) - z*sin(φ)        x*z*(1-cos(φ)) + y*sin(φ)       0
//        *   x*y*(1-cos(φ)) + z*sin(φ)      cos(φ) + (y^2)*(1-cos(φ))        y*z*(1-cos(φ)) - x*sin(φ)       0
//        *   x*z*(1-cos(φ)) - y*sin(φ)      y*z*(1-cos(φ)) + x*sin(φ)        cos(φ) + (z^2)*(1-cos(φ))       0
//        *               0                               0                               0                   1
//        *
//        * Hopefully, the patterns are obvious (and it's intelligible)
//        *
//        * Thus, given the matrices S, T, and R, the model matrix is:
//        *
//        * T*R*S (Don't ask me to multiply that out symbolically)
//        * */
//
//        // Figure out the scale matrix
//        Matrix4f S = new Matrix4f();
//        Matrix4f.setIdentity(S); // this is redundant, but I'm going to play it safe
//        S.m00 = modelScale.x;
//        S.m11 = modelScale.y;
//        S.m22 = modelScale.z;
//        // And that leaves S.m33 to be 1.0
//
//        // Figure out the transformation matrix
//        Matrix4f T = new Matrix4f();
//        Matrix4f.setIdentity(T); // again, redundant, but I'm doing it to be sure
//        T.m30 = modelPos.x;
//        T.m31 = modelPos.y;
//        T.m32 = modelPos.z;
//        // And the diagonal is all 1.0
//
//        // Figure out the rotation matrix
//        Matrix4f R = new Matrix4f();
//        Matrix4f.setIdentity(R); // This is to make things faster, although we still have to set 9 of the 16 manually
//        R.m00 = (float) (Math.cos(modelAngle.w) + modelAngle.x * modelAngle.x * ( 1.0 - Math.cos(modelAngle.w)));
//        R.m10 = (float) (modelAngle.x * modelAngle.y * (1.0 - Math.cos(modelAngle.w)) - modelAngle.z * Math.sin(modelAngle.w));
//        R.m21 = (float) (modelAngle.x * modelAngle.z * (1.0 - Math.cos(modelAngle.w)) + modelAngle.y * Math.sin(modelAngle.w));
//        R.m01 = (float) (modelAngle.x * modelAngle.y * (1.0 - Math.cos(modelAngle.w)) + modelAngle.z * Math.sin(modelAngle.w));
//        R.m11 = (float) (Math.cos(modelAngle.w) + modelAngle.y * modelAngle.y * ( 1.0 - Math.cos(modelAngle.w)));
//        R.m21 = (float) (modelAngle.y * modelAngle.z * (1.0 - Math.cos(modelAngle.w)) - modelAngle.x * Math.sin(modelAngle.w));
//        R.m02 = (float) (modelAngle.x * modelAngle.z * (1.0 - Math.cos(modelAngle.w)) - modelAngle.y * Math.sin(modelAngle.w));
//        R.m12 = (float) (modelAngle.y * modelAngle.z * (1.0 - Math.cos(modelAngle.w)) + modelAngle.x * Math.sin(modelAngle.w));
//        R.m22 = (float) (Math.cos(modelAngle.w) + modelAngle.z * modelAngle.z * ( 1.0 - Math.cos(modelAngle.w)));
//        // TODO maybe the above could be done more efficiently? I didn't bother because I wanted to ensure the math was correct, first
//        // TODO SLAM: should be fine- it seems to be rare enough to not be an issue
//
//
//        // Finally, we can calculate the modelMatrix // TODO shore up to reduce any potentially needless matrices...
//        Matrix4f RtimesS = new Matrix4f(); // intermediate step required because of the Matrix4f library
//        Matrix4f.mul(R, S, RtimesS); // left, right, destination
//        Matrix4f.mul(T, RtimesS, modelMatrix);
//
//    }
    public String toString(){
        return vertices.toString();

    }
}
