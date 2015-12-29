package com.inspur.hsf.transaction.distributed;

import com.inspur.hsf.service.rpc.bootstrap.ServiceConfig;
import com.inspur.hsf.service.rpc.bootstrap.ServiceReference;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DistributedTransactionManager
{
  protected final Log log = LogFactory.getLog(DistributedTransactionManager.class);

  public void commit(String transId)
    throws com.inspur.hsf.transaction.distributed.DistributedTransactionException
  {
    execute(transId, "commit");
  }

  public void rollback(String transId)
    throws com.inspur.hsf.transaction.distributed.DistributedTransactionException
  {
    execute(transId, "rollback");
  }

  public void execute(String transId, String command)
    throws com.inspur.hsf.transaction.distributed.DistributedTransactionException
  {
    List serviceConfigs = 
      DistributedTransactionManagerUtil.getServiceConfigs(transId);

    for (int i = 0; i < serviceConfigs.size(); ++i) {
      Object[] args = { transId };
      ServiceConfig serviceConfig = (ServiceConfig)serviceConfigs.get(i);
      serviceConfig.setServiceName("transCommandService");
      ServiceReference reference = new ServiceReference(serviceConfig);
      try {
        reference.invoke(command, args);
      } catch (Throwable e) {
        if (this.log.isErrorEnabled())
          this.log.error("传替" + command + "命令出错", e);
      }
    }

    DistributedTransactionManagerUtil.executeCommand(transId, command, true);
  }

  protected void doCleanupAfterCompletion(String transId)
  {
    DistributedTransactionManagerUtil.removeUrl(transId);
    DistributedTransactionManagerUtil.removeTransStatus(transId);
    DistributedTransactionManagerUtil.removeTransManager(transId);
  }
}