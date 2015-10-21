/**
 * Copyright (C) 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved. 
 *
 * Licensed under the Amazon Software License (the "License"). You may not use this 
 * file except in compliance with the License. A copy of the License is located at
 *  http://aws.amazon.com/asl/
 * or in the "license" file accompanying this file. This file is distributed on 
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or
 * implied. See the License for the specific language governing permissions and 
 * limitations under the License. 
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
