/**
 * Copyright (C) 2006-2009 Dustin Sallings
 * Copyright (C) 2009-2013 Couchbase, Inc.
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

import datomic.spy.memcached.KeyUtil;
import datomic.spy.memcached.ops.OperationCallback;
import datomic.spy.memcached.ops.OperationState;
import datomic.spy.memcached.ops.OperationStatus;
import datomic.spy.memcached.ops.StatusCode;
import datomic.spy.memcached.ops.TouchOperation;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;


/**
 * Memcached touch operation.
 */
final class TouchOperationImpl extends OperationImpl implements TouchOperation {

  private static final int OVERHEAD = 9;

  private static final OperationStatus OK =
    new OperationStatus(true, "TOUCHED", StatusCode.SUCCESS);

  private final String key;
  private final int exp;

  public TouchOperationImpl(String k, int t, OperationCallback cb) {
    super(cb);
    key = k;
    exp = t;
  }

  // This method implements getKeys in KeyedOperation and should be moved
  // once TouchOperation in ops is unified with the binary touch method impl.
  public Collection<String> getKeys() {
    return Collections.singleton(key);
  }

  @Override
  public void handleLine(String line) {
    getLogger().debug("Touch completed successfully");
    getCallback().receivedStatus(matchStatus(line, OK));
    transitionState(OperationState.COMPLETE);
  }

  @Override
  public void initialize() {
    ByteBuffer b = null;
    b = ByteBuffer.allocate(KeyUtil.getKeyBytes(key).length
      + String.valueOf(exp).length() + OVERHEAD);
    b.put(("touch " + key + " " + exp + "\r\n").getBytes());
    b.flip();
    setBuffer(b);
  }

  @Override
  public int getExpiration() {
    return exp;
  }

  @Override
  public String toString() {
    return "Cmd: touch key: " + key + " exp: " + exp;
  }
}
