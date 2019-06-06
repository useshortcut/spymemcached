/**
 * Copyright (C) 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved. 
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package net.spy.memcached.ops;


/**
 * "config get" Operation.
 */
public interface GetConfigOperation extends Operation {

  /**
   * Get the type used for fetching config. 
   */
  ConfigurationType getType();
  
  /**
   * Operation callback for the getConfig request.
   */
  interface Callback extends OperationCallback {
    /**
     * Callback for result from getConfig.
     *
     * @param type the type that was retrieved
     * @param flags the flags for this value
     * @param data the data stored under this type
     */
    void gotData(ConfigurationType type, int flags, byte[] data);
  }
}
