package com.inspur.hsf.cache.memcache.cluster.guardersuspention.task;

import com.inspur.hsf.cache.memcache.MemcachedClient;
import com.inspur.hsf.cache.memcache.cluster.bean.Server;
import com.inspur.hsf.cache.memcache.exception.MemcachedIOException;
import java.util.List;

public class DeleteTask
  implements ITask
{
  private List<Server> clientList;
  private String key;

  public DeleteTask(List<Server> clientList, String key)
  {
    this.clientList = clientList;
    this.key = key;
  }

  public void excuse()
  {
    for (int i = 0; i < this.clientList.size(); ) {
      try {
        if (((Server)this.clientList.get(i)).getMemcachedClient().delete(this.key)) break label113;
        ((Server)this.clientList.get(i)).makeUnRead(
          ((Server)this.clientList.get(i)).getMemcachedClient().getDuration());
      }
      catch (MemcachedIOException e) {
        ((Server)this.clientList.get(i)).makeUnRead(
          ((Server)this.clientList.get(i)).getMemcachedClient().getDuration());
      }
      label113: ++i;
    }
  }
}