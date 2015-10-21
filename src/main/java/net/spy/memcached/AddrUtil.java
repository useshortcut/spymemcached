/**
 * Copyright (C) 2006-2009 Dustin Sallings
 * Copyright (C) 2009-2011 Couchbase, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALING
 * IN THE SOFTWARE.
 * 
 * 
 * Portions Copyright (C) 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.spy.memcached.config.ClusterConfiguration;
import net.spy.memcached.config.NodeEndPoint;

/**
 * Convenience utilities for simplifying common address parsing.
 */
public final class AddrUtil {
  public static final char HOST_CONFIG_DELIMITER = '|';

  private AddrUtil() {
    // Empty
  }

  /**
   * Split a string containing whitespace or comma separated host or IP
   * addresses and port numbers of the form "host:port host2:port" or
   * "host:port, host2:port" into a List of InetSocketAddress instances suitable
   * for instantiating a MemcachedClient.
   *
   * Note that colon-delimited IPv6 is also supported. For example: ::1:11211
   */
  public static List<InetSocketAddress> getAddresses(String s) {
    if (s == null) {
      throw new NullPointerException("Null host list");
    }
    if (s.trim().equals("")) {
      throw new IllegalArgumentException("No hosts in list:  ``" + s + "''");
    }
    ArrayList<InetSocketAddress> addrs = new ArrayList<InetSocketAddress>();

    for (String hoststuff : s.split("(?:\\s|,)+")) {
      if (hoststuff.equals("")) {
        continue;
      }

      int finalColon = hoststuff.lastIndexOf(':');
      if (finalColon < 1) {
        throw new IllegalArgumentException("Invalid server ``" + hoststuff
            + "'' in list:  " + s);
      }
      String hostPart = hoststuff.substring(0, finalColon);
      String portNum = hoststuff.substring(finalColon + 1);

      addrs.add(new InetSocketAddress(hostPart, Integer.parseInt(portNum)));
    }
    assert !addrs.isEmpty() : "No addrs found";
    return addrs;
  }

  public static List<InetSocketAddress> getAddresses(List<String> servers) {
    ArrayList<InetSocketAddress> addrs =
        new ArrayList<InetSocketAddress>(servers.size());
    for (String server : servers) {
      int finalColon = server.lastIndexOf(':');
      if (finalColon < 1) {
        throw new IllegalArgumentException("Invalid server ``" + server
            + "'' in list:  " + server);
      }
      String hostPart = server.substring(0, finalColon);
      String portNum = server.substring(finalColon + 1);

      addrs.add(new InetSocketAddress(hostPart, Integer.parseInt(portNum)));
    }
    if (addrs.isEmpty()) {
      // servers was passed in empty, and shouldn't have been
      throw new IllegalArgumentException("servers cannot be empty");
    }
    return addrs;
  }

  public static List<InetSocketAddress>
  getAddressesFromURL(List<URL> servers) {
    ArrayList<InetSocketAddress> addrs =
      new ArrayList<InetSocketAddress>(servers.size());
    for (URL server : servers) {
      addrs.add(new InetSocketAddress(server.getHost(), server.getPort()));
    }
    return addrs;
  }
  
  /**
   * Parse response from getConfig for cluster type.
   * version number
   * hostname1|ipaddress1|port hostname2|ipaddress2|port
   *  
   * returns the ClusterConfiguration object which contains the parsed results. 
   */
  public static ClusterConfiguration parseClusterTypeConfiguration(String configurationResponse) {
    if (configurationResponse == null) {
      throw new NullPointerException("Null configuration");
    }
    if (configurationResponse.trim().equals("")) {
      throw new IllegalArgumentException("No configuration in the response:" + configurationResponse);
    }
    String[] lines = configurationResponse.trim().split("(?:\\r?\\n)");
    if(lines == null || lines.length != 2) {
      throw new IllegalArgumentException("Incorrect response format. Response:" + configurationResponse);
    }
    
    String versionString = lines[0].trim();
    if(versionString.equals("")){
      throw new IllegalArgumentException("Version number is missing. Response:" + configurationResponse);
    }
    
    long versionNumber = Long.parseLong(versionString);
    
    String hostList = lines[1].trim();
    if (hostList.equals("")) {
      throw new IllegalArgumentException("Empty host list in the response:" + configurationResponse);
    }
    
    List<NodeEndPoint> endPoints = new ArrayList<NodeEndPoint>();

    for (String hostDetails : hostList.split("(?:\\s)+")) {
      if (hostDetails.equals("")) {
        continue;
      }

      int firstDelimiter = hostDetails.indexOf(HOST_CONFIG_DELIMITER);
      int secondDelimiter = hostDetails.lastIndexOf(HOST_CONFIG_DELIMITER);
      if (firstDelimiter < 1 || firstDelimiter == secondDelimiter) {
        throw new IllegalArgumentException("Invalid server ''" + hostDetails
            + "'' in response:  " + configurationResponse);
      }
      String hostName = hostDetails.substring(0, firstDelimiter).trim();
      String ipAddress = hostDetails.substring(firstDelimiter+1, secondDelimiter).trim();
      String portNum = hostDetails.substring(secondDelimiter + 1).trim();
      int port = Integer.parseInt(portNum);
      
      NodeEndPoint endPoint = new NodeEndPoint(hostName, ipAddress, port);
      endPoints.add(endPoint);
    }
    assert !endPoints.isEmpty() : "No endpoints found";
    
    ClusterConfiguration config = new ClusterConfiguration(versionNumber, endPoints);
    
    return config;
  }

}
