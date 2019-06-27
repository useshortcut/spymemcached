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
 * SPDX-License-Identifier: Apache-2.0
 */

package net.spy.memcached;

import static org.junit.Assert.fail;

import java.net.InetSocketAddress;
import java.util.List;

import net.spy.memcached.categories.StandardTests;
import net.spy.memcached.ops.ConfigurationType;

import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * A TimeoutTest.
 */
@Category(StandardTests.class)
public class TimeoutTest extends ClientBaseCase {

  @Override
public void tearDown() throws Exception {
    // override teardown to avoid the flush phase
    client.shutdown();
  }

  @Override
  protected void initClient() throws Exception {
    ConnectionFactory cf = new DefaultConnectionFactory() {
      @Override
      public ClientMode getClientMode() {
        return TestConfig.getInstance().getClientMode();
      }
      
      @Override
      public long getOperationTimeout() {
        return 20;
      }

      @Override
      public FailureMode getFailureMode() {
        return FailureMode.Retry;
      }
    };
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
      //Add delay for the client to pick up new config.
      Thread.sleep(10000);
      
    } else {
      client = new MemcachedClient(cf, AddrUtil.getAddresses(TestConfig.IPV4_ADDR
          + ":64213"));
    }
  }

  private void tryTimeout(String name, Runnable r) {
    try {
      r.run();
      fail("Expected timeout in " + name);
    } catch (OperationTimeoutException e) {
      // pass
    }
  }
  
  @Test
  public void testCasTimeout() {
    tryTimeout("cas", new Runnable() {
      public void run() {
        client.cas("k", 1, "blah");
      }
    });
  }

  @Test
  public void testGetsTimeout() {
    tryTimeout("gets", new Runnable() {
      public void run() {
        client.gets("k");
      }
    });
  }

  @Test
  public void testGetTimeout() {
    tryTimeout("get", new Runnable() {
      public void run() {
        client.get("k");
      }
    });
  }

  @Test
  public void testGetBulkTimeout() {
    tryTimeout("getbulk", new Runnable() {
      public void run() {
        client.getBulk("k", "k2");
      }
    });
  }

  @Test
  public void testIncrTimeout() {
    tryTimeout("incr", new Runnable() {
      public void run() {
        client.incr("k", 1);
      }
    });
  }

  @Test
  public void testIncrWithDefTimeout() {
    tryTimeout("incrWithDef", new Runnable() {
      public void run() {
        client.incr("k", 1, 5);
      }
    });
  }
}
