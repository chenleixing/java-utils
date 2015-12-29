package com.inspur.hsf.transaction.distributed;

import java.util.ArrayList;
import java.util.List;

public abstract class DistributedTransactionCallback
{
  private List<Boolean> hasExceptions = new ArrayList();

  public List<Boolean> getHasExceptions()
  {
    return this.hasExceptions;
  }

  public abstract Object doInDistributedTransaction();
}