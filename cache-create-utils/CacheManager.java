package com.inspur.hsf.cache;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.phprpc.util.PHPSerializer;

public class CacheManager
{
  private static Log log = LogFactory.getLog(CacheManager.class);
  private static CacheInterface cacheInstance;

  public static void set(String key, Object value)
  {
    byte[] v;
    try
    {
      v = new PHPSerializer().serialize(value);
      String tt = new String(v, "utf-8");
      getCacheInstance().set(key, tt);
    } catch (IllegalArgumentException e) {
      log.error("参数不合法！", e);
    } catch (IllegalAccessException e) {
      log.error("访问出错！", e);
    } catch (InvocationTargetException e) {
      log.error("调用出错！", e);
    } catch (UnsupportedEncodingException e) {
      log.error("不支持的编码！", e);
    }
  }

  public static void set(String key, Object value, int duration)
  {
    byte[] v;
    try
    {
      v = new PHPSerializer().serialize(value);
      getCacheInstance().set(key, new String(v, "utf-8"), duration);
    } catch (IllegalArgumentException e) {
      log.error("参数不合法！", e);
    } catch (IllegalAccessException e) {
      log.error("访问出错！", e);
    } catch (InvocationTargetException e) {
      log.error("调用出错！", e);
    } catch (UnsupportedEncodingException e) {
      log.error("不支持的编码！", e);
    }
  }

  public static Object get(String key)
  {
    Object s = getCacheInstance().get(key);
    if (s == null)
      return null;
    try
    {
      return new PHPSerializer().unserialize(((String)s).getBytes("utf-8"), String.class);
    } catch (IllegalArgumentException e) {
      log.error("参数不合法！", e);
    } catch (IllegalAccessException e) {
      log.error("访问出错！", e);
    } catch (InvocationTargetException e) {
      log.error("调用出错！", e);
    } catch (UnsupportedEncodingException e) {
      log.error("不支持的编码！", e);
    }
    return null;
  }

  public static <T> T get(String key, Class<T> cls)
  {
    Object s = getCacheInstance().get(key);
    if (s == null)
      return null;
    try
    {
      return new PHPSerializer().unserialize(((String)s).getBytes("utf-8"), cls);
    } catch (IllegalArgumentException e) {
      log.error("参数不合法！", e);
      System.out.println("参数不合法:"); e.printStackTrace();
    } catch (IllegalAccessException e) {
      log.error("访问出错！", e);
    } catch (InvocationTargetException e) {
      log.error("调用出错！", e);
    } catch (UnsupportedEncodingException e) {
      log.error("不支持的编码！", e);
      System.out.println("参数不合法:"); e.printStackTrace();
    }
    return null;
  }

  public static boolean remove(String key)
  {
    return getCacheInstance().remove(key);
  }

  public static void clear()
  {
    getCacheInstance().clear();
  }

  private static CacheInterface getCacheInstance() {
    if (cacheInstance == null)
      try {
        cacheInstance = CacheFactory.getMemcach();
      } catch (Exception e) {
        log.error(e);
        throw new RuntimeException("获取缓存配置信息失败!");
      }

    return cacheInstance;
  }
}