package GlooKit.GlooAPI;

import GlooKit.GlooFramework.DefaultBatch;
import GlooKit.GlooFramework.GlooApplication;
import GlooKit.GlooFramework.TextBatch;
import GlooKit.Utils.Matrix;
import org.lwjgl.opengl.GL11;

import java.util.*;

/**
 * All drawing calls pass through here at some point in time.
 *
 * GlooCore's main feature is a BatchQueue, which is basically a glorified arraylist of batches
 * @see GlooCore.BatchQueue
 *
 * Currently, GlooCore only has one window, which limits us to one application running at a time
 *
 * GlooCore is, however, capable of multithreading.
 * Given that a batch is threadable, it automatically threads drawing elements of the batch.
 *
 * Author: Duncan Walter
 * Documenter: Eli Jergensen
 * */
// TODO Enable multi-windowing
public class GlooCore {

    public static final int DEFAULT = 0; // the default batch is always added first
    public static final int TEXT = 1; // the text batch is always added next

    private BatchQueue batchQueue;

    private Worker pool;

    public GlooCore(){

    }
    public void init(GlooApplication app){
        pool = new Worker();
        batchQueue = new BatchQueue();
        addBatch("default", new DefaultBatch(app)); // Initialize the default batch as the zeroth element of the list
        addBatch("text", new TextBatch(app)); // Initialize the text batch as the first element of the list
    }

    /**
     * This renderFrame call is essentially the main draw call for the entire program.
     * It cycles through all of the batches in the batchQueue and calls render on each
     * see GlooKit.GlooAPI.GlooBatch.Batch#render()
     *
     * Each of the batches is responsible for rendering itself in its entirety
     *
     * Once the batches are all rendered, the buffers are swapped and the batchQueue reset
     * */
    public void renderFrame(int w, int h){
        // clear the color buffer (draw the background over the screen)
        GL11.glClearColor(0.4f, 0.6f, 0.9f, 0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

        Matrix panelMatrix = new Matrix(4);
        panelMatrix.set(0, 0, +2f/w); //
        panelMatrix.set(1, 1, +2f/h); //
        panelMatrix.set(0, 3, -1); //
        panelMatrix.set(1, 3, -1); //
        /*
        * 2./w    0     0    -1
        *  0    2./h    0    -1
        *  0      0     1     0
        *  0      0     0     1
        *
        *  x' = (2./w)x - w
        *  y' = (2./h)y - w
        *  z' = z
        *  w' = w
        * */

        while(batchQueue.hasNext()){
            batchQueue.next().render(panelMatrix);
        }

        batchQueue.reset();
    }
    public GlooBatch getBatch(int handle){
        return batchQueue.getBatch(handle);

    }
    public int getHandle(String key){
        return batchQueue.getHandle(key);

    }
    public int addBatch(String key, GlooBatch batch){
        return batchQueue.addBatch(key, batch);

    }
    public GlooBatch ripBatch(int batchHandle){
        return batchQueue.ripBatch(batchHandle);

    }
    public Worker getPool(){
        return pool;

    }
    public void destroy(){
        batchQueue.destroy();

    }

    /**
     * The BatchQueue is specific to the GlooCore (GlooSockets don't have them)
     * The BatchQueue is basically a list of batches, but it also has a specific iterator,
     * which allows it to do pass off batches to different threads without them conflicting with each other
     *
     * Author: Duncan Walter
     * Documenter: Eli Jergensen
     * */
    private class BatchQueue {

        HashMap<String, Integer> handles;
        List<GlooBatch> batches;
        List<Integer> sequence;

        int iterator;

        BatchQueue(){
            handles = new HashMap<>();
            batches = new ArrayList<>();
            sequence = new ArrayList<>();
        }
        int addBatch(String key, GlooBatch batch){
            if(handles.get(key) == null){
                batch.handle = -1;
                for(int i = 0; i < batches.size(); i++){
                    if(batches.get(i) == null){
                        handles.put(key, batches.size());
                        batch.handle = i;
                        batches.set(i, batch);
                    }
                }
                if(batch.handle == -1){
                    handles.put(key, batches.size());
                    batch.handle = batches.size();
                    batches.add(batch);
                }
                for(int i = 0; i < sequence.size(); i++){
                    if(batch.precedent < batches.get(sequence.get(i)).precedent){
                        sequence.add(i, batch.handle);
                        return batch.handle;
                    }
                }
                sequence.add(batch.handle);
                return batch.handle;
            } else {
                int i = handles.get(key);
                batches.set(i, batch);
                return i;
            }
        }
        GlooBatch ripBatch(int batchHandle){
            for(int i = 0; i < sequence.size(); i++){
                if(sequence.get(i).equals(batchHandle)){
                    sequence.remove(i);
                }
            }
            GlooBatch batch = batches.get(batchHandle);
            batches.set(batchHandle, null);
            return batch;
        }
        int getHandle(String key){
            return handles.get(key);

        }
        GlooBatch getBatch(int handle){
            return batches.get(handle);

        }
        boolean hasNext(){
            return iterator < sequence.size();

        }
        GlooBatch next(){
            iterator += 1;
            return batches.get(sequence.get(iterator - 1));
        }
        void reset(){
            iterator = 0;

        }
        void destroy(){
            for(GlooBatch batch : batches){
                if(!batch.destroy()){
                    new Exception("ERROR: batch mismatch detected during application cleanup").printStackTrace();
                    System.exit(-400);
                }
            }
        }
    }
}
