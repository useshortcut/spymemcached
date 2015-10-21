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
