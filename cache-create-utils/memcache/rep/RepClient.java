package com.inspur.hsf.cache.memcache.rep;

import com.inspur.hsf.cache.memcache.MemcachedClient;
import com.inspur.hsf.cache.memcache.exception.MemcachedIOException;
import java.util.Collection;
import java.util.Map;
import net.rubyeye.xmemcached.GetsResponse;

public class RepClient
  implements MemcachedClient
{
  private MemcachedClient memcachedClient;
  private int maxTry = 3;

  public void setMemcachedClient(MemcachedClient memcachedClient)
  {
    this.memcachedClient = memcachedClient;
  }

  public void setMaxTry(int maxTry) {
    this.maxTry = maxTry;
  }

  public int getDuration() {
    return this.memcachedClient.getDuration();
  }

  public void setDuration(int duration) {
    this.memcachedClient.setDuration(duration);
  }

  public Object get(String key)
    throws MemcachedIOException
  {
    int i = 0;
    try {
      return this.memcachedClient.get(key);
    }
    catch (MemcachedIOException localMemcachedIOException)
    {
      do
        ++i; while (i < this.maxTry);

      throw new MemcachedIOException();
    }
  }

  public GetsResponse<Object> gets(String key)
    throws MemcachedIOException
  {
    int i = 0;
    try {
      return this.memcachedClient.gets(key);
    }
    catch (MemcachedIOException localMemcachedIOException)
    {
      do
        ++i; while (i < this.maxTry);

      throw new MemcachedIOException();
    }
  }

  public Map<String, Object> get(Collection<String> keys)
    throws MemcachedIOException
  {
    int i = 0;
    try {
      return this.memcachedClient.get(keys);
    }
    catch (MemcachedIOException localMemcachedIOException)
    {
      do
        ++i; while (i < this.maxTry);

      throw new MemcachedIOException();
    }
  }

  public boolean put(String key, Object value)
    throws MemcachedIOException
  {
    for (int i = 0; i < this.maxTry; ++i)
      if (this.memcachedClient.put(key, value))
        return true;
    return false;
  }

  public boolean add(String key, Object value)
    throws MemcachedIOException
  {
    for (int i = 0; i < this.maxTry; ++i)
      if (this.memcachedClient.add(key, value))
        return true;
    return false;
  }

  public boolean cas(String key, Object value, long cas)
    throws MemcachedIOException
  {
    for (int i = 0; i < this.maxTry; ++i)
      if (this.memcachedClient.cas(key, value, cas))
        return true;
    return false;
  }

  public boolean delete(String key)
    throws MemcachedIOException
  {
    for (int i = 0; i < this.maxTry; ++i)
      if (this.memcachedClient.delete(key))
        return true;
    return false;
  }

  public long incr(String key, long delta, long initValue)
    throws MemcachedIOException
  {
    int i = 0;
    try {
      return this.memcachedClient.incr(key, delta, initValue);
    }
    catch (MemcachedIOException localMemcachedIOException)
    {
      do
        ++i; while (i < this.maxTry);

      throw new MemcachedIOException();
    }
  }

  public long decr(String key, long delta, long initValue)
    throws MemcachedIOException
  {
    int i = 0;
    try {
      return this.memcachedClient.decr(key, delta, initValue);
    }
    catch (MemcachedIOException localMemcachedIOException)
    {
      do
        ++i; while (i < this.maxTry);

      throw new MemcachedIOException();
    }
  }

  public boolean append(String key, Object obj)
    throws MemcachedIOException
  {
    for (int i = 0; i < this.maxTry; ++i)
      if (this.memcachedClient.append(key, obj))
        return true;
    return false;
  }

  public void flush()
  {
    int i = 0;
    try {
      this.memcachedClient.flush();
      return;
    }
    catch (MemcachedIOException localMemcachedIOException)
    {
      do
        ++i; while (i < this.maxTry);
    }
  }

  public void shutdown()
  {
    int i = 0;
    try {
      this.memcachedClient.shutdown();
      return;
    }
    catch (MemcachedIOException localMemcachedIOException)
    {
      do
        ++i; while (i < this.maxTry);
    }
  }
}