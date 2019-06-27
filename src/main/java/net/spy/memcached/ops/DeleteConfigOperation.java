/**
 * Copyright (C) 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved. 
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package net.spy.memcached.ops;

/**
 * Deletion operation for config.
 */
public interface DeleteConfigOperation extends Operation {
  /**
   * Get the type to be deleted. 
   */
  ConfigurationType getType();
}
