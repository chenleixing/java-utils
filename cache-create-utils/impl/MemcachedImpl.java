package com.inspur.hsf.cache.impl;

import com.inspur.hsf.cache.CacheInterface;
import com.inspur.hsf.cache.memcache.MemcachedClient;
import com.inspur.hsf.cache.memcache.exception.MemcachedIOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MemcachedImpl
  implements CacheInterface
{
  private static Log log = LogFactory.getLog(MemcachedImpl.class);
  private MemcachedClient client;

  public Object get(String key)
  {
    try
    {
      return this.client.get(key);
    } catch (MemcachedIOException e) {
      log.error(e);
    }
    return null;
  }

  public boolean remove(String key) {
    try {
      return this.client.delete(key);
    } catch (MemcachedIOException e) {
      log.error(e);
    }
    return false; }

  public void set(String key, Object value, int duration) {
    int defaultDuration;
    try {
      defaultDuration = this.client.getDuration();
      this.client.put(key, value);
      this.client.setDuration(defaultDuration);
    } catch (MemcachedIOException e) {
      log.error(e);
    }
  }

  public void set(String key, Object value) {
    try {
      this.client.put(key, value);
    } catch (MemcachedIOException e) {
      log.error(e); }
  }

  public void clear() {
    try {
      this.client.flush();
    } catch (MemcachedIOException e) {
      log.error(e);
    }
  }

  public void setClient(MemcachedClient client)
  {
    this.client = client;
  }
}