package com.inspur.hsf.transaction.distributed;

import com.inspur.hsf.service.rpc.RpcContext;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

public class DistributedTransaction
  implements DistributedTransactionOperations, InitializingBean
{
  private static final long serialVersionUID = 6188527538466379374L;
  protected final Log logger = LogFactory.getLog(DistributedTransaction.class);
  private static final AtomicLong TRANS_ID = new AtomicLong(6549909239039524864L);
  private DistributedTransactionManager transactionManager;
  private long transId;

  public DistributedTransaction()
  {
    this.transId = newId();
  }

  public DistributedTransaction(DistributedTransactionManager transactionManager)
  {
    this.transactionManager = transactionManager;
    this.transId = newId();
  }

  public void setTransactionManager(DistributedTransactionManager transactionManager)
  {
    this.transactionManager = transactionManager;
  }

  public DistributedTransactionManager getTransactionManager() {
    return this.transactionManager;
  }

  public void afterPropertiesSet() {
    if (this.transactionManager == null)
      throw new IllegalArgumentException("属性transactionManager不能为空！");
  }

  public Object execute(DistributedTransactionCallback action)
    throws com.inspur.hsf.transaction.distributed.DistributedTransactionException
  {
    Object result = null;
    try
    {
      Map contextMap = RpcContext.getContext().getMap();
      contextMap.put("transactonId", 
        String.valueOf(this.transId));
      result = action.doInDistributedTransaction();
      contextMap.remove("transactonId");
    } catch (RuntimeException ex) {
      rollbackOnException(ex);
      throw ex;
    } catch (Error err) {
      rollbackOnException(err);
      throw err;
    }

    boolean needRollback = false;
    List hasExceptions = action.getHasExceptions();
    for (int i = 0; i < hasExceptions.size(); ++i)
      if (((Boolean)hasExceptions.get(i)).booleanValue()) {
        needRollback = true;
        break;
      }

    String transactionId = String.valueOf(this.transId);
    if (needRollback)
      this.transactionManager.rollback(transactionId);
    else
      this.transactionManager.commit(transactionId);

    this.transactionManager.doCleanupAfterCompletion(transactionId);
    return result;
  }

  private void rollbackOnException(Throwable ex) throws com.inspur.hsf.transaction.distributed.DistributedTransactionException
  {
    this.logger.debug(
      "Initiating transaction rollback on application exception", ex);
    Map contextMap = RpcContext.getContext().getMap();
    contextMap.remove("transactonId");
    String transactionId = String.valueOf(this.transId);
    try {
      this.transactionManager.rollback(transactionId);
    } catch (RuntimeException ex2) {
      this.logger.error(
        "Application exception overridden by rollback exception", 
        ex);
      throw ex2;
    } catch (Error err) {
      this.logger.error("Application exception overridden by rollback error", 
        ex);
      throw err;
    } finally {
      this.transactionManager.doCleanupAfterCompletion(transactionId);
    }
  }

  private static long newId()
  {
    return TRANS_ID.getAndIncrement();
  }

  public long getTransId() {
    return this.transId;
  }
}