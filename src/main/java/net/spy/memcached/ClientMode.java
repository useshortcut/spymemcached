/**
 * Copyright (C) 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved. 
 *
 * SPDX-License-Identifier: Apache-2.0
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
