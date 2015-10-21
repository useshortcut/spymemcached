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

import java.nio.ByteBuffer;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import net.spy.memcached.categories.StandardTests;
import net.spy.memcached.ops.Operation;
import net.spy.memcached.ops.OperationCallback;
import net.spy.memcached.ops.OperationStatus;
import net.spy.memcached.protocol.ascii.ExtensibleOperationImpl;

/**
 * @author Matt Ingenthron <ingenthr@cep.net>
 */
@Category(StandardTests.class)
public class TimeoutNowriteTest extends ClientBaseCase {

  @Override
  public void tearDown() throws Exception {
    // override teardown to avoid the flush phase
    client.shutdown();
  }

  @Override
  protected void initClient() throws Exception {
    client = new MemcachedClient(new DefaultConnectionFactory() {
      @Override
      public ClientMode getClientMode() {
        return TestConfig.getInstance().getClientMode();
      }
      
      @Override
      public long getOperationTimeout() {
        return 1000; // 1 sec
      }

      @Override
      public FailureMode getFailureMode() {
        return FailureMode.Retry;
      }
    }, AddrUtil.getAddresses(TestConfig.IPV4_ADDR + ":"
         + TestConfig.PORT_NUMBER));
  }

  @Test
  public void testTimeoutDontwrite() {
    Operation op = new ExtensibleOperationImpl(new OperationCallback() {
      public void complete() {
        System.err.println("Complete.");
      }

      public void receivedStatus(OperationStatus s) {
        System.err.println("Received a line.");
      }
    }) {

      @Override
      public void handleLine(String line) {
        System.out.println("Woo! A line!");
      }

      @Override
      public void initialize() {
        setBuffer(ByteBuffer.wrap("garbage\r\n".getBytes()));
      }

    };
    try {
      Thread.sleep(1100);
    } catch (InterruptedException ex) {
      System.err.println("Interrupted when sleeping for timeout nowrite");
    }

    client.mconn.enqueueOperation("x", op);
    System.err.println("Operation attempted:");
    System.err.println(op);
    System.err.println("Trying to get:");
    try {
      client.get("x");
      String retValString = new String();
      System.err.println(retValString);
    } catch (net.spy.memcached.OperationTimeoutException ex) {
      System.err.println("Timed out successfully: " + ex.getMessage());
    }

    System.err.println("Op timed out is " + op.isTimedOut());
    assert op.isTimedOut();
  }
}
