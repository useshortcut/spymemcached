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

/**
 * A CancelFailureModeTest.
 */
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.spy.memcached.categories.StandardTests;
import net.spy.memcached.ops.ConfigurationType;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

/**
 * A CancellationFailureModeTest.
 */
@Category(StandardTests.class)
public class CancelFailureModeTest extends ClientBaseCase {
  private String serverList;
  private String dynamicModeServerList;

  @Override
public void setUp() throws Exception {
    serverList = TestConfig.IPV4_ADDR + ":" + "11212"
        + " " + TestConfig.IPV4_ADDR
        + ":11311";
    dynamicModeServerList = "1\n" +  "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "11212"
        + " localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "11311";
    super.setUp();
  }

  @Override
public void tearDown() throws Exception {
    serverList = TestConfig.IPV4_ADDR + ":" + "11212";
    dynamicModeServerList = "1\n" +  "localhost.localdomain|" + TestConfig.IPV4_ADDR + "|" + "11212";
    super.tearDown();
  }

  @Override
  protected void initClient(ConnectionFactory cf) throws Exception {
    if(TestConfig.getInstance().getClientMode() == ClientMode.Dynamic){
      List<InetSocketAddress> addrs = AddrUtil.getAddresses(TestConfig.IPV4_ADDR+ ":11212");
      MemcachedClient staticClient = new MemcachedClient(addrs);
      
      if(TestConfig.getInstance().getEngineType().isSetConfigSupported()) {
          staticClient.setConfig(addrs.get(0), ConfigurationType.CLUSTER, dynamicModeServerList);
      } else {
    	  staticClient.set(ConfigurationType.CLUSTER.getValueWithNameSpace(), 0, dynamicModeServerList);
    	  Thread.sleep(1000); // wait for configuration to apply
      }

      client = new MemcachedClient(cf, AddrUtil.getAddresses(TestConfig.IPV4_ADDR    + ":11212"));

    }else {
      client = new MemcachedClient(cf, AddrUtil.getAddresses(serverList));
    }
  }

  @Override
  protected void initClient() throws Exception {
    initClient(new DefaultConnectionFactory() {
      @Override
      public ClientMode getClientMode() {
        return TestConfig.getInstance().getClientMode();
      }
      
      @Override
      public FailureMode getFailureMode() {
        return FailureMode.Cancel;
      }
    });
  }

  @Override
  protected void flushPause() throws InterruptedException {
    Thread.sleep(100);
  }

  @Test
  public void testQueueingToDownServer() throws Exception {
    Future<Boolean> f = client.add("someKey", 0, "some object");
    try {
      boolean b = f.get();
      fail("Should've thrown an exception, returned " + b);
    } catch (ExecutionException e) {
      // probably OK
    }
    assertTrue(f.isCancelled());
  }
}
