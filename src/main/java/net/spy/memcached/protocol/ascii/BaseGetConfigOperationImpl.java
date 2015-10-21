/**
 * Copyright (C) 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved. 
 *
 * Licensed under the Amazon Software License (the "License"). You may not use this 
 * file except in compliance with the License. A copy of the License is located at
 *  http://aws.amazon.com/asl/
 * or in the "license" file accompanying this file. This file is distributed on 
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or
 * implied. See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package net.spy.memcached.protocol.ascii;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;

import net.spy.memcached.ConfigurationTypeUtil;
import net.spy.memcached.KeyUtil;
import net.spy.memcached.ops.ConfigurationType;
import net.spy.memcached.ops.GetAndTouchOperation;
import net.spy.memcached.ops.GetConfigOperation;
import net.spy.memcached.ops.GetOperation;
import net.spy.memcached.ops.GetlOperation;
import net.spy.memcached.ops.GetsOperation;
import net.spy.memcached.ops.OperationCallback;
import net.spy.memcached.ops.OperationState;
import net.spy.memcached.ops.OperationStatus;
import net.spy.memcached.util.StringUtils;

/**
 * Base class for getConfig handlers.
 */
abstract class BaseGetConfigOperationImpl extends OperationImpl {

  private static final OperationStatus END = new OperationStatus(true, "END");
  private static final OperationStatus NOT_FOUND = new OperationStatus(false,
      "NOT_FOUND");
  private static final OperationStatus LOCK_ERROR = new OperationStatus(false,
      "LOCK_ERROR");
  private static final byte[] RN_BYTES = "\r\n".getBytes();
  private final String cmd;
  protected final Collection<ConfigurationType> types;
  private String currentType = null;
  private final int exp;
  private final boolean hasExp;
  private int currentFlags = 0;
  private byte[] data = null;
  private int readOffset = 0;
  private boolean hasValue;

  /*
   * Enum to track the state transition while reading off the buffers. Tracking each state
   *  is critical as the entire response might span multiple buffer reads. 
   */
  private enum BufferState{ 
    READ_CONFIG('*'),
    RESPONSE_END_START('\r'),
    RESPONSE_END_FINISH('\n'),
    READ_COMPLETED('\0');
    
    //The value mapping to the enum is used sometimes to help in state transition.
    private byte value;
    
    BufferState(char value){
      this.value = (byte)value;
    }
    
    public byte getValue(){
      return value;
    }
    
  }
  private BufferState lookingFor = BufferState.READ_CONFIG;
  
  public BaseGetConfigOperationImpl(String cmd, OperationCallback cb, Collection<ConfigurationType> types) {
    super(cb);
    this.cmd = cmd;
    this.types = types;
    this.exp = 0;
    this.hasExp = false;
    this.hasValue = false;
  }

  public BaseGetConfigOperationImpl(String cmd, int e, OperationCallback cb, ConfigurationType type) {
    super(cb);
    this.cmd = cmd;
    this.types = Collections.singleton(type);
    this.exp = e;
    this.hasExp = true;
    this.hasValue = false;
  }

  /**
   * Get the types that this GetConfigOperation is looking for.
   */
  public final Collection<ConfigurationType> getTypes() {
    return Collections.unmodifiableCollection(types);
  }

  @Override
  public final void handleLine(String line) {
    if (line.equals("END")) {
      getLogger().debug("Get complete!");
      if (this.hasValue) {
        getCallback().receivedStatus(END);
      } else {
        getCallback().receivedStatus(NOT_FOUND);
      }
      transitionState(OperationState.COMPLETE);
      data = null;
    } else if (line.startsWith("CONFIG ")) {
      getLogger().debug("Got line %s", line);
      String[] configPreamble = line.split(" ");
      assert configPreamble[0].equals("CONFIG");
      this.currentType = configPreamble[1];
      this.currentFlags = Integer.parseInt(configPreamble[2]);
      this.data = new byte[Integer.parseInt(configPreamble[3])];
      this.readOffset = 0;
      this.hasValue = true;
      getLogger().debug("Set read type to data");
      setReadType(OperationReadType.DATA);
    } else {
      assert false : "Unknown line type: " + line;
    }
  }

