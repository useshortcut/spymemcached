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

package datomic.spy.memcached.transcoders;

import datomic.spy.memcached.CachedData;
import junit.framework.TestCase;

/**
 * Test the integer transcoder.
 */
public class IntegerTranscoderTest extends TestCase {

  private IntegerTranscoder tc = null;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    tc = new IntegerTranscoder();
  }

  public void testInt() throws Exception {
    assertEquals(923, tc.decode(tc.encode(923)).intValue());
  }

  public void testBadFlags() throws Exception {
    CachedData cd = tc.encode(9284);
    assertNull(tc.decode(new CachedData(cd.getFlags() + 1, cd.getData(),
        CachedData.MAX_SIZE)));
  }
}
