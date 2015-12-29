package com.inspur.hsf.cache;

import com.inspur.hsf.cache.impl.MemcachedImpl;
import com.inspur.hsf.cache.memcache.affix.AffixClient;
import com.inspur.hsf.cache.memcache.base.BaseClient;
import com.inspur.hsf.service.common.utils.ServiceConfigUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CacheFactory
{
  private static Log log = LogFactory.getLog(CacheFactory.class);
  public static final String USE_MEMCACHE = "useMemcache";
  private static final String CACHE_ADDRESSES = "memcache.addresses";
  private static final String CACHE_DURATION = "memcache.duration";
  private static final String CACHE_ISUSESTRING = "memcache.isUseString";
  private static final String CACHE_PREFIX = "memcache.prefix";

  public static MemcachedImpl getMemcach()
  {
    MemcachedImpl memcache = new MemcachedImpl();
    String addresses = ServiceConfigUtils.getString("memcache.addresses");
    if (addresses != null) {
      String[] arrayOfString1;
      String[] address = addresses.split(";");
      List addressList = new ArrayList();
      int j = (arrayOfString1 = address).length; for (int i = 0; i < j; ++i) { String addrTmp = arrayOfString1[i];
        addressList.add(addrTmp);
      }
      String duration = ServiceConfigUtils.getString("memcache.duration", "0");
      String isUseString = ServiceConfigUtils.getString("memcache.isUseString", "true");
      BaseClient baseClient = new BaseClient();
      baseClient.setAddressList(addressList);
      baseClient.setDuration(Integer.valueOf(duration).intValue());
      baseClient.setIsUseString(Boolean.valueOf(isUseString).booleanValue());
      baseClient.afterPropertiesSet();

      String prefix = ServiceConfigUtils.getString("memcache.prefix", "");
      AffixClient affixClient = new AffixClient();
      affixClient.setMemcachedClient(baseClient);
      affixClient.setPrefix(prefix);

      memcache.setClient(affixClient);
    }
    else if (log.isErrorEnabled()) {
      log.error("没有配置memcache的ip和端口信息！");
    }
    return memcache;
  }
}