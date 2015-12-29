package com.inspur.hsf.cache.memcache.cluster.bean;

import com.inspur.hsf.cache.memcache.MemcachedClient;

public class Server
{
  private MemcachedClient memcachedClient;
  private boolean readable = true;
  private long expirationMillis = -1L;

  public Server(MemcachedClient client)
  {
    this.memcachedClient = client;
  }

  public MemcachedClient getMemcachedClient() {
    return this.memcachedClient;
  }

  public void setMemcachedClient(MemcachedClient memcachedClient) {
    this.memcachedClient = memcachedClient;
  }

  public boolean isReadable() {
    if ((!(this.readable)) && (System.currentTimeMillis() > this.expirationMillis))
      this.readable = true;

    return this.readable;
  }

  public void makeUnRead(long duration) {
    this.readable = false;
    if (duration == 6549907366433783808L)
      this.expirationMillis = 9223372036854775807L;
    else
      this.expirationMillis = (System.currentTimeMillis() + duration * 1000L);
  }
}