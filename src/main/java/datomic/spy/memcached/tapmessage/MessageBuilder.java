/**
 * Copyright (C) 2009-2012 Couchbase, Inc.
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

package datomic.spy.memcached.tapmessage;

/**
 * Builds a tap message.
 */
public class MessageBuilder {
  private RequestMessage message;

  public MessageBuilder() {
    this.message = new RequestMessage();
    message.setMagic(TapMagic.PROTOCOL_BINARY_REQ);
    message.setOpcode(TapOpcode.REQUEST);
  }

  public void doBackfill(long date) {
    message.setBackfill(date);
    message.setFlags(TapRequestFlag.BACKFILL);
  }

  public void doDump() {
    message.setFlags(TapRequestFlag.DUMP);
  }

  public void specifyVbuckets(short[] vbucketlist) {
    message.setVbucketlist(vbucketlist);
    message.setFlags(TapRequestFlag.LIST_VBUCKETS);
  }

  public void supportAck() {
    message.setFlags(TapRequestFlag.SUPPORT_ACK);
  }

  public void keysOnly() {
    message.setFlags(TapRequestFlag.KEYS_ONLY);
  }

  public void takeoverVbuckets(short[] vbucketlist) {
    message.setVbucketlist(vbucketlist);
    message.setFlags(TapRequestFlag.TAKEOVER_VBUCKETS);
  }

  public RequestMessage getMessage() {
    return message;
  }
}
