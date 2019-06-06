/**
 * Copyright (C) 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved. 
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package net.spy.memcached.protocol.ascii;

import java.nio.ByteBuffer;

import net.spy.memcached.KeyUtil;
import net.spy.memcached.ops.ConfigurationType;
import net.spy.memcached.ops.DeleteConfigOperation;
import net.spy.memcached.ops.OperationCallback;
import net.spy.memcached.ops.OperationState;
import net.spy.memcached.ops.OperationStatus;

/**
 * Operation to delete the specified config types from the cache node.
 */
final class DeleteConfigOperationImpl extends OperationImpl implements
    DeleteConfigOperation {

  private static final String CMD = "config delete";
  private static final int OVERHEAD = 32;

  private static final OperationStatus DELETED = new OperationStatus(true,
      "DELETED");
  private static final OperationStatus NOT_FOUND = new OperationStatus(false,
      "NOT_FOUND");

  private final ConfigurationType type;

  public DeleteConfigOperationImpl(ConfigurationType type, OperationCallback cb) {
    super(cb);
    this.type = type;
  }

  @Override
  public void handleLine(String line) {
    getLogger().debug("Delete of %s returned %s", type, line);
    getCallback().receivedStatus(matchStatus(line, DELETED, NOT_FOUND));
    transitionState(OperationState.COMPLETE);
  }

  @Override
  public void initialize() {
    ByteBuffer b = ByteBuffer.allocate(KeyUtil.getKeyBytes(type.getValue()).length
        + OVERHEAD);
    setArguments(b, CMD, type.getValue());
    b.flip();
    setBuffer(b);
  }

  @Override
  public ConfigurationType getType() {
    return type;
  }
  
  @Override
  public String toString() {
    return "Cmd: delete config type: " + type;
  }
}
