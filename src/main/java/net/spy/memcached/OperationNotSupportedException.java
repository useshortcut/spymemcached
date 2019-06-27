/**
 * Copyright (C) 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved. 
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package net.spy.memcached;

/**
 * Thrown by {@link MemcachedClient} when the operation is not supported. 
 *
 */
public class OperationNotSupportedException extends RuntimeException {

  private static final long serialVersionUID = 8270943033252267083L;

  public OperationNotSupportedException(String message) {
    super(message);
  }

  public OperationNotSupportedException(String message, Throwable cause) {
    super(message, cause);
  }
}
