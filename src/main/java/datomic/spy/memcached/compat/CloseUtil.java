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

package datomic.spy.memcached.compat;

import java.io.Closeable;

import datomic.spy.memcached.compat.log.Logger;
import datomic.spy.memcached.compat.log.LoggerFactory;

/**
 * CloseUtil exists to provide a safe means to close anything closeable. This
 * prevents exceptions from being thrown from within finally blocks while still
 * providing logging of exceptions that occur during close. Exceptions during
 * the close will be logged using the spy logging infrastructure, but will not
 * be propagated up the stack.
 */
public final class CloseUtil {

  private static Logger logger = LoggerFactory.getLogger(CloseUtil.class);

  private CloseUtil() {
    // Empty
  }

  /**
   * Close a closeable.
   */
  public static void close(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (Exception e) {
        logger.info("Unable to close %s", closeable, e);
      }
    }
  }
}
