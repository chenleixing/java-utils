package com.inspur.hsf.cache.memcache.cluster.guardersuspention.queue;

import com.inspur.hsf.cache.memcache.cluster.guardersuspention.task.ITask;
import java.util.LinkedList;
import java.util.List;

public class TaskQueue
{
  private List<ITask> queue = new LinkedList();

  public synchronized ITask getTask()
  {
    while (this.queue.size() == 0)
      try {
        super.wait();
      } catch (InterruptedException e) {
        return null;
      }

    return ((ITask)this.queue.remove(0));
  }

  public synchronized void putTask(ITask task) {
    this.queue.add(task);
    super.notifyAll();
  }
}