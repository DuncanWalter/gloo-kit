package GlooKit.GlooAPI;

import GlooKit.GlooAPI.DrawingObjects.DrawingObject;
import GlooKit.GlooFramework.GlooApplication;
import GlooKit.GlooShaders.Shaders;
import GlooKit.Utils.Matrix;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static GlooKit.GlooShaders.Shaders.createShaderProgram;
import static GlooKit.GlooShaders.Shaders.loadFragmentShader;
import static GlooKit.GlooShaders.Shaders.loadVertexShader;

/**
 * GlooBatches are large collections of primitives that will be drawn to the screen.
 *
 * Each GlooBatch contains a Batch (see below) and some additional methods to transfer info to the GPU
 * @see GlooBatch.Batch
 *
 * see "Game.Batches.UserBatch"
 * for an implementation
 * 
 * Author: Duncan Walter
 * Documenter: Eli Jergensen
 * */
public abstract class GlooBatch {

    public static final int FLOAT_VEC1 = 0;
//    public static final int FLOAT = FLOAT_VEC1;
    public static final int FLOAT_VEC2 = 1;
    public static final int FLOAT_VEC3 = 2;
    public static final int FLOAT_VEC4 = 3;
    public static final int INT_VEC1 = 4;
//    public static final int INT = INT_VEC1;
    public static final int INT_VEC2 = 5;
    public static final int INT_VEC3 = 6;
    public static final int INT_VEC4 = 7;
    public static final int UINT_VEC1 = 8;
//    public static final int UINT = UINT_VEC1;
    public static final int UINT_VEC2 = 9;
    public static final int UINT_VEC3 = 10;
    public static final int UINT_VEC4 = 11;
    public static final int FLOAT_MAT2x2 = 12;
//    public static final int FLOAT_MAT2 = FLOAT_MAT2x2;
    public static final int FLOAT_MAT2x3 = 13;
    public static final int FLOAT_MAT2x4 = 14;
    public static final int FLOAT_MAT3x2 = 15;
    public static final int FLOAT_MAT3x3 = 16;
//    public static final int FLOAT_MAT3 = FLOAT_MAT3x3;
    public static final int FLOAT_MAT3x4 = 17;
    public static final int FLOAT_MAT4x2 = 18;
    public static final int FLOAT_MAT4x3 = 19;
    public static final int FLOAT_MAT4x4 = 20;
//    public static final int FLOAT_MAT4 = FLOAT_MAT4x4;

    // the custom collection of drawing objects to be drawn
    private final GlooApplication application;
    private final Batch batch;
//    private int vertexSize;
    protected TextureAtlas atlas;

    private int shaderProgram;
    private int VAOID;
    private int VBOID;
    private int indicesVBOID;
    private int parameterCount;
    private int vertexSize;


    public abstract Vertex createVertex();

    public GlooBatch(GlooApplication app, TextureAtlas atlas){
        batch = new Batch(app.getPool());
        this.atlas = atlas == null ? new TextureAtlas(app) : atlas;
        application = app;
    }

    public Texture getTexture(String file){
        return atlas.getTexture(atlas.getHandle(file));

    }
    public void addTexture(String filePath){
        atlas.addTexture(filePath);

    }
    public void bindTextures(){
        atlas.bindAtlas();

    }
    public int getAtlasHandle(){
        return atlas.handle();

    }
    public void describeShaders(String path, String vertexShader, String fragmentShader, String geometryShader) {

        System.out.println(path + vertexShader);
        System.out.println(path + fragmentShader);
        shaderProgram = createShaderProgram(loadVertexShader(path + vertexShader), loadFragmentShader(path + fragmentShader));

        Vertex v = createVertex();

        if(v.getParameterNames().length != v.getParameterLengths().length){
            new Exception("ERROR: parameterLengths and parameterNames were of different sizes").printStackTrace();
        } else {
            parameterCount = v.getParameterNames().length;
        }

        VAOID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(VAOID);
        // Create and bind the default VAO's VBO
        VBOID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOID);

