package com.inspur.hsf.cache.memcache;

import com.inspur.hsf.cache.memcache.exception.MemcachedIOException;
import net.rubyeye.xmemcached.KeyIterator;

public abstract interface TraversalMemcachedClient extends MemcachedClient
{
  public abstract int getGroupCount();

  public abstract KeyIterator getKeyIterator(int paramInt)
    throws MemcachedIOException;
}