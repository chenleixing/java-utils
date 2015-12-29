package com.inspur.hsf.cache.memcache.cluster.guardersuspention.task;

import com.inspur.hsf.cache.memcache.MemcachedClient;
import com.inspur.hsf.cache.memcache.cluster.bean.Server;
import com.inspur.hsf.cache.memcache.exception.MemcachedIOException;
import java.util.List;

public class AppendTask
  implements ITask
{
  private List<Server> clientList;
  private String key;
  private Object value;

  public AppendTask(List<Server> clientList, String key, Object value)
  {
    this.clientList = clientList;
    this.key = key;
    this.value = value;
  }

  public void excuse()
  {
    for (int i = 0; i < this.clientList.size(); ) {
      try {
        if (((Server)this.clientList.get(i)).getMemcachedClient().append(this.key, this.value)) break label117;
        ((Server)this.clientList.get(i)).makeUnRead(
          ((Server)this.clientList.get(i)).getMemcachedClient().getDuration());
      }
      catch (MemcachedIOException e) {
        ((Server)this.clientList.get(i)).makeUnRead(
          ((Server)this.clientList.get(i)).getMemcachedClient().getDuration());
      }
      label117: ++i;
    }
  }
}