/**
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
 * Portions Copyright (C) 2012-2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import junit.framework.TestCase;

/**
 * These tests test to make sure that we don't get null pointer
 * exceptions when calling toString methods on classes that have
 * custom toString() functions.
 */
public class ToStringTest extends TestCase {

  public void testDefaultConnectionFactory() {
    (new DefaultConnectionFactory()).toString();
    (new DefaultConnectionFactory(10, 1000)).toString();
    (new DefaultConnectionFactory(ClientMode.Dynamic, 100, 100,
        DefaultHashAlgorithm.KETAMA_HASH)).toString();
  }

  public void testBinaryConnectionFactory() {
    (new BinaryConnectionFactory()).toString();
    (new BinaryConnectionFactory(10, 1000)).toString();
    (new BinaryConnectionFactory(ClientMode.Dynamic, 100, 1000,
        DefaultHashAlgorithm.KETAMA_HASH)).toString();
  }
}
