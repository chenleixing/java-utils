package com.inspur.hsf.cache.memcache;

import com.inspur.hsf.cache.memcache.exception.MemcachedIOException;
import java.util.Collection;
import java.util.Map;
import net.rubyeye.xmemcached.GetsResponse;

public abstract interface MemcachedClient
{
  public abstract int getDuration();

  public abstract void setDuration(int paramInt);

  public abstract Object get(String paramString)
    throws MemcachedIOException;

  public abstract GetsResponse<Object> gets(String paramString)
    throws MemcachedIOException;

  public abstract Map<String, Object> get(Collection<String> paramCollection)
    throws MemcachedIOException;

  public abstract boolean put(String paramString, Object paramObject)
    throws MemcachedIOException;

  public abstract boolean add(String paramString, Object paramObject)
    throws MemcachedIOException;

  public abstract boolean cas(String paramString, Object paramObject, long paramLong)
    throws MemcachedIOException;

  public abstract boolean delete(String paramString)
    throws MemcachedIOException;

  public abstract long incr(String paramString, long paramLong1, long paramLong2)
    throws MemcachedIOException;

  public abstract long decr(String paramString, long paramLong1, long paramLong2)
    throws MemcachedIOException;

  public abstract boolean append(String paramString, Object paramObject)
    throws MemcachedIOException;

  public abstract void flush()
    throws MemcachedIOException;

  public abstract void shutdown()
    throws MemcachedIOException;
}