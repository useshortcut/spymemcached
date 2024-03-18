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
 */

package datomic.spy.memcached;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

import datomic.spy.memcached.internal.GetFuture;
import datomic.spy.memcached.internal.OperationFuture;
import datomic.spy.memcached.ops.OperationCallback;
import datomic.spy.memcached.ops.OperationStatus;
import datomic.spy.memcached.ops.StatusCode;
import datomic.spy.memcached.protocol.ascii.ExtensibleOperationImpl;

import org.junit.Test;

/**
 * This test assumes a server is running on the host specified in the
 * environment variable SPY_MC_TEST_SERVER or localhost:11211 by default.
 */
public class AsciiClientTest extends ProtocolBaseCase {

  public void testBadOperation() throws Exception {
    client.mconn.enqueueOperation("x",
        new ExtensibleOperationImpl(new OperationCallback() {
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
      });
  }

  @Override
  @Test(expected=UnsupportedOperationException.class)
  public void testSetReturnsCAS() {
  }
  @Override
  protected String getExpectedVersionSource() {
    return String.valueOf(new InetSocketAddress(TestConfig.IPV4_ADDR,
        TestConfig.PORT_NUMBER));
  }

  public void testAsyncCASResponse() throws InterruptedException,
    ExecutionException {
    String key = "testAsyncCASResponse";
    client.set(key, 300, key + "0");
    CASValue<Object> getsRes = client.gets(key);
    OperationFuture<CASResponse> casRes = client.asyncCAS(key, getsRes.getCas(),
      key + "1");
    try {
      casRes.getCas();
      fail("Expected an UnsupportedOperationException");
    } catch (UnsupportedOperationException ex) {
      //expected
    }
  }

  public void testAddGetSetStatusCodes() throws Exception {
    OperationFuture<Boolean> set = client.set("statusCode1", 0, "value");
    set.get();
    assertEquals(StatusCode.SUCCESS, set.getStatus().getStatusCode());

    GetFuture<Object> get = client.asyncGet("statusCode1");
    get.get();
    assertEquals(StatusCode.SUCCESS, get.getStatus().getStatusCode());

    OperationFuture<Boolean> add = client.add("statusCode1", 0, "value2");
    add.get();
    assertEquals(StatusCode.ERR_NOT_STORED, add.getStatus().getStatusCode());
  }

  public void testAsyncIncrementWithDefault() throws Exception {
    String k = "async-incr-with-default";
    try {
      client.asyncIncr(k, 1, 5);
      assertTrue(false);
    } catch (UnsupportedOperationException e) {
      assertTrue(true);
    }
  }

  public void testAsyncDecrementWithDefault() throws Exception {
    String k = "async-decr-with-default";
    try {
      client.asyncDecr(k, 1, 5);
      assertTrue(false);
    } catch (UnsupportedOperationException e) {
      assertTrue(true);
    }
  }

}
