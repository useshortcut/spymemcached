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

package datomic.spy.memcached.internal;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple thread factory that can set daemon status on threads and give them
 * names.
 */
public class BasicThreadFactory implements ThreadFactory {

  private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
  private final AtomicInteger threadNumber = new AtomicInteger(1);
  private final String namePrefix;
  private final boolean daemon;

  public BasicThreadFactory(String name, boolean daemon) {
    this.namePrefix = name + "-" + POOL_NUMBER.getAndIncrement() + "-";
    this.daemon = daemon;
  }

  public Thread newThread(Runnable r) {
    Thread t = new Thread(r, namePrefix + threadNumber.getAndIncrement());
    t.setDaemon(daemon);
    return t;
  }
}