        int length = 0;
        for(int l : v.getParameterLengths()){
            length += l;
        }
        int index = 0;
        int offset = 0;
        for(String s : v.getParameterNames()){
//            Setup the VBO with the corresponding attribute pointers
//            attribute list #, pointSize of element, type of element, isNormalized?, stride (skipping stuff), run spot
            GL20.glBindAttribLocation(shaderProgram, index, s);
            GL20.glVertexAttribPointer(index, v.getParameterLengths()[index], GL11.GL_FLOAT, false, length * 4, offset * 4);
            offset += v.getParameterLengths()[index];
            index++;
        }
        vertexSize = offset;

        // unbind the VBO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        // deselect the VAO
        GL30.glBindVertexArray(0);
        // Create and bind a VBO for the indices (this isn't attached to the VAO (for some reason))
        indicesVBOID = GL15.glGenBuffers();

    }
    public int describeUniform(String uniform){
        return GL20.glGetUniformLocation(shaderProgram, uniform);

    }
    public void assignUniform(int type, int location, Buffer value){
        value.flip();
        if(value instanceof FloatBuffer){
            Shaders.sendUniformToGPU(type, location, (FloatBuffer)value);
        }
        if(value instanceof IntBuffer){
            Shaders.sendUniformToGPU(type, location, (IntBuffer)value);
        }
    }


    /** 
     * This abstract render call must be implemented by each batch that a user constructs
     * @see GlooKit.GlooFramework.DefaultBatch#render()
     * for an example
     * */
    public abstract void render();
    
    /**
     * This non-abstract method eventually gets called after each batch-specific render method.
     * batch.render() calls the render method in batch, which is threadable, to process all the vertices
     * @see GlooBatch#render()
     * Batch is a local class in this file (see below)
     * 
     * After all the vertices are processed, they are send to the GPU through buffers
     * and the batch is cleared for the next frame
     * */
    public void render(int primitiveType){

//        System.out.println("rendering! " + batch.batch);

        // Bind the default shader
        GL20.glUseProgram(shaderProgram);
        // Bind the textureAtlas
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, atlas.handle());

        // bind to the default VAO and enable the standard VertexAttrib pointers
        GL30.glBindVertexArray(VAOID);
        // activate each of the parameters
        for(int i = 0; i < parameterCount; i++){
            GL20.glEnableVertexAttribArray(i);
        }
        // Bind to the vertices VBO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOID);
        // Bind to the indices VBO
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesVBOID);

        //
        batch.render();
        //
        List<FloatBuffer> vBuffers = batch.vBuffers;
        List<ShortBuffer> iBuffers = batch.iBuffers;
        // and finally send the render requests to the GPU to be processed

        for(int i = 0; i < iBuffers.size(); i++){
            if(vBuffers.get(i) != null && iBuffers.get(i) != null){
                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vBuffers.get(i), GL15.GL_STREAM_DRAW); // send the object vertices to the GPU
                GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, iBuffers.get(i), GL15.GL_STREAM_DRAW); // send the object indices to the GPU
                GL11.glDrawElements(primitiveType, iBuffers.get(i).limit(), GL11.GL_UNSIGNED_SHORT, 0); // Draw triangles, number of indices, type of indicesArray, where to run
            }
        }
        // reset for the next frame
        batch.clear();
    }

