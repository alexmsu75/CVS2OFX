package com.ajic.fi.xls_ofx_converter.model;

import static org.junit.Assert.assertEquals;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

public class CapitalOneSparkTransactionTest {
  private final static Logger log = Logger.getLogger(CapitalOneSparkTransactionTest.class.getName());

  @Test
  public void testLombok() throws Throwable {
    final String __functionName = "testLombok";
    final String __className = this.getClass().getName();
    if (log.isLoggable(Level.FINER))
      log.entering(__className, __functionName);
    try {
      final CapitalOneSparkTransaction tx = new CapitalOneSparkTransaction();
      tx.setAccountNumber("xyz");
      assertEquals("xyz", tx.getAccountNumber());
    } finally {
      if (log.isLoggable(Level.FINER))
        log.exiting(__className, __functionName);
    }
  }

}
