/**
 * Copyright (C) 2012-2012 Amazon.com, Inc. or its affiliates. All Rights Reserved. 
 *
 * Licensed under the Amazon Software License (the "License"). You may not use this 
 * file except in compliance with the License. A copy of the License is located at
 *  http://aws.amazon.com/asl/
 * or in the "license" file accompanying this file. This file is distributed on 
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or
 * implied. See the License for the specific language governing permissions and 
 * limitations under the License. 
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
