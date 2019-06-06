/**
 * Copyright (C) 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved. 
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package net.spy.memcached.protocol.binary;

import net.spy.memcached.ops.ConfigurationType;
import net.spy.memcached.ops.OperationCallback;
import net.spy.memcached.ops.SetConfigOperation;

/**
 * Binary protocol implementation for "config set".
 * 
 */
class SetConfigOperationImpl extends OperationImpl implements
    SetConfigOperation{

  private static final byte SET_CONFIG = 0x64;

  private final ConfigurationType type;
  private final int flags;
  private final byte[] data;

  public SetConfigOperationImpl(ConfigurationType type, int f, byte[] d, OperationCallback cb) {
    super(SET_CONFIG, generateOpaque(), cb);
    this.type = type;
    flags = f;
    data = d;
  }

  @Override
  public void initialize() {
    prepareBuffer(type.getValue(), 0, data, flags);
  }

  public int getFlags() {
    return flags;
  }

  public byte[] getData() {
    return data;
  }

  @Override
  public ConfigurationType getType() {
    return type;
  }
  
  @Override
  public String toString() {
    return super.toString() + " Type: " + type + " Flags: " + flags + " Data Length: " + data.length;
  }
}
