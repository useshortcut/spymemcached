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

import java.util.ArrayList;
import java.util.List;
import java.net.InetSocketAddress;

/**
 * A type to capture the configuration version number and the server list 
 * contained the response for getConfig API for "cluster" configuration type.
 */
public class ClusterConfiguration {

  private long configVersion;
  private List<NodeEndPoint> cacheNodeEndPoints;
  
  public ClusterConfiguration(final long configVersion, final List<NodeEndPoint> cacheNodeEndPoints){
    this.configVersion = configVersion;
    this.cacheNodeEndPoints = cacheNodeEndPoints;
  }
  
  public long getConfigVersion() {
    return configVersion;
  }

  public List<NodeEndPoint> getCacheNodeEndPoints() {
    return cacheNodeEndPoints;
  }
  
  public List<InetSocketAddress> getInetSocketAddresses(){
    List<InetSocketAddress> addrs = new ArrayList<InetSocketAddress>(cacheNodeEndPoints.size());
    for(NodeEndPoint endPoint : cacheNodeEndPoints){
      addrs.add(endPoint.getInetSocketAddress());
    }
    
    return addrs;
  }
  
  @Override
  public String toString(){
    StringBuilder nodeList = new StringBuilder("Version:" + configVersion + "  CacheNode List:");
    for(NodeEndPoint endPoint : cacheNodeEndPoints){
      nodeList.append(" " + endPoint.getHostName() + ":" + endPoint.getPort());
    }
    
    return nodeList.toString();
  }
}
