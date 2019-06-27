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
 * SPDX-License-Identifier: Apache-2.0
 */

package net.spy.memcached;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.spy.memcached.categories.StandardTests;
import net.spy.memcached.config.NodeEndPoint;
import net.spy.memcached.ops.ConfigurationType;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Base class for cancellation tests.
 */
@Category(StandardTests.class)
public abstract class CancellationBaseCase extends ClientBaseCase {

  @Override
public void tearDown() throws Exception {
    // override teardown to avoid the flush phase
    client.shutdown();
  }

  @Override
  protected void initClient(ConnectionFactory cf) throws Exception {
    if(TestConfig.getInstance().getClientMode() == ClientMode.Dynamic){
      List<InetSocketAddress> addrs = AddrUtil.getAddresses(TestConfig.IPV4_ADDR+ ":11212");
      MemcachedClient staticClient = new MemcachedClient(addrs);

      if(TestConfig.getInstance().getEngineType().isSetConfigSupported()) {
          staticClient.setConfig(addrs.get(0), ConfigurationType.CLUSTER, "1\n" + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "11212");
          client = new MemcachedClient(cf, AddrUtil.getAddresses(TestConfig.IPV4_ADDR    + ":11212"));
          staticClient.setConfig(addrs.get(0), ConfigurationType.CLUSTER, "2\nlocalhost.localdomain|" + TestConfig.IPV4_ADDR + "|64213");
      } else {
    	  staticClient.set(ConfigurationType.CLUSTER.getValueWithNameSpace(), 0, "1\n" + "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "11212");
          client = new MemcachedClient(cf, AddrUtil.getAddresses(TestConfig.IPV4_ADDR    + ":11212"));
          staticClient.set(ConfigurationType.CLUSTER.getValueWithNameSpace(), 0, "2\nlocalhost.localdomain|" + TestConfig.IPV4_ADDR + "|64213");
      }
      //Add a delay to allow time for dynamic mode client to pickup the config.
      Thread.sleep(10000);
    } else {
      client = new MemcachedClient(cf, AddrUtil.getAddresses(TestConfig.IPV4_ADDR
          + ":64213")); 
    }
  }

  private void tryCancellation(Future<?> f) throws Exception {
    f.cancel(true);
    assertTrue(f.isCancelled());
    assertTrue(f.isDone());
    try {
      Object o = f.get();
      fail("Expected cancellation, got " + o);
    } catch (ExecutionException e) {
      assertTrue(e.getCause() instanceof RuntimeException);
      assertEquals("Cancelled", e.getCause().getMessage());
    }
  }

  @Test
  public void testAvailableServers() {
    client.asyncGet("x");
    assertEquals(Collections.emptyList(), client.getAvailableServers());
  }

  @Test
  public void testUnavailableServers() {
    client.asyncGet("x");
    Collection<SocketAddress> unavailableServers = client.getUnavailableServers();
    
    assertTrue(unavailableServers.size() == 1);
    
    SocketAddress sa = unavailableServers.iterator().next();
    String addrString = String.valueOf(sa);
    //Exact string match is not used as the host name can have multiple names such as "localhost", "localhost:localdomain" or even blank.
    assertTrue(addrString.contains(TestConfig.IPV4_ADDR + ":64213"));
    
  }

  private void tryTimeout(Future<?> f) throws Exception {
    try {
      Object o = f.get(10, TimeUnit.MILLISECONDS);
      fail("Expected timeout, got " + o);
    } catch (TimeoutException e) {
      // expected
    }
  }

  protected void tryTestSequence(Future<?> f) throws Exception {
    tryTimeout(f);
    tryCancellation(f);
  }

  @Test
  public void testAsyncGetCancellation() throws Exception {
    tryTestSequence(client.asyncGet("k"));
  }

  @Test
  public void testAsyncGetsCancellation() throws Exception {
    tryTestSequence(client.asyncGets("k"));
  }

  @Test
  public void testAsyncGetBulkCancellationCollection() throws Exception {
    tryTestSequence(client.asyncGetBulk(Arrays.asList("k", "k2")));
  }

  @Test
  public void testAsyncGetBulkCancellationVararg() throws Exception {
    tryTestSequence(client.asyncGetBulk("k", "k2"));
  }

  @Test
  public void testDeleteCancellation() throws Exception {
    tryTestSequence(client.delete("x"));
  }

  @Test
  public void testReplaceCancellation() throws Exception {
    tryTestSequence(client.replace("x", 3, "y"));
  }

  @Test
  public void testAddCancellation() throws Exception {
    tryTestSequence(client.add("x", 3, "y"));
  }

  @Test
  public void testSetCancellation() throws Exception {
    tryTestSequence(client.set("x", 3, "y"));
  }

  @Test
  public void testCASCancellation() throws Exception {
    tryTestSequence(client.asyncCAS("x", 3, "y"));
  }
  
  @Test
  public void testflushCancellation() throws Exception {
    String current_config = null;
    Collection<NodeEndPoint> endpoints = new ArrayList<NodeEndPoint>();
    if(TestConfig.getInstance().getClientMode().equals(ClientMode.Dynamic) &&
       !TestConfig.getInstance().getEngineType().isSetConfigSupported()) {
    	current_config = getCurrentConfigAndClusterEndpoints(client, endpoints);
    }
    tryTestSequence(client.flush());
    if(TestConfig.getInstance().getClientMode().equals(ClientMode.Dynamic) &&
       !TestConfig.getInstance().getEngineType().isSetConfigSupported()) {
    	restoreClusterConfig(current_config, endpoints);
    }
  }

  @Test
  public void testDelayedflushCancellation() throws Exception {
    String current_config = null;
    Collection<NodeEndPoint> endpoints = new ArrayList<NodeEndPoint>();
    if(TestConfig.getInstance().getClientMode().equals(ClientMode.Dynamic) &&
       !TestConfig.getInstance().getEngineType().isSetConfigSupported()) {
    	current_config = getCurrentConfigAndClusterEndpoints(client, endpoints);
    }
    tryTestSequence(client.flush(3));
    if(TestConfig.getInstance().getClientMode().equals(ClientMode.Dynamic) &&
       !TestConfig.getInstance().getEngineType().isSetConfigSupported()) {
    	restoreClusterConfig(current_config, endpoints);
    }
  }
}
