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

package datomic.spy.memcached.protocol.ascii;

import datomic.spy.memcached.ops.OperationErrorType;
import datomic.spy.memcached.ops.OperationException;
import junit.framework.TestCase;

/**
 * Test operation exception constructors and accessors and stuff.
 */
public class OperationExceptionTest extends TestCase {

  public void testEmpty() {
    OperationException oe = new OperationException();
    assertSame(OperationErrorType.GENERAL, oe.getType());
    assertEquals("OperationException: GENERAL", String.valueOf(oe));
  }

  public void testServer() {
    OperationException oe = new OperationException(OperationErrorType.SERVER,
        "SERVER_ERROR figures");
    assertSame(OperationErrorType.SERVER, oe.getType());
    assertEquals("OperationException: SERVER: SERVER_ERROR figures",
        String.valueOf(oe));
  }

  public void testClient() {
    OperationException oe = new OperationException(OperationErrorType.CLIENT,
        "CLIENT_ERROR nope");
    assertSame(OperationErrorType.CLIENT, oe.getType());
    assertEquals("OperationException: CLIENT: CLIENT_ERROR nope",
        String.valueOf(oe));
  }

  public void testGeneral() {
    // General type doesn't have additional info
    OperationException oe = new OperationException(OperationErrorType.GENERAL,
        "GENERAL wtf");
    assertSame(OperationErrorType.GENERAL, oe.getType());
    assertEquals("OperationException: GENERAL", String.valueOf(oe));
  }
}
