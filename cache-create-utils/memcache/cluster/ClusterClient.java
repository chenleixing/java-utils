package com.inspur.hsf.cache.memcache.cluster;

import com.inspur.hsf.cache.memcache.MemcachedClient;
import com.inspur.hsf.cache.memcache.cluster.bean.Server;
import com.inspur.hsf.cache.memcache.cluster.guardersuspention.queue.TaskQueue;
import com.inspur.hsf.cache.memcache.cluster.guardersuspention.serverthread.ServerThread;
import com.inspur.hsf.cache.memcache.cluster.guardersuspention.task.AddTask;
import com.inspur.hsf.cache.memcache.cluster.guardersuspention.task.AppendTask;
import com.inspur.hsf.cache.memcache.cluster.guardersuspention.task.DeleteTask;
import com.inspur.hsf.cache.memcache.cluster.guardersuspention.task.WriteTask;
import com.inspur.hsf.cache.memcache.exception.MemcachedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.rubyeye.xmemcached.GetsResponse;
import org.springframework.beans.factory.InitializingBean;

public class ClusterClient
  implements MemcachedClient, InitializingBean
{
  private List<Server> servers = new ArrayList();
  private int duration = 43200;
  private TaskQueue taskQueue = new TaskQueue();
  private ServerThread serverThread = new ServerThread(this.taskQueue);

  public void setClientList(List<MemcachedClient> memcachedClient)
  {
    for (Iterator localIterator = memcachedClient.iterator(); localIterator.hasNext(); ) { MemcachedClient one = (InitializingBean)localIterator.next();
      this.servers.add(new Server(one));
    }
  }

  public void afterPropertiesSet() throws Exception {
    if (this.servers.size() == 0)
      throw new RuntimeException("Memcached集群客户端Server列表不可为空");

    this.serverThread.start();
    setDuration(getDuration());
  }

  public int getDuration() {
    return this.duration;
  }

  public void setDuration(int duration) {
    for (Iterator localIterator = this.servers.iterator(); localIterator.hasNext(); ) { Server server = (Server)localIterator.next();
      server.getMemcachedClient().setDuration(duration);
    }
  }

  public Object get(String key)
  {
    List exits = new ArrayList();
    Integer index = nextInteger(this.servers.size(), exits); break label99:

    if (((Server)this.servers.get(index.intValue())).isReadable());
    try {
      return ((Server)this.servers.get(index.intValue())).getMemcachedClient().get(key);
    }
    catch (MemcachedIOException localMemcachedIOException1)
    {
      do
      {
        exits.add
          (index); label99: index = nextInteger(this.servers.size(), exits);
      }
      while (index != null);
      try
      {
        return ((Server)this.servers.get(0)).getMemcachedClient().get(key); } catch (MemcachedIOException e) { }
    }
    return null;
  }

  public Map<String, Object> get(Collection<String> keys)
  {
    List exits = new ArrayList();
    Integer index = nextInteger(this.servers.size(), exits); break label99:

    if (((Server)this.servers.get(index.intValue())).isReadable());
    try {
      return ((Server)this.servers.get(index.intValue())).getMemcachedClient().get(keys);
    }
    catch (MemcachedIOException localMemcachedIOException1)
    {
      do
      {
        exits.add
          (index); label99: index = nextInteger(this.servers.size(), exits);
      }
      while (index != null);
      try
      {
        return ((Server)this.servers.get(0)).getMemcachedClient().get(keys); } catch (MemcachedIOException e) { }
    }
    return Collections.EMPTY_MAP;
  }

  public boolean put(String key, Object value)
  {
    this.taskQueue.putTask(new WriteTask(this.servers, key, value));
    return true;
  }

  public boolean add(String key, Object value)
  {
    this.taskQueue.putTask(new AddTask(this.servers, key, value));
    return true;
  }

  public boolean delete(String key)
  {
    this.taskQueue.putTask(new DeleteTask(this.servers, key));
    return true;
  }

  public long incr(String key, long delta, long initValue)
  {
    long result = initValue;

    int i = 0; break label66:
    if (((Server)this.servers.get(i)).isReadable());
    try {
      result = ((Server)this.servers.get(i)).getMemcachedClient().incr(key, 
        delta, initValue);
    }
    catch (MemcachedIOException localMemcachedIOException)
    {
      label66: 
      do
        ++i; while (i < this.servers.size());
    }

    put(key, String.valueOf(result));
    return result;
  }

  public long decr(String key, long delta, long initValue)
  {
    Long result = Long.valueOf(initValue);

    for (int i = 0; i < this.servers.size(); ++i)
      if (((Server)this.servers.get(i)).isReadable())
        try {
          result = Long.valueOf(((Server)this.servers.get(i)).getMemcachedClient().decr(key, 
            delta, initValue));
        } catch (MemcachedIOException e) {
          break label89:
        }



    label89: put(key, String.valueOf(result));
    return result.longValue();
  }

  public boolean append(String key, Object value)
  {
    this.taskQueue.putTask(new AppendTask(this.servers, key, value));
    return true;
  }

  public void flush()
    throws MemcachedIOException
  {
    for (int i = 0; i < this.servers.size(); ++i)
      ((Server)this.servers.get(i)).getMemcachedClient().flush();
  }

  public void shutdown()
    throws MemcachedIOException
  {
    for (int i = 0; i < this.servers.size(); ++i)
      ((Server)this.servers.get(i)).getMemcachedClient().shutdown();

    this.serverThread.shutdown();
  }

  private Integer nextInteger(int range, List<Integer> exist)
  {
    if (range <= exist.size())
      return null;
    Random random = new Random();
    Integer i = Integer.valueOf(random.nextInt(range));
    while (true) { if (!(exist.contains(i)))
        return i;
      i = Integer.valueOf(random.nextInt(range));
    }
  }

  public boolean cas(String key, Object value, long cas)
  {
    throw new UnsupportedOperationException();
  }

  public GetsResponse<Object> gets(String key) throws MemcachedIOException {
    throw new UnsupportedOperationException();
  }
}