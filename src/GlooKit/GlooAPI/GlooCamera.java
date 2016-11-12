package GlooKit.GlooAPI;

//import org.lwjgl.opengl.Display;
//import org.lwjgl.util.vector.Matrix4f;
//
//public class GlooCamera {
//    Matrix4f position;      // the camera position matrix
//    Matrix4f rotation;      // the camera rotation matrix
//    Matrix4f projection;    // the camera projection vector
//    Matrix4f adjuster;      // converts from screen coordinates to GlooAPI coordinates
//    Matrix4f scale;         // multi-coordinate scaling
//    Matrix4f catcher;       // catches mutations from the Matrix library
//
//    public Matrix4f composeViewMatrix(){
//        return Matrix4f.mul(scale, Matrix4f.mul(position, rotation, catcher), catcher);
//    }
//    public Matrix4f composeProjMatrix(){
//        return Matrix4f.mul(projection, adjuster, catcher);
//    }
//    public void setPerspectiveMode(double fieldOfVision){
//
//        // Gloo projection matrix
//        projection = new Matrix4f();
//        projection.setZero(); // set the projectionMatrix to the zero matrix
//        float FOV = (float) fieldOfVision; // convert to radians
//        float aspectRatio = (float) Display.getDisplayMode().width() / Display.getDisplayMode().height(); // (WIDTH/HEIGHT)
//        float nearPlane = 0.1f;
//        float farPlane = 1.1f;
//
//        projection.m00 = (float) (1f/(Math.tan(FOV / 2f))) / aspectRatio;
//        projection.m11 = (float) (1f/Math.tan(FOV / 2f));
//        projection.m22 = -(farPlane + nearPlane)/(farPlane - nearPlane);
//        projection.m23 = -1;
//        projection.m32 = -(2f * farPlane * nearPlane)/(farPlane - nearPlane);
//
//    }
//    public void setOrthagonalMode(){
//
//    }
//    public void setPostion(double X, double Y, double Z){
//
//        // Figure out the transformation matrix
//        Matrix4f T = new Matrix4f();
//        Matrix4f.setIdentity(T); // again, redundant, but I'm doing it to be sure
//        T.m30 = (float)X;
//        T.m31 = (float)Y;
//        T.m32 = (float)Z;
//        // And the diagonal is all 1.0
//
//    }
//    public void setRotation(double X, double Y, double Z, double A){
//
//        // quaternion vector rotation
//        double l = Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2) + Math.pow(Z, 2));
//        double x = X / l;
//        double y = Y / l;
//        double z = Z / 1;
//
//        // Figure out the rotation matrix
//        rotation = new Matrix4f();
//        Matrix4f.setIdentity(rotation); // This is to make things faster, although we still have to set 9 of the 16 manually
//        rotation.m00 = (float) (Math.cos(A) + x * x * ( 1.0 - Math.cos(A)));
//        rotation.m10 = (float) (x * y * (1.0 - Math.cos(A)) - z * Math.sin(A));
//        rotation.m21 = (float) (x * z * (1.0 - Math.cos(A)) + y * Math.sin(A));
//        rotation.m01 = (float) (x * y * (1.0 - Math.cos(A)) + z * Math.sin(A));
//        rotation.m11 = (float) (Math.cos(A) + y * y * ( 1.0 - Math.cos(A)));
//        rotation.m21 = (float) (y * z * (1.0 - Math.cos(A)) - x * Math.sin(A));
//        rotation.m02 = (float) (x * z * (1.0 - Math.cos(A)) - y * Math.sin(A));
//        rotation.m12 = (float) (y * z * (1.0 - Math.cos(A)) + x * Math.sin(A));
//        rotation.m22 = (float) (Math.cos(A) + z * z * ( 1.0 - Math.cos(A)));
//
////        // Finally, we can calculate the modelMatrix // TODO shore up to reduce any potentially needless matrices...
////        Matrix4f RtimesS = new Matrix4f(); // intermediate step required because of the Matrix4f library
////        Matrix4f.mul(R, S, RtimesS); // left, right, destination
////        Matrix4f.mul(T, RtimesS, modelMatrix);
//
//    }
//    public void setScale(double X, double Y, double Z){
//
//        // Figure out the scale matrix
//        scale = new Matrix4f();
//        Matrix4f.setIdentity(scale); // this is redundant, but I'm going to start it safe
//        scale.m00 = (float)X;
//        scale.m11 = (float)Y;
//        scale.m22 = (float)Z;
//        // And that leaves S.m33 to be 1.0
//
//    }
//
//}
