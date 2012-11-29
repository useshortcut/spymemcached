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
