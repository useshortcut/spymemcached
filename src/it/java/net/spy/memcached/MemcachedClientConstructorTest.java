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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.spy.memcached.categories.StandardTests;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test the various memcached client constructors.
 */
@Category(StandardTests.class)
public class MemcachedClientConstructorTest {

  private MemcachedClient client = null;

  @Before
  public void tearDown() throws Exception {
    if (client != null) {
      try {
        client.shutdown();
      } catch (NullPointerException e) {
        // This is a workaround for a disagreement betweewn how things
        // should work in eclipse and buildr. My plan is to upgrade to
        // junit4 all around and write some tests that are a bit easier
        // to follow.

        // The actual problem here is a client that isn't properly
        // initialized is attempting to be shut down.
      }
    }
  }

  private void assertWorking() throws Exception {
    Map<SocketAddress, String> versions = client.getVersions();
    Iterator<SocketAddress> iterator = versions.keySet().iterator(); 
    while(iterator.hasNext()){
      String hostString = iterator.next().toString();
      assertTrue(hostString.contains("/" + TestConfig.IPV4_ADDR + ":" + TestConfig.PORT_NUMBER));
    }
  }

  private void assertArgRequired(IllegalArgumentException e) {
    assertEquals("You must have at least one server to connect to",
        e.getMessage());
  }

  @Test
  public void testVarargConstructor() throws Exception {
    InetSocketAddress host1 = new InetSocketAddress(
        InetAddress.getByName(TestConfig.IPV4_ADDR),
        TestConfig.PORT_NUMBER);
    InetSocketAddress host2 = host1;
    client = new MemcachedClient(host1, host2);
    assertWorking();
  }

  @Test
  public void testEmptyVarargConstructor() throws Exception {
    try {
      client = new MemcachedClient();
      fail("Expected illegal arg exception, got " + client);
    } catch (IllegalArgumentException e) {
      assertArgRequired(e);
    }
  }

  @Test
  public void testNulListConstructor() throws Exception {
    try {
      List<InetSocketAddress> l = null;
      client = new MemcachedClient(l);
      fail("Expected null pointer exception, got " + client);
    } catch (NullPointerException e) {
      assertEquals("Server list required", e.getMessage());
    }
  }

  @Test
  public void testEmptyListConstructor() throws Exception {
    try {
      client = new MemcachedClient(Collections.<InetSocketAddress>emptyList());
      fail("Expected illegal arg exception, got " + client);
    } catch (IllegalArgumentException e) {
      assertArgRequired(e);
    }
  }

  @Test
  public void testNullFactoryConstructor() throws Exception {
    try {
      client =
          new MemcachedClient(null, AddrUtil.getAddresses(TestConfig.IPV4_ADDR
              + ":" + TestConfig.PORT_NUMBER));
      fail("Expected null pointer exception, got " + client);
    } catch (NullPointerException e) {
      assertEquals("Connection factory required", e.getMessage());
    }
  }

  @Test
  public void testNegativeTimeout() throws Exception {
    try {
      client = new MemcachedClient(new DefaultConnectionFactory() {
        @Override
        public long getOperationTimeout() {
          return -1;
        }
      }, AddrUtil.getAddresses(TestConfig.IPV4_ADDR
              +":" + TestConfig.PORT_NUMBER));
      fail("Expected null pointer exception, got " + client);
    } catch (IllegalArgumentException e) {
      assertEquals("Operation timeout must be positive.", e.getMessage());
    }
  }

  @Test
  public void testZeroTimeout() throws Exception {
    try {
      client = new MemcachedClient(new DefaultConnectionFactory() {
        @Override
        public long getOperationTimeout() {
          return 0;
        }
      }, AddrUtil.getAddresses(TestConfig.IPV4_ADDR
              + ":" + TestConfig.PORT_NUMBER));
      fail("Expected null pointer exception, got " + client);
    } catch (IllegalArgumentException e) {
      assertEquals("Operation timeout must be positive.", e.getMessage());
    }
  }

  @Test
  public void testConnFactoryWithoutOpFactory() throws Exception {
    try {
      client = new MemcachedClient(new DefaultConnectionFactory() {
        @Override
        public OperationFactory getOperationFactory() {
          return null;
        }
      }, AddrUtil.getAddresses(TestConfig.IPV4_ADDR + ":"
              + TestConfig.PORT_NUMBER));
    } catch (AssertionError e) {
      assertEquals("Connection factory failed to make op factory",
          e.getMessage());
    }
  }

  @Test
  public void testConnFactoryWithoutConns() throws Exception {
    try {
      client = new MemcachedClient(new DefaultConnectionFactory() {
        @Override
        public MemcachedConnection createConnection(
            List<InetSocketAddress> addrs) throws IOException {
          return null;
        }
      }, AddrUtil.getAddresses(TestConfig.IPV4_ADDR + ":"
              + TestConfig.PORT_NUMBER));
    } catch (AssertionError e) {
      assertEquals("Connection factory failed to make a connection",
          e.getMessage());
    }
  }

  @Test
  public void testArraymodNodeLocatorAccessor() throws Exception {
    client =
        new MemcachedClient(AddrUtil.getAddresses(TestConfig.IPV4_ADDR
            + ":" + TestConfig.PORT_NUMBER));
    assertTrue(client.getNodeLocator() instanceof ArrayModNodeLocator);
    assertTrue(client.getNodeLocator().getPrimary("x")
        instanceof MemcachedNodeROImpl);
  }

  @Test
  public void testKetamaNodeLocatorAccessor() throws Exception {
    client =
        new MemcachedClient(new KetamaConnectionFactory(),
            AddrUtil.getAddresses(TestConfig.IPV4_ADDR
            + ":" + TestConfig.PORT_NUMBER));
    assertTrue(client.getNodeLocator() instanceof KetamaNodeLocator);
    assertTrue(client.getNodeLocator().getPrimary("x")
        instanceof MemcachedNodeROImpl);
  }
}
