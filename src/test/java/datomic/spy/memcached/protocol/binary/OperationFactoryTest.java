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

import datomic.spy.memcached.OperationFactory;
import datomic.spy.memcached.ops.GetAndTouchOperation;
import datomic.spy.memcached.ops.OperationCallback;
import datomic.spy.memcached.ops.TouchOperation;
import datomic.spy.memcached.OperationFactoryTestBase;

/**
 * An OperationFactoryTest.
 */
public class OperationFactoryTest extends OperationFactoryTestBase {

  @Override
  protected OperationFactory getOperationFactory() {
    return new BinaryOperationFactory();
  }

  public void testGetAndTouchOperationCloning() {
    GetAndTouchOperation.Callback callback =
      (GetAndTouchOperation.Callback) mock(GetAndTouchOperation.Callback.class).proxy();
    GetAndTouchOperation op = ofact.getAndTouch(TEST_KEY, 0, callback);

    GetAndTouchOperation op2 = cloneOne(GetAndTouchOperation.class, op);
    assertKey(op2);
    assertSame(callback, op2.getCallback());
  }

  public void testTouchOperationCloning() {
    OperationCallback callback =
      (OperationCallback) mock(OperationCallback.class).proxy();

    TouchOperation op = ofact.touch(TEST_KEY, 0, callback);

    TouchOperation op2 = cloneOne(TouchOperation.class, op);
    assertKey(op2);
    assertSame(callback, op2.getCallback());
  }

}
