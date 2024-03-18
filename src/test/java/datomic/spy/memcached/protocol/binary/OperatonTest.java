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

package datomic.spy.memcached.protocol.binary;

import junit.framework.TestCase;

/**
 * Test operation stuff.
 */
public class OperatonTest extends TestCase {

  public void testIntegerDecode() {
    assertEquals(129, OperationImpl.decodeInt(new byte[] { 0, 0, 0, (byte) 0x81 }, 0));
    assertEquals(129 * 256, OperationImpl.decodeInt(new byte[] { 0, 0, (byte) 0x81, 0 }, 0));
    assertEquals(129 * 256 * 256,
        OperationImpl.decodeInt(new byte[] { 0, (byte) 0x81, 0, 0 }, 0));
    assertEquals(129 * 256 * 256 * 256,
        OperationImpl.decodeInt(new byte[] { (byte) 0x81, 0, 0, 0 }, 0));
  }

  public void testUnsignedIntegerDecode() {
    assertEquals(129, OperationImpl.decodeUnsignedInt(new byte[] { 0, 0, 0, (byte) 0x81 },
        0));
    assertEquals(129 * 256,
        OperationImpl.decodeUnsignedInt(new byte[] { 0, 0, (byte) 0x81, 0 }, 0));
    assertEquals(129 * 256 * 256,
        OperationImpl.decodeUnsignedInt(new byte[] { 0, (byte) 0x81, 0, 0 }, 0));
    assertEquals(129L * 256L * 256L * 256L,
        OperationImpl.decodeUnsignedInt(new byte[] { (byte) 0x81, 0, 0, 0 }, 0));
  }

  public void testLongDecode() {
    assertEquals(4294967296L,
        OperationImpl.decodeLong(new byte[]{0, 0, 0, 1, 0, 0, 0, 0}, 0));
    assertEquals(1L,
        OperationImpl.decodeLong(new byte[]{0, 0, 0, 0, 0, 0, 0, 1}, 0));
  }

  public void testOperationStatusString() {
    String s = String.valueOf(OperationImpl.STATUS_OK);
    assertEquals("{OperationStatus success=true:  OK}", s);
  }
}
