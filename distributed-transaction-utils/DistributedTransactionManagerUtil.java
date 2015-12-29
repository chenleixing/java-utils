package com.inspur.hsf.transaction.distributed;

import com.inspur.hsf.service.common.URL;
import com.inspur.hsf.service.rpc.bootstrap.ServiceConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DistributedTransactionManagerUtil
{
  private static Map<String, List<Object>> transManagers = new ConcurrentHashMap();
  private static Map<String, List<Object>> transStatus = new ConcurrentHashMap();
  private static Map<String, List<URL>> urlMap = new ConcurrentHashMap();
  private static Map<String, List<Object>> nativeTransManagers = new ConcurrentHashMap();
  private static Map<String, List<Object>> nativeTransStatus = new ConcurrentHashMap();

  public static void addTransManager(String transId, Object manager, boolean isNative)
  {
    if (isNative)
      addTransManager(transId, manager, nativeTransManagers);
    else
      addTransManager(transId, manager, transManagers);
  }

  private static void addTransManager(String transId, Object manager, Map<String, List<Object>> transManagers)
  {
    List managers = (List)transManagers.get(transId);
    if (managers == null)
      managers = new ArrayList();

    managers.add(manager);
    transManagers.put(transId, managers);
  }

  public static void removeTransManager(String transId)
  {
    transManagers.remove(transId);
    nativeTransManagers.remove(transId);
  }

  public static void addTransStatus(String transId, Object status, boolean isNative)
  {
    if (isNative)
      addTransStatus(transId, status, nativeTransStatus);
    else
      addTransStatus(transId, status, transStatus);
  }

  private static void addTransStatus(String transId, Object status, Map<String, List<Object>> transStatus)
  {
    List statuses = (List)transStatus.get(transId);
    if (statuses == null)
      statuses = new ArrayList();

    statuses.add(status);
    transStatus.put(transId, statuses);
  }

  public static void removeTransStatus(String transId)
  {
    transStatus.remove(transId);
    nativeTransStatus.remove(transId);
  }

  public static void addUrl(String transId, URL url)
  {
    List urls = (List)urlMap.get(transId);
    if (urls == null)
      urls = new ArrayList();

    urls.add(url);
    urlMap.put(transId, urls);
  }

  public static List<URL> getUrl(String transId)
  {
    List urls = (List)urlMap.get(transId);
    if (urls == null)
      urls = new ArrayList();

    return urls;
  }

  public static void removeUrl(String transId)
  {
    urlMap.remove(transId);
  }

  public static List<ServiceConfig> getServiceConfigs(String transId)
  {
    List services = new ArrayList();
    List urls = (List)urlMap.get(transId);
    if (urls != null)
      for (int i = 0; i < urls.size(); ++i) {
        URL url = (URL)urls.get(i);
        ServiceConfig service = new ServiceConfig();
        service.setProtocol(url.getProtocol());
        service.setHost(url.getHost());
        service.setPort(url.getPort());
        services.add(service);
      }

    return services;
  }

  public static void executeCommand(String transId, String command, boolean isNative)
  {
    if (isNative)
      executeCommand(transId, command, nativeTransManagers, nativeTransStatus);
    else
      executeCommand(transId, command, transManagers, transStatus);
  }

  private static void executeCommand(String transId, String command, Map<String, List<Object>> transManagers, Map<String, List<Object>> transStatus)
  {
    List managers = (List)transManagers.get(transId);
    List statuses = (List)transStatus.get(transId);
    if (managers == null)
      return;
    for (int i = 0; i < managers.size(); ++i) {
      Object manager = managers.get(i);
      Object status = statuses.get(i);
      if (manager instanceof org.springframework.transaction.PlatformTransactionManager) {
        org.springframework.transaction.PlatformTransactionManager realManager = (org.springframework.transaction.PlatformTransactionManager)manager;
        org.springframework.transaction.TransactionStatus realStatus = (org.springframework.transaction.TransactionStatus)status;
        if ("commit".equalsIgnoreCase(command))
          realManager.commit(realStatus);
        else
          realManager.rollback(realStatus);
      }
      else {
        org.loushang.persistent.transaction.PlatformTransactionManager realManager = (org.loushang.persistent.transaction.PlatformTransactionManager)manager;
        org.loushang.persistent.transaction.TransactionStatus realStatus = (org.loushang.persistent.transaction.TransactionStatus)status;
        if ("commit".equalsIgnoreCase(command))
          realManager.commit(realStatus);
        else
          realManager.rollback(realStatus);
      }
    }
  }
}