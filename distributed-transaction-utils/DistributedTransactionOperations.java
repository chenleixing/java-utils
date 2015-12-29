package com.inspur.hsf.transaction.distributed;

public abstract interface DistributedTransactionOperations
{
  public abstract Object execute(DistributedTransactionCallback paramDistributedTransactionCallback)
    throws com.inspur.hsf.transaction.distributed.DistributedTransactionException;
}