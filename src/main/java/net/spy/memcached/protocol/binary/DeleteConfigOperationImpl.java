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
