/**
 * Copyright (C) 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved. 
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package net.spy.memcached.internal;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.spy.memcached.ops.ConfigurationType;
import net.spy.memcached.ops.Operation;
import net.spy.memcached.ops.OperationStatus;

/**
 * Future returned for GetConfig operation.
 *
 * Not intended for general use.
 *
 * @param <T> Type of object returned from the getconfig
 */
public class GetConfigFuture<T> implements Future<T> {

  private final OperationFuture<Future<T>> rv;

  public GetConfigFuture(CountDownLatch l, long opTimeout, ConfigurationType type,
		  ExecutorService service) {
    this.rv = new OperationFuture<Future<T>>(type.getValue(), l, opTimeout, service);
  }

  public boolean cancel(boolean ign) {
    return rv.cancel(ign);
  }

  public T get() throws InterruptedException, ExecutionException {
    Future<T> v = rv.get();
    return v == null ? null : v.get();
  }

  public T get(long duration, TimeUnit units) throws InterruptedException,
      TimeoutException, ExecutionException {
    Future<T> v = rv.get(duration, units);
    return v == null ? null : v.get();
  }

  public OperationStatus getStatus() {
    return rv.getStatus();
  }

  public void set(Future<T> d, OperationStatus s) {
    rv.set(d, s);
  }

  public void setOperation(Operation to) {
    rv.setOperation(to);
  }

  public boolean isCancelled() {
    return rv.isCancelled();
  }

  public boolean isDone() {
    return rv.isDone();
  }
}
