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

import java.util.Arrays;

import datomic.spy.memcached.CachedData;

/**
 * A WhalinV1TranscoderTest.
 */
public class WhalinV1TranscoderTest extends BaseTranscoderCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    setTranscoder(new WhalinV1Transcoder());
  }

  @Override
  public void testByteArray() throws Exception {
    byte[] a = { 'a', 'b', 'c' };

    CachedData cd = getTranscoder().encode(a);
    byte[] decoded = (byte[]) getTranscoder().decode(cd);
    assertNotNull(decoded);
    assertTrue(Arrays.equals(a, decoded));
  }

  public void testJsonObject() {
    WhalinV1Transcoder transcoder = ((WhalinV1Transcoder)getTranscoder());
    String json = "{\"aaaaaaaaaaaaaaaaaaaaaaaaa\":"
        + "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\"}";
    transcoder.setCompressionThreshold(8);
    CachedData cd = transcoder.encode(json);
    assertFalse("Flags shows JSON was compressed",
        (cd.getFlags() & (1L << WhalinV1Transcoder.COMPRESSED)) != 0);
    assertTrue("JSON was incorrectly encoded", Arrays.equals(json.getBytes(),
        Arrays.copyOfRange(cd.getData(), 1, cd.getData().length)));
    assertEquals("JSON was harmed, should not have been",
        json, transcoder.decode(cd));
  }

  @Override
  protected int getStringFlags() {
    // Flags are not used by this transcoder.
    return 0;
  }
}
