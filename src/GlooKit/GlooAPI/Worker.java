package GlooKit.GlooAPI;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;

public class Worker {

    /**A flag used to permanently halt all the Worker threads associated with this thread.*/
    private boolean halt;
    /**A flag used to check whether a thread currently has any available work.*/
    private boolean idle;

    /**A reference to a collection of all the Worker threads associated with this one.*/
    private final Worker[] workers;
    /**A reference to the queue utilized by this Worker pool / thread*/
    private final Queue<FutureTask<Object>> queue;

    /**
     * A public default constructor which creates a worker pool with a number of threads appropriate for the runtime environment.
     */
    public Worker(){
        this(Runtime.getRuntime().availableProcessors());

    }

    /**
     * A public constructor which creates a worker pool with a set number of threads.
     * @param count indicates the number of threads the worker pool will possess.
     */
    public Worker(int count){
        queue = new LinkedList<>();
        workers = new Worker[count];
        for(int i = 0; i < count; i++){
            workers[i] = new Worker(queue, workers);
        }
        halt = false;
    }

    ////////////////////////////////////
    ////////////////////////////////////

    /**
     * A private constructor used in order to create worker pools. Worker pools all reference the same queue and
     * worker list, necessitating this constructor. All Workers created with this constructor may not be referenced
     * outside of this file.
     * @param queue represents the queue utilized by this hidden worker.
     * @param workers represents a collection of all Workers utilizing the same queue as this one.
     */
    private Worker(Queue<FutureTask<Object>> queue, Worker[] workers){
        this.queue = queue;
        this.workers = workers;
        this.halt = false;
        new Thread(()->{
            try{
                while(!halt){
                    Runnable T = dequeue();
                    if(T != null){
                        T.run();
                    } else {
                        idle = true;
                        synchronized(this){this.wait();}
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
                System.exit(-400);
            }
        }).start();
    }

    ////////////////////////////////////
    ////////////////////////////////////

    /**
     * A private call which securely queues Runnable tasks into this Worker's operating queue.
     * @param task is a Runnable, which will be taken up by one of the Worker threads associated with this
     * Worker's queue.
     * @return Returns the FutureTask of the task submitted to the queue. This information is important to capture
     * so that tasks can later be waited upon and/or confirmed to have been executed.
     */
    // TODO batch enqueuing multiple tasks!!!
    private FutureTask<Object> enqueue(Runnable task){
        FutureTask<Object> F = new FutureTask<>(task, null);
        synchronized(queue){
            queue.offer(F);
            if(queue.size() == 1){
                for(Worker w : workers){
                    if(w.idle){
                        synchronized(w){
                            w.notify();
                            w.idle = false;
                        }
                    }
                }
            }
        }
        return F;
    }

    /**
     * Safely removes a task from the queue, so that this thread may execute that task.
     * @return Returns the FutureTask of the retrieved task. This information is important to capture
     * so that tasks can later be waited upon and/or confirmed to have been executed.
     */
    private FutureTask<Object> dequeue(){
        synchronized(queue){
            return queue.poll();
        }
    }

    /**
     * One of the primary utility functions of a Worker pool, this method handles the enqueuing
     * of tasks and returns the futures necessary for managing those tasks over time.
     * @param tasks is a collection of tasks to be enqueued.
     * @return The FutureTask representations of the given tasks (in order to handle asynchronisity, probably through the use of the await methods).
     */
    public Future[] task(Runnable[] tasks){
        Future[] F = new Future[tasks.length];
        for(int i =0; i < tasks.length; i++){
            F[i] = enqueue(tasks[i]);
        }
        return F;
    }

    /**
     * One of the primary utility functions of a Worker pool, this method handles the enqueuing
     * of tasks and returns the futures necessary for managing those tasks over time.
     * @param task is a single task to be enqueued.
     * @return The FutureTask representations of the given task (in order to handle asynchronisity, probably through the use of the await methods).
     */
    public Future task(Runnable task){
        return enqueue(task);

    }

    /**
     * One of the primary utility functions of a Worker pool, this method blocks the thread it is called in until the completion
     * of the given future tasks. While waiting, the thread is in a true wait and yields its cpu time to other tasks.
     * @param future is the task to be awaited.
     * */
    public void await(Future future){
        try{
            future.get();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * One of the primary utility functions of a Worker pool, this method blocks the thread it is called in until the completion
     * of the given future tasks. While waiting, the thread is in a true wait and yields its cpu time to other tasks.
     * @param futures is the group of tasks to be awaited.
     * */
    public void await(Future[] futures){
        for (Future f : futures){
            await(f);
        }
    }

    /**
     * Returns the number of {@code Worker}s available in this thread pool.
     * */
    public int size(){
        return workers.length;

    }

    /**
     * Safely ends all {@code Worker}s in this thread pool. All their remaining tasks will be abandoned, though present tasks
     * will be completed. All {@code Worker}s must be terminated in order for an application to truly exit.
     * */
    public void destroy(){
        for(Worker w : workers){
            w.halt = true;
            synchronized(w){
                w.notify();
                w.idle = false;
            }
        }
        halt = true;
    }

}

