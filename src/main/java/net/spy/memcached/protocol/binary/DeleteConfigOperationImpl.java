/**
 * Copyright (C) 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved. 
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package net.spy.memcached.protocol.binary;

import net.spy.memcached.ops.ConfigurationType;
import net.spy.memcached.ops.DeleteConfigOperation;
import net.spy.memcached.ops.OperationCallback;

/**
 * Binary protocol implementation for config delete.
 * 
 */
class DeleteConfigOperationImpl extends OperationImpl implements
    DeleteConfigOperation {

  private static final byte CMD = 0x66;
  private final ConfigurationType type;
  
  public DeleteConfigOperationImpl(ConfigurationType type, OperationCallback cb) {
    super(CMD, generateOpaque(), cb);
    this.type = type;
  }

  @Override
  public void initialize() {
    prepareBuffer(type.getValue(), 0, EMPTY_BYTES);
  }
  
  @Override
  public ConfigurationType getType() {
    return type;
  }

  @Override
  public String toString() {
    return super.toString() + " Type: " + type;
  }
}