  @Override
  public final void handleRead(ByteBuffer readBuffer) {
    assert this.currentType != null;
    assert this.data != null;
    // This will be the case, because we'll clear them when it's not.
    assert this.readOffset <= this.data.length : "readOffset is " + this.readOffset
        + " data.length is " + this.data.length;

    getLogger().debug("readOffset: %d, length: %d", this.readOffset, this.data.length);
    // If we're not looking for termination, we're still looking for data
    if (this.lookingFor == BufferState.READ_CONFIG) {
      transferFromBuffer(readBuffer);
    }
    
    // Transition us into a ``looking for \r\n'' kind of state if we've
    // read enough and are still in a data state.
    if (this.readOffset == this.data.length && this.lookingFor == BufferState.READ_CONFIG) {
      updateCallBackWithResponse();
      this.lookingFor = BufferState.RESPONSE_END_START;
    }
    
    // If we're looking for an ending byte, let's go find it.
    if (this.lookingFor != BufferState.READ_CONFIG && readBuffer.hasRemaining()) {
      readResponseEnding(readBuffer);
      // Completed the read, reset stuff.
      if (this.lookingFor == BufferState.READ_COMPLETED) {
        resetAndChangeReadTypeToLine();
      }
    }
  }

  void transferFromBuffer(ByteBuffer readBuffer){
    int toRead = this.data.length - this.readOffset;
    int available = readBuffer.remaining();
    toRead = Math.min(toRead, available);
    getLogger().debug("Reading %d bytes", toRead);
    readBuffer.get(this.data, this.readOffset, toRead);
    this.readOffset += toRead;    
  }
  
  void updateCallBackWithResponse(){
    OperationCallback cb = getCallback();
    if (cb instanceof GetConfigOperation.Callback) {
      GetConfigOperation.Callback gcb = (GetConfigOperation.Callback) cb;
      gcb.gotData(ConfigurationType.valueOf(this.currentType.toUpperCase()), this.currentFlags, this.data);
    } else {
      throw new ClassCastException("Couldn't convert " + cb
          + "to a relevent op");
    }
  }
  
  void readResponseEnding(ByteBuffer readBuffer){
    do {
      byte tmp = readBuffer.get();
      assert tmp == this.lookingFor.getValue() : "Expecting " + this.lookingFor.getValue() + ", got "
          + (char) tmp;
      switch (this.lookingFor) {
      case RESPONSE_END_START:
        this.lookingFor = BufferState.RESPONSE_END_FINISH;
        break;
      case RESPONSE_END_FINISH:
        this.lookingFor = BufferState.READ_COMPLETED;
        break;
      default:
        assert false : "Looking for unexpected char: " + (char) this.lookingFor.getValue();
      }
    } while (this.lookingFor != BufferState.READ_COMPLETED && readBuffer.hasRemaining());    
  }
  
  void resetAndChangeReadTypeToLine(){
    this.currentType = null;
    this.data = null;
    this.lookingFor = BufferState.READ_CONFIG;
    this.readOffset = 0;
    this.currentFlags = 0;
    getLogger().debug("Setting read type back to line.");
    setReadType(OperationReadType.LINE);
  }

  @Override
  public final void initialize() {
    // Figure out the length of the request
    int size = 12; // Enough for config get\r\n
    Collection<byte[]> typeBytes = ConfigurationTypeUtil.getTypeBytes(this.types);
    for (byte[] t : typeBytes) {
      size += t.length;
      //Increment one more for the space after type.
      size++;
    }
    byte[] e = String.valueOf(this.exp).getBytes();
    if (this.hasExp) {
      size += e.length + 1;
    }
    ByteBuffer b = ByteBuffer.allocate(size);
    b.put(this.cmd.getBytes());
    for (byte[] t : typeBytes) {
      b.put((byte) ' ');
      b.put(t);
    }
    if (this.hasExp) {
      b.put((byte) ' ');
      b.put(e);
    }
    b.put(RN_BYTES);
    b.flip();
    setBuffer(b);
  }

  @Override
  protected final void wasCancelled() {
    getCallback().receivedStatus(CANCELLED);
  }

  @Override
  public String toString() {
    StringBuffer typeValues = new StringBuffer();
    for(ConfigurationType type : types){
      typeValues.append(" " + type.getValue());
    }
    
    return "Cmd: " + cmd + " Types: " + typeValues.toString() + "Exp: "
      + exp;
  }
}
