package com.inspur.hsf.transaction.distributed;

public abstract class DistributedTransactionException extends RuntimeException
{
  private static final long serialVersionUID = -1122887400528590001L;

  public DistributedTransactionException(String msg)
  {
    super(msg);
  }

  public DistributedTransactionException(String msg, Throwable cause)
  {
    super(msg, cause);
  }
}