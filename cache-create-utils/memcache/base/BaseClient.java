package com.inspur.hsf.cache.memcache.base;

import com.inspur.hsf.cache.memcache.TraversalMemcachedClient;
import com.inspur.hsf.cache.memcache.exception.MemcachedIOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.rubyeye.xmemcached.GetsResponse;
import net.rubyeye.xmemcached.KeyIterator;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.transcoders.Transcoder;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

public class BaseClient
  implements TraversalMemcachedClient, InitializingBean
{
  private static Logger log = Logger.getLogger(BaseClient.class);
  private List<String> addressList;
  private int duration = 0;
  private boolean isBinary;
  private boolean isUseString;
  private MemcachedClient client;

  public void setAddressList(List<String> addressList)
  {
    this.addressList = addressList;
  }

  public void setDuration(int duration)
  {
    this.duration = duration;
  }

  public int getDuration() {
    return this.duration;
  }

  public void setIsBinary(boolean isBinary) {
    this.isBinary = isBinary;
  }

  public void afterPropertiesSet()
  {
    if ((this.addressList == null) || (this.addressList.size() == 0))
      throw new RuntimeException("Memcached�ͻ��˵�ַ�б?��Ϊ��");
    try
    {
      MemcachedClientBuilder builder = getMemcachedClientBuilder();
      if (this.isBinary) {
        builder.setCommandFactory(new BinaryCommandFactory());
      }

      this.client = builder.build();
      if (this.isUseString)
        this.client.setPrimitiveAsString(true);

      this.client.getTranscoder().setCompressionThreshold(1048576);
    } catch (Exception e) {
      log.error(e);
    }
  }

  public Object get(String key)
    throws MemcachedIOException
  {
    try
    {
      return this.client.get(key);
    } catch (Exception e) {
      throw new MemcachedIOException(e);
    }
  }

  public GetsResponse<Object> gets(String key)
    throws MemcachedIOException
  {
    try
    {
      return this.client.gets(key);
    } catch (Exception e) {
      throw new MemcachedIOException(e);
    }
  }

  public Map<String, Object> get(Collection<String> keys)
    throws MemcachedIOException
  {
    try
    {
      return this.client.get(keys);
    } catch (Exception e) {
      throw new MemcachedIOException(e);
    }
  }

  public boolean put(String key, Object value)
    throws MemcachedIOException
  {
    try
    {
      return this.client.set(key, this.duration, value);
    } catch (Exception e) {
      throw new MemcachedIOException(e);
    }
  }

  public boolean add(String key, Object value)
    throws MemcachedIOException
  {
    try
    {
      return this.client.add(key, this.duration, value);
    } catch (Exception e) {
      throw new MemcachedIOException(e);
    }
  }

  public boolean cas(String key, Object value, long cas)
    throws MemcachedIOException
  {
    try
    {
      return this.client.cas(key, this.duration, value, cas);
    } catch (Exception e) {
      throw new MemcachedIOException(e);
    }
  }

  public boolean delete(String key)
    throws MemcachedIOException
  {
    try
    {
      return this.client.delete(key);
    } catch (Exception e) {
      throw new MemcachedIOException(e);
    }
  }

  public long incr(String key, long delta, long initValue)
    throws MemcachedIOException
  {
    try
    {
      return this.client.incr(key, delta, initValue, this.duration);
    } catch (Exception e) {
      throw new MemcachedIOException(e);
    }
  }

  public long decr(String key, long delta, long initValue)
    throws MemcachedIOException
  {
    try
    {
      return this.client.decr(key, delta, initValue, this.duration);
    } catch (Exception e) {
      throw new MemcachedIOException(e);
    }
  }

  public boolean append(String key, Object value)
    throws MemcachedIOException
  {
    try
    {
      return this.client.append(key, value);
    } catch (Exception e) {
      throw new MemcachedIOException(e);
    }
  }

  public int getGroupCount()
  {
    return this.addressList.size();
  }

  public KeyIterator getKeyIterator(int index)
    throws MemcachedIOException
  {
    try
    {
      return this.client.getKeyIterator(AddrUtil.getOneAddress(
        ((String)this.addressList.get
        (index)).split(",")[0]));
    } catch (Exception e) {
      throw new MemcachedIOException(e);
    }
  }

  public void flush()
    throws MemcachedIOException
  {
    try
    {
      this.client.flushAll();
    } catch (Exception e) {
      throw new MemcachedIOException(e);
    }
  }

  public void shutdown()
    throws MemcachedIOException
  {
    try
    {
      this.client.shutdown();
    } catch (Exception e) {
      throw new MemcachedIOException(e);
    }
  }

  private MemcachedClientBuilder getMemcachedClientBuilder()
  {
    MemcachedClientBuilder builder;
    String adress = getAdressString();

    if (isFailureMode()) {
      builder = new XMemcachedClientBuilder(
        AddrUtil.getAddressMap(adress));
      builder.setFailureMode(true);
    } else {
      builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(adress));
    }
    return builder;
  }

  private String getAdressString() {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < this.addressList.size(); ++i) {
      buffer.append((String)this.addressList.get(i));
      if (i < this.addressList.size() - 1)
        buffer.append(" ");
    }

    return buffer.toString();
  }

  private boolean isFailureMode() {
    for (Iterator localIterator = this.addressList.iterator(); localIterator.hasNext(); ) { String address = (String)localIterator.next();
      if (address.indexOf(",") <= 0) break label34;
      label34: return true;
    }
    return false;
  }

  public boolean getIsUseString() {
    return this.isUseString;
  }

  public void setIsUseString(boolean isUseString) {
    this.isUseString = isUseString;
  }
}