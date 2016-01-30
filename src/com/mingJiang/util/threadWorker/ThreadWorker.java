package com.mingJiang.util.threadWorker;
/**
 * @deprecated
 * @author Ming Jiang
 *
 */
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadWorker<T> {

  //  private int workerNum;
    private int current;
  //  private Object lock;
   // private Counter count;
    private ArrayList<T> groups;
    private ThreadPoolExecutor executor;

    public ThreadWorker(ArrayList<T> groups, int threadNum) {
        this.groups = groups;
        current = 0;
   //     count = new Counter(threadNum);
   //     lock = new Object();
   //     workerNum = threadNum;
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadNum);
    }

    public void setThread(int threadNum) {
    //    workerNum = threadNum;
    //    count.setMax(workerNum);
        executor= (ThreadPoolExecutor) Executors.newFixedThreadPool(threadNum);
    }

    public void startWork(final MyRunnable<T> run) {
        current = 0;
     //   count.reset();
        final MyRunnable<T> tmp = run.getInstance();
        for (int i = 0; i < groups.size(); i++) {
        	final int k = i;
            executor.execute(new Runnable() {
				
                public void run() {
                   tmp.run(groups.get(k));

                }
            });

        }
        executor.shutdown();
        try {
			executor.awaitTermination(5, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

    public synchronized T getNext() {
	//	if(current<groups.size())
        //	System.out.println("get next: "+current+" : "+groups.get(current));
        return current >= groups.size() ? null : groups.get(current++);
    }
}
