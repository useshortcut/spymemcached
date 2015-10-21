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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import net.spy.memcached.ops.ConfigurationType;

/**
 * Utilities for processing config types.
 */
public final class ConfigurationTypeUtil {

  private ConfigurationTypeUtil() {
    // Empty
  }

  /**
   * Get the bytes for a config type.
   *
   * @param type the config type
   * @return the bytes
   */
  public static byte[] getTypeBytes(ConfigurationType type) {
    try {
      String value = type.getValue();
      return value.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get the types in byte form for all of the config types.
   *
   * @param types a collection of types
   * @return return a collection of the byte representations of config types
   */
  public static Collection<byte[]> getTypeBytes(Collection<ConfigurationType> types) {
    Collection<byte[]> rv = new ArrayList<byte[]>(types.size());
    for (ConfigurationType type : types) {
      rv.add(getTypeBytes(type));
    }
    return rv;
  }
}
