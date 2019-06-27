/**
 * Copyright (C) 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved. 
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package net.spy.memcached.testsuites;

import java.net.InetSocketAddress;
import java.util.List;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.AsciiCancellationTest;
import net.spy.memcached.AsciiClientTest;
import net.spy.memcached.AutoDiscoveryTest;
import net.spy.memcached.BinaryCancellationTest;
import net.spy.memcached.BinaryClientTest;
import net.spy.memcached.CASMutatorTest;
import net.spy.memcached.CancelFailureModeTest;
import net.spy.memcached.ClientMode;
import net.spy.memcached.LongClientTest;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.MemcachedClientConstructorTest;
import net.spy.memcached.ObserverTest;
import net.spy.memcached.QueueOverflowTest;
import net.spy.memcached.RedistributeFailureModeTest;
import net.spy.memcached.TestConfig;
import net.spy.memcached.TimeoutNowriteTest;
import net.spy.memcached.TimeoutTest;
import net.spy.memcached.categories.StandardTests;
import net.spy.memcached.ops.ConfigurationType;
import net.spy.memcached.util.ConnectionUtil;
import net.spy.memcached.util.ConnectionUtilLinuxImpl;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Categories.class)  
@IncludeCategory(StandardTests.class)
@SuiteClasses({
  AutoDiscoveryTest.class,
  AsciiClientTest.class,
  AsciiCancellationTest.class,
  BinaryClientTest.class,
  BinaryCancellationTest.class,
  CancelFailureModeTest.class,
  CASMutatorTest.class,
  LongClientTest.class,
  MemcachedClientConstructorTest.class,
  ObserverTest.class,
  QueueOverflowTest.class,
  RedistributeFailureModeTest.class,
  TimeoutNowriteTest.class,
  TimeoutTest.class
  })
public class DynamicModeTestSuite {

  public static ConnectionUtil connUtil = new ConnectionUtilLinuxImpl();

  /**
   * Start connections to local memcached ports. This is executed before all tests 
   * in the suite. 
   */
  @BeforeClass
  public static void setUpClass() throws Exception {
    System.out.println("Master Set Up.");
    TestConfig.initialize(ClientMode.Dynamic);
    startConnections();
    List<InetSocketAddress> addrs = AddrUtil.getAddresses(TestConfig.IPV4_ADDR
        + ":" + TestConfig.PORT_NUMBER);
    MemcachedClient client = new MemcachedClient(addrs);
    client.setConfig(addrs.get(0), ConfigurationType.CLUSTER, "1\n" + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + TestConfig.PORT_NUMBER);
  }


  /**
   * Kill connection to local memcached, finish tear down. This is executed after all
   * tests are completed in the suite. 
   */
  @AfterClass
  public static void tearDownClass() throws Exception {
    killConnections();
    System.out.println("Master Tear Down.");
  }

  /**
   * Starts local memcached servers in multiple ports.
   * @throws Exception 
   */
  private static void startConnections() throws Exception {
    connUtil.addLocalMemcachedServer(11200, 11201, 11211, 11212, 22211, 22212);
  }

  /**
   * Kills local memcached connections created for this test.
   */
  private static void killConnections() {
    connUtil.removeLocalMemcachedServer(TestConfig.MEMCACHED_NAME,11200, 11201, 11211, 11212, 22211, 22212);
  }

}
