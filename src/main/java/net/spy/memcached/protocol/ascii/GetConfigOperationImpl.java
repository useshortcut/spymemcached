/**
 * Copyright (C) 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved. 
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package net.spy.memcached.protocol.ascii;

import java.util.Collections;

import net.spy.memcached.ops.ConfigurationType;
import net.spy.memcached.ops.GetConfigOperation;

/**
 * Operation for retrieving configuration data.
 */
class GetConfigOperationImpl extends BaseGetConfigOperationImpl implements GetConfigOperation {

  private static final String CMD = "config get";
  
  public GetConfigOperationImpl(ConfigurationType type, GetConfigOperation.Callback c) {
    super(CMD, c, Collections.singleton(type));
  }

  @Override
  public ConfigurationType getType() {
    return types.iterator().next();
  }
}
