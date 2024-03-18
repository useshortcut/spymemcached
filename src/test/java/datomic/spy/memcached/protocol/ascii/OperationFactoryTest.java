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

import datomic.spy.memcached.OperationFactory;
import datomic.spy.memcached.ops.Mutator;
import datomic.spy.memcached.ops.MutatorOperation;
import datomic.spy.memcached.OperationFactoryTestBase;

/**
 * An OperationFactoryTest.
 */
public class OperationFactoryTest extends OperationFactoryTestBase {

  @Override
  protected OperationFactory getOperationFactory() {
    return new AsciiOperationFactory();
  }

  @Override
  public void testMutatorOperationIncrCloning() {
    int exp = 823862;
    long def = 28775;
    long by = 7735;
    MutatorOperation op = ofact.mutate(Mutator.incr, TEST_KEY, by, def, exp,
        genericCallback);

    MutatorOperation op2 = cloneOne(MutatorOperation.class, op);
    assertKey(op2);
    assertEquals(-1, op2.getExpiration());
    assertEquals(-1, op2.getDefault());
    assertEquals(by, op2.getBy());
    assertSame(Mutator.incr, op2.getType());
    assertCallback(op2);
  }

  @Override
  public void testMutatorOperationDecrCloning() {
    int exp = 823862;
    long def = 28775;
    long by = 7735;
    MutatorOperation op = ofact.mutate(Mutator.decr, TEST_KEY, by, def, exp,
        genericCallback);

    MutatorOperation op2 = cloneOne(MutatorOperation.class, op);
    assertKey(op2);
    assertEquals(-1, op2.getExpiration());
    assertEquals(-1, op2.getDefault());
    assertEquals(by, op2.getBy());
    assertSame(Mutator.decr, op2.getType());
    assertCallback(op2);
  }
}
