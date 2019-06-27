/**
 * Copyright (C) 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved. 
 *
 * SPDX-License-Identifier: Apache-2.0
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
