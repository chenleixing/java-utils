package com.inspur.hsf.cache;

public abstract interface CacheInterface
{
  public abstract void set(String paramString, Object paramObject);

  public abstract void set(String paramString, Object paramObject, int paramInt);

  public abstract Object get(String paramString);

  public abstract boolean remove(String paramString);

  public abstract void clear();
}