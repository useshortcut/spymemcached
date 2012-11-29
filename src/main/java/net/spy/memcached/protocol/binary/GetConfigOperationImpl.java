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
import net.spy.memcached.ops.GetConfigOperation;

/**
 * Binary protocol implementation for "config get <key>".
 * 
 */
class GetConfigOperationImpl extends OperationImpl implements GetConfigOperation {

  protected final ConfigurationType type;
  
  static final byte CONFIG_GET_CMD = 0x60;

  /**
   * Length of the extra header stuff for a GET CONFIG response.
   * 4 bytes is used for flags.
   */
  static final int EXTRA_HDR_LEN = 4;

  public GetConfigOperationImpl(ConfigurationType type, GetConfigOperation.Callback cb) {
    super(CONFIG_GET_CMD, generateOpaque(), cb);
    this.type = type;
  }

  @Override
  public void initialize() {
    prepareBuffer(type.getValue(), 0, EMPTY_BYTES);
  }

  @Override
  protected void decodePayload(byte[] pl) {
    final int flags = decodeInt(pl, 0);
    final byte[] data = new byte[pl.length - EXTRA_HDR_LEN];
    System.arraycopy(pl, EXTRA_HDR_LEN, data, 0, pl.length - EXTRA_HDR_LEN);
    GetConfigOperation.Callback gcb = (GetConfigOperation.Callback) getCallback();
    gcb.gotData(type, flags, data);
    getCallback().receivedStatus(STATUS_OK);
  }

  @Override
  public ConfigurationType getType() {
    return type;
  }

  @Override
  public String toString() {
    return super.toString() + " Type: " + type.getValue();
  }
}
