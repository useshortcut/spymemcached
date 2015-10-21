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
package net.spy.memcached;

/**
 * Thrown by {@link MemcachedClient} when the operation is not supported. 
 *
 */
public class OperationNotSupportedException extends RuntimeException {

  private static final long serialVersionUID = 8270943033252267083L;

  public OperationNotSupportedException(String message) {
    super(message);
  }

  public OperationNotSupportedException(String message, Throwable cause) {
    super(message, cause);
  }
}