//    /**
//     * Adds a drawing object to a channel in the Batch by passing it on to the hidden Batch method
//     * Takes
//     * @param O a DrawingObject that is a collection of vertices
//     * (that must be the same type as the vertices of the implemented GlooBatch)
//     * */
    public final void add(DrawingObject O){
        batch.add(O);

    }
    public final void destroy(){
        atlas.destroy();

    }
    public GlooApplication app(){
        return application;

    }

    /** 
     * A Batch is a collection of sub-batches (which are referenced by channels)
     * Batch is basically a list of list of drawing objects
     *      Each list of drawing objects is a sub-batch (channel)
     *
     * In addition to containing the batches, they also hold lists of vertices and indices buffers
     *
     * When an object is added to a batch, it must be added to a particular channel
     * This allows for multithreading to occur as each thread can addBatch a processed drawing object
     * to a particular channel that no other thread is modifying (thereby avoiding synchronization!)
     *
     * Note that while the channels get reset each frame, the buffers persist, ensuring that the game runs faster
     * */
    private class Batch{

        Worker batchPool;
        LinkedList<DrawingObject> batch;
        List<FloatBuffer> vBuffers = new ArrayList<>();
        List<ShortBuffer> iBuffers = new ArrayList<>();

        /**
         * Constructs a Batch with no channels open,
         * no sub-batches, and empty vertices and indices buffers
         * */
        Batch(Worker pool){
            batchPool = pool;
            batch = new LinkedList<>();
            vBuffers.add(null);
            iBuffers.add(null);
        }

        void add(DrawingObject O){
            batch.addFirst(O);

        }

        /**
         * Clear each of the sub-batches (clear each of the lists of drawing objects)
         *
         * Also resets the number of open channels to 0
         * */
        void clear(){
            batch = new LinkedList<>();

        }

        /**
         * Makes a series of tasks (threads) equal in number to the number of open channels
         * Each task is a lambda function to the render(int index) call immediately below
         * @see GlooBatch.Batch#render(int)
         *
         * After making the list of tasks, they get sent to the ThreadPool (as blocking threads)
         * */
        void render(){
            int threads = (int)Math.min((float)batch.size()/batchPool.size(), batchPool.size());
            if(threads > 1){
                Runnable[] tasks = new Runnable[threads];
                while(vBuffers.size() < threads){vBuffers.add(null);}
                while(iBuffers.size() < threads){iBuffers.add(null);}
                for(int i = 0; i < tasks.length; i++){
                    int index = i;
                    tasks[i] = (() -> render(index, threads));
                }
                batchPool.await(batchPool.task(tasks));
            } else {
                while(vBuffers.size() < 1){vBuffers.add(null);}
                while(iBuffers.size() < 1){iBuffers.add(null);}
                render(0, 1);
            }
        }

        /**
         * This render call is the one that actually does things! (Congrats on getting here)
         * It is only called by virtue of a lambda function through a thread
         *
         * First, it gets the batch, vertices buffer, and indices buffer of its channel
         * Then, it cycles through each drawing object in the batch and increments the pointSize of the buffers
         *  to account for each new object
         * After that, it cycles though each drawing object and adds its vertices and indices to the vertices and indices buffers
         * Finally, it flips the buffers so that they are ready to be sent to the GPU
         * */
        void render(int index, int step){
            // references used throughout the render call
            FloatBuffer vBuffer = vBuffers.get(index);
            ShortBuffer iBuffer = iBuffers.get(index);
            // ensure buffer capacity
            int vSize = 0;
            int iSize = 0;
            //////////////
            for(int i = index; i < batch.size(); i += step){ // cycle through each drawing object and increment the pointSize of the buffers to fit the object
                vSize += batch.get(i).vertices().size() * vertexSize * 4;
                iSize += batch.get(i).indices().size();
            }/////////////
            if(vBuffer == null || vSize > vBuffer.capacity()){ // if vSize exceeds the buffer capacity, double the new needed capacity
                vBuffers.set(index, BufferUtils.createFloatBuffer(vSize * 2));
                vBuffer = vBuffers.get(index);
            }/////////////
            if(iBuffer == null || iSize > iBuffer.capacity()){ // if iSize exceeds the buffer capacity, double the new needed capacity
                iBuffers.set(index, BufferUtils.createShortBuffer(iSize * 2));
                iBuffer = iBuffers.get(index);
            }/////////////
            vBuffer.limit(vBuffer.capacity()); // manually sets the buffer limit equal to capacity
            iBuffer.limit(iBuffer.capacity()); // manually sets the buffer limit equal to capacity
            // load objects into buffers
            short iWalk = 0;
            //////////////
            DrawingObject O;
            for(int i = index; i < batch.size(); i += step){ // cycle through each object in the batch...
                O = batch.get(i);
                for(short j : O.indices()){
                    iBuffer.put((short)(j + iWalk)); // ... and addBatch the indices to the indices buffer
                }
                for(Vertex v: O.vertices()){
                    for(float f : v.attributes()){
                        vBuffer.put(f); // ... and addBatch the vertices to the vertices buffer
                    }
                }
                iWalk += O.vertices().size();
            }//////////////
            iBuffer.flip(); // ALWAYS FLIP THE BUFFERS!!!! (Feel my pain)
            vBuffer.flip();
            // sending data to the GPU is then handled above
        }
    }
}
