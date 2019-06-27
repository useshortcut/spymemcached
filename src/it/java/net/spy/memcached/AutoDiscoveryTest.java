/**
 * Copyright (C) 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved. 
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package net.spy.memcached;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

import net.spy.memcached.categories.StandardTests;
import net.spy.memcached.ops.ConfigurationType;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(StandardTests.class)
public class AutoDiscoveryTest {
  private ConnectionFactory cf;
  private static final long POLLING_INTERVAL = 3000; //milliseconds 
  private static final int MAX_RETRY = 20;
  
  @Before
  public void setUp() throws Exception {
    cf = new DefaultConnectionFactory() {
      @Override
      public ClientMode getClientMode() {
        return TestConfig.getInstance().getClientMode();
      }
      
      @Override
      public long getOperationTimeout() {
        return 50;
      }

      @Override
      public FailureMode getFailureMode() {
        return FailureMode.Retry;
      }
      
      @Override
      public long getDynamicModePollingInterval(){
        return POLLING_INTERVAL;
      }
    };
  }

  @Test
  public void addNodeTest() throws IOException, InterruptedException{
    List<InetSocketAddress> addrs1 = AddrUtil.getAddresses(TestConfig.IPV4_ADDR + ":22211");
    MemcachedClient staticClient1 = new MemcachedClient(addrs1);
    List<InetSocketAddress> addrs2 = AddrUtil.getAddresses(TestConfig.IPV4_ADDR + ":22212");
    MemcachedClient staticClient2 = new MemcachedClient(addrs2);
    if(TestConfig.getInstance().getEngineType().isSetConfigSupported()) {
        staticClient1.setConfig(addrs1.get(0), ConfigurationType.CLUSTER, "1\n" + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211");
        staticClient2.setConfig(addrs2.get(0), ConfigurationType.CLUSTER, "1\n" + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211");
    } else {
        staticClient1.set(ConfigurationType.CLUSTER.getValueWithNameSpace(), 0, "1\n" + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211");
        staticClient2.set(ConfigurationType.CLUSTER.getValueWithNameSpace(), 0, "1\n" + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211");
    }
    MemcachedClient dynamicClient = new MemcachedClient(cf, AddrUtil.getAddresses(TestConfig.IPV4_ADDR    + ":22211"));
    assertEquals(1, retry(dynamicClient, 1));
    
    if(TestConfig.getInstance().getEngineType().isSetConfigSupported()) {
        staticClient1.setConfig(addrs1.get(0), ConfigurationType.CLUSTER, "2\n" 
                                + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211"
                                + " localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22212");
    
        staticClient2.setConfig(addrs2.get(0), ConfigurationType.CLUSTER, "2\n" 
                                + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211"
                                + " localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22212");
    } else {
    	staticClient1.set(ConfigurationType.CLUSTER.getValueWithNameSpace(), 0, "2\n"
                          + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211"
                          + " localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22212");
    	staticClient2.set(ConfigurationType.CLUSTER.getValueWithNameSpace(), 0, "2\n"
                + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211"
                + " localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22212");
    }

    assertEquals(2, retry(dynamicClient, 2));
    
    staticClient1.shutdown();
    staticClient2.shutdown();
    dynamicClient.shutdown();
  }
  
  @Test
  public void removeNodeTest() throws IOException, InterruptedException{
    List<InetSocketAddress> addrs1 = AddrUtil.getAddresses(TestConfig.IPV4_ADDR + ":22211");
    MemcachedClient staticClient1 = new MemcachedClient(addrs1);
    List<InetSocketAddress> addrs2 = AddrUtil.getAddresses(TestConfig.IPV4_ADDR + ":22212");
    MemcachedClient staticClient2 = new MemcachedClient(addrs2);

    if(TestConfig.getInstance().getEngineType().isSetConfigSupported()) {
        staticClient1.setConfig(addrs1.get(0), ConfigurationType.CLUSTER, "1\n" 
            + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211"
            + " localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22212");

        staticClient2.setConfig(addrs2.get(0), ConfigurationType.CLUSTER, "1\n" 
            + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211"
            + " localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22212");
    } else {
        staticClient1.set(ConfigurationType.CLUSTER.getValueWithNameSpace(), 0, "1\n" 
                + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211"
                + " localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22212");

        staticClient2.set(ConfigurationType.CLUSTER.getValueWithNameSpace(), 0, "1\n" 
                + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211"
                + " localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22212");
    }
    MemcachedClient dynamicClient = new MemcachedClient(cf, AddrUtil.getAddresses(TestConfig.IPV4_ADDR    + ":22211"));
    assertEquals(2, retry(dynamicClient, 2));
    
    if(TestConfig.getInstance().getEngineType().isSetConfigSupported()) {
        staticClient1.setConfig(addrs1.get(0), ConfigurationType.CLUSTER, "2\n" + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211");
        staticClient2.setConfig(addrs2.get(0), ConfigurationType.CLUSTER, "2\n" + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211");
    } else {
        staticClient1.set(ConfigurationType.CLUSTER.getValueWithNameSpace(), 0, "2\n" + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211");
        staticClient2.set(ConfigurationType.CLUSTER.getValueWithNameSpace(), 0, "2\n" + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211");
    }
    assertEquals(1, retry(dynamicClient, 1));
    
    staticClient1.shutdown();
    staticClient2.shutdown();
    dynamicClient.shutdown();
  }
  
  @Test
  public void replaceNodeTest() throws IOException, InterruptedException{
    List<InetSocketAddress> addrs = AddrUtil.getAddresses(TestConfig.IPV4_ADDR + ":22211");
    MemcachedClient staticClient = new MemcachedClient(addrs);
    if(TestConfig.getInstance().getEngineType().isSetConfigSupported()) {
        staticClient.setConfig(addrs.get(0), ConfigurationType.CLUSTER, "1\n" + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211");
    } else {
        staticClient.set(ConfigurationType.CLUSTER.getValueWithNameSpace(), 0, "1\n" + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211");
    }
    MemcachedClient dynamicClient = new MemcachedClient(cf, AddrUtil.getAddresses(TestConfig.IPV4_ADDR    + ":22211"));
    assertEquals(1, retry(dynamicClient, 1));
    
    InetAddress addr = InetAddress.getLocalHost();
    assertFalse(TestConfig.IPV4_ADDR.equals(addr.getHostAddress()));
    if(TestConfig.getInstance().getEngineType().isSetConfigSupported()) {
        staticClient.setConfig(addrs.get(0), ConfigurationType.CLUSTER, "2\n" + "localhost.localdomain|" + addr.getHostAddress() + "|" + "22211");
    } else {
        staticClient.set(ConfigurationType.CLUSTER.getValueWithNameSpace(), 0, "2\n" + "localhost.localdomain|" + addr.getHostAddress() + "|" + "22211");
    }

    assertEquals(addr.getHostAddress(), retry(dynamicClient, addr.getHostAddress()));
    assertEquals(1, retry(dynamicClient, 1));
    
    staticClient.shutdown();
    dynamicClient.shutdown();
  }

  @Test
  public void staleConfigTest() throws IOException, InterruptedException{
    List<InetSocketAddress> addrs1 = AddrUtil.getAddresses(TestConfig.IPV4_ADDR + ":22211");
    MemcachedClient staticClient1 = new MemcachedClient(addrs1);
    List<InetSocketAddress> addrs2 = AddrUtil.getAddresses(TestConfig.IPV4_ADDR + ":22212");
    MemcachedClient staticClient2 = new MemcachedClient(addrs2);

    if(TestConfig.getInstance().getEngineType().isSetConfigSupported()) {
        staticClient1.setConfig(addrs1.get(0), ConfigurationType.CLUSTER, "2\n" 
            + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211"
            + " localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22212");

        staticClient2.setConfig(addrs2.get(0), ConfigurationType.CLUSTER, "2\n" 
            + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211"
            + " localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22212");
    } else {
        staticClient1.set(ConfigurationType.CLUSTER.getValueWithNameSpace(), 0, "2\n" 
                + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211"
                + " localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22212");

        staticClient2.set(ConfigurationType.CLUSTER.getValueWithNameSpace(), 0, "2\n" 
                + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211"
                + " localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22212");
    }
    MemcachedClient dynamicClient = new MemcachedClient(cf, AddrUtil.getAddresses(TestConfig.IPV4_ADDR    + ":22211"));
    assertEquals(2, retry(dynamicClient, 2));
    
    if(TestConfig.getInstance().getEngineType().isSetConfigSupported()) {
        staticClient1.setConfig(addrs1.get(0), ConfigurationType.CLUSTER, "1\n" + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211");
        staticClient2.setConfig(addrs2.get(0), ConfigurationType.CLUSTER, "1\n" + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211");
    } else {
        staticClient1.set(ConfigurationType.CLUSTER.getValueWithNameSpace(), 0, "1\n" + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211");
        staticClient2.set(ConfigurationType.CLUSTER.getValueWithNameSpace(), 0, "1\n" + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "22211");
    }
    assertEquals(2, retry(dynamicClient, 2));
    
    staticClient1.shutdown();
    staticClient2.shutdown();
    dynamicClient.shutdown();
  }

  private int retry(MemcachedClient client, int expected) throws InterruptedException {
	  Thread.sleep(2*POLLING_INTERVAL);
      for (int retry = 0; retry < MAX_RETRY; retry++) {
          if (client.getAvailableNodeEndPoints().size() == expected)
              break;
          Thread.sleep(2*POLLING_INTERVAL);
      }
      return client.getAvailableNodeEndPoints().size();
  }
  
  private String retry(MemcachedClient client, String expected) throws InterruptedException {
	  Thread.sleep(2*POLLING_INTERVAL);
      for (int retry = 0; retry < MAX_RETRY; retry++) {
          if (client.getAvailableNodeEndPoints().iterator().next().getIpAddress().equals(expected))
              break;
          Thread.sleep(2*POLLING_INTERVAL);
      }
      return client.getAvailableNodeEndPoints().iterator().next().getIpAddress();
  }

}
