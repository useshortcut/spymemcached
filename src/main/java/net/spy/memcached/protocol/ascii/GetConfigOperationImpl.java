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
