package com.inspur.hsf.cache.memcache.cluster.guardersuspention.serverthread;

import com.inspur.hsf.cache.memcache.cluster.guardersuspention.queue.TaskQueue;
import com.inspur.hsf.cache.memcache.cluster.guardersuspention.task.ITask;

public class ServerThread extends Thread
{
  private boolean stop = false;
  private TaskQueue queue;

  public ServerThread(TaskQueue queue)
  {
    this.queue = queue;
  }

  public void shutdown() {
    this.stop = true;
    interrupt();
    try {
      join();
    } catch (InterruptedException localInterruptedException) {
    }
  }

  public void run() {
    while (!(this.stop)) {
      ITask task = this.queue.getTask();
      if (task != null)
        task.excuse();
    }
  }
}