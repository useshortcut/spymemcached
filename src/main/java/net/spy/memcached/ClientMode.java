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
package net.spy.memcached;

/**
 * The modes in which the client can operate. 
 */
public enum ClientMode {

  /**
   * In Static Client mode, the set of endpoints specified during initialization is used throughout the lifetime of the client object.
   */
  Static,
  /**
   * In Dynamic Client mode, the set of cache node endpoints and any updates to it is dynamically managed in this mode. 
   * The client is initialized with a configuration endpoint. The client will periodically learn about the cache nodes in the
   * cluster.
   */
  Dynamic
}
