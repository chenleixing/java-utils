package com.inspur.hsf.cache.memcache.exception;

public class MemcachedIOException extends Exception
{
  private static final long serialVersionUID = 1L;

  public MemcachedIOException()
  {
    super("Memcached通讯异常");
  }

  public MemcachedIOException(Throwable t) {
    super(t);
  }
}