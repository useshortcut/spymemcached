/**
 * Copyright (C) 2006-2009 Dustin Sallings
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

package datomic.spy.memcached.test;

import datomic.spy.memcached.AddrUtil;
import datomic.spy.memcached.MemcachedClient;

/**
 * This is an attempt to reproduce a problem where a server fails during a
 * series of gets.
 */
public final class MultiNodeFailureTest {

  private MultiNodeFailureTest() {
    // Empty
  }

  public static void main(String[] args) throws Exception {
    MemcachedClient c =
        new MemcachedClient(
            AddrUtil.getAddresses("localhost:11200 localhost:11201"));
    while (true) {
      for (int i = 0; i < 1000; i++) {
        try {
          c.getBulk("blah1", "blah2", "blah3", "blah4", "blah5");
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      System.out.println("Did a thousand.");
    }
  }

}
