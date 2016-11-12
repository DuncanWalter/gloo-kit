package GlooKit.GlooAPI.DrawingObjects;

import GlooKit.GlooAPI.Vertex;
import GlooKit.GlooFramework.GlooApplication;

public class Polygon extends DrawingObject {

    /* Polygon's are 2D (convex!) objects that will be drawn on the screen. They are flat in the z-direction.*/
    public Polygon(GlooApplication app, int batchHandle) {
        super(app, batchHandle);

    }
    public void addVertex(){

        Vertex v = newVertex();
//        v.set(v.X(), X);
//        v.set(v.Y(), Y);
//        v.set(v.Z(), Z);
        vertices().add(v); // TODO use XYZ

        if (indices().size() < 3) {
            // if we don't have three indices in the list, then we simply add the index to the list
            indices().add((short) indices().size());
        } else {
            // we have more than three, so we are adding triangles, convexly...
            // each new triangle is formed from the 0 vertex, the last vertex added, and this new vertex
            indices().add((short) 0);
            indices().add((short)(vertices().size() - 2));
            indices().add((short)(vertices().size() - 1));
        }

    }

    public void load(int numVertices){
        for(int i = 0; i < numVertices; i++){
            addVertex();
        }
        close();
    }

//    public void finish() { // TODO will be encompassed by Primitive draws...
//        // check to make sure there are enough vertices
//        if (verticesSize < 3) {
//            // this polygon was built wrong, so CRASH! the entire program with a warning and a stack trace.
//            try {
//                throw new Exception();
//            } catch (Exception e) {
//                System.out.println("Polygon did not have 3 vertices yet and was finished.");
//                e.printStackTrace();
//                System.exit(-1);
//            }
//        }
//        // assuming that didn't just happen...
//        // first, we need to manually add the indicices for the first and last triangles to touch each other

//        indicesSize += 3; // increment indicesSize by 3
//
//        indices = new byte[indicesSize]; // do the same for indices
//        indicesCount = indicesSize; // copy this over... // TODO what a crazy mess I've made
//        verticesCount = indicesSize / 3;
//        for (int i = 0; i < indicesSize; i++) {
//            indices[i] = tempIndices.get(i); // this has to be done this way because we can't cast objects to bytes
//        }
//
//        //setZ(0.5f);
//
//        Gloo.DrawingObjects.add(0, this); // this breaks OOP rules, but I don't care enough. // Add in reverse order
//    }

    public Polygon close(){
        indices().add((short)0);
        indices().add((short)1);
        indices().add((short)(vertices().size() - 1));
        return this;
    }

}
