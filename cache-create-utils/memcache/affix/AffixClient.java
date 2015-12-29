package com.inspur.hsf.cache.memcache.affix;

import com.inspur.hsf.cache.memcache.MemcachedClient;
import com.inspur.hsf.cache.memcache.exception.MemcachedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import net.rubyeye.xmemcached.GetsResponse;

public class AffixClient
  implements MemcachedClient
{
  private MemcachedClient memcachedClient;
  private String prefix = "";
  private String suffix = "";

  public void setMemcachedClient(MemcachedClient memcachedClient)
  {
    this.memcachedClient = memcachedClient;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public int getDuration() {
    return this.memcachedClient.getDuration();
  }

  public void setDuration(int duration) {
    this.memcachedClient.setDuration(duration);
  }

  public long decr(String key, long delta, long initValue) throws MemcachedIOException
  {
    return this.memcachedClient.decr(getKey(key), delta, initValue);
  }

  public boolean delete(String key) throws MemcachedIOException {
    return this.memcachedClient.delete(getKey(key));
  }

  public Object get(String key) throws MemcachedIOException {
    return this.memcachedClient.get(getKey(key));
  }

  public Map<String, Object> get(Collection<String> keys) throws MemcachedIOException
  {
    return this.memcachedClient.get(getKey(keys));
  }

  public long incr(String key, long delta, long initValue) throws MemcachedIOException
  {
    return this.memcachedClient.incr(getKey(key), delta, initValue);
  }

  public boolean put(String key, Object value) throws MemcachedIOException {
    return this.memcachedClient.put(getKey(key), value);
  }

  public boolean cas(String key, Object value, long cas) throws MemcachedIOException
  {
    return this.memcachedClient.cas(getKey(key), value, cas);
  }

  public GetsResponse<Object> gets(String key) throws MemcachedIOException {
    return this.memcachedClient.gets(getKey(key));
  }

  public boolean add(String key, Object value) throws MemcachedIOException {
    return this.memcachedClient.add(getKey(key), value);
  }

  public boolean append(String key, Object value) throws MemcachedIOException {
    return this.memcachedClient.append(getKey(key), value);
  }

  public void flush() throws MemcachedIOException {
    this.memcachedClient.flush();
  }

  public void shutdown() throws MemcachedIOException {
    this.memcachedClient.shutdown();
  }

  private String getKey(String key) {
    return this.prefix + key + this.suffix;
  }

  private Collection<String> getKey(Collection<String> keys)
  {
    Collection result = new ArrayList();
    for (Iterator localIterator = keys.iterator(); localIterator.hasNext(); ) { String key = (String)localIterator.next();
      result.add(getKey(key));
    }
    return result;
  }
}