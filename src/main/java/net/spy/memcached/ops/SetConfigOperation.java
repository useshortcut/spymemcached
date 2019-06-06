/**
 * Copyright (C) 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved. 
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package net.spy.memcached.ops;

/**
 * Operation that represents config set.
 */
public interface SetConfigOperation extends Operation {

  /**
   * Get the type used for setting the config.
   */
  ConfigurationType getType();
  
  /**
   * Get the flags to be set.
   */
  int getFlags();

  /**
   * Get the bytes to be set during this operation.
   *
   * <p>
   * Note, this returns an exact reference to the bytes and the data
   * <em>must not</em> be modified.
   * </p>
   */
  byte[] getData();
}
