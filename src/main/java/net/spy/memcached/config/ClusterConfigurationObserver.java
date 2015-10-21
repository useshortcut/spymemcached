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
package net.spy.memcached.config;

/**
 * Observer for changes in the ConfigurationType.CLUSTER type of config in the cluster.
 *
 */
public interface ClusterConfigurationObserver extends ConfigurationObserver {
  
  /**
   * The publisher calls all the subscribers through this method. This is invoked whenever
   * there is change in cluster configuration data.
   * @param clusterConfiguration - The parameter contains the latest information about the cluster. 
   */
  public void notifyUpdate(ClusterConfiguration clusterConfiguration);

}
