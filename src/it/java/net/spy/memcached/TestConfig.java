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
 * Portions Copyright (C) 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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


/**
 * A testConfig.
 */
public final class TestConfig {
  public static enum engineTypeEnum {
    V_1_4_24 ("1.4.24", true),
    V_1_4_14 ("1.4.14", true),
    V_1_4_5 ("1.4.5", false);
    private String version;
    private boolean setConfigSupported;
    engineTypeEnum(String version, boolean setConfigSupported) {
      this.version = version;
      this.setConfigSupported = setConfigSupported;
    }
    public String getVersion() {
      return this.version;
    }
    public boolean isSetConfigSupported() {
      return this.setConfigSupported;
    }
    public static engineTypeEnum fromString(String version) {
      if (version != null) {
        for (engineTypeEnum engineType: engineTypeEnum.values()) {
          if (engineType.getVersion().equals(version)) {
            return engineType;
          }
        }
      }
      throw new IllegalArgumentException("Memcached engine version " + version + " is not supported!");
    }
  };
  public static final String IPV4_PROP = "server.address_v4";
  public static final String IPV6_PROP = "server.address_v6";
  public static final String TEST_PROP = "test.type";
  public static final String PORT_PROP = "server.port_number";
  public static final String TYPE_TEST_UNIT = "unit";
  public static final String TYPE_TEST_CI = "ci";
  public static final String SERVER_BIN = "server.bin";
  public static final String SERVER_VERSION = "server.version";

  //currently server host address is always default to "127.0.0.1", disabled in build.xml
  public static final String IPV4_ADDR = System.getProperty(IPV4_PROP,
      "127.0.0.1");
  public static final String IPV6_ADDR = resolveIpv6Addr();

  //currently server port is always default to 11211, disabled in build.xml
  public static final int PORT_NUMBER =
      Integer.parseInt(System.getProperty(PORT_PROP, "11211"));

  public static final String TEST_TYPE = System.getProperty(TEST_PROP,
      TYPE_TEST_UNIT).toLowerCase();
  public static final String MEMCACHED_NAME = "memcached";
  public static final String MEMCACHED_PATH = System.getProperty(SERVER_BIN, "/usr/bin/memcached");
  public static final String MEMCACHED_VERSION = System.getProperty(SERVER_VERSION, "1.4.24");

  private ClientMode clientMode;
  private engineTypeEnum engineType;
  private static TestConfig testConfig;

  private TestConfig(ClientMode clientMode, engineTypeEnum ConfigType) {
    this.clientMode = clientMode;
    this.engineType = ConfigType;
  }

  public static void initialize(ClientMode clientMode) {
    if(testConfig == null){  
      testConfig = new TestConfig(clientMode, engineTypeEnum.fromString(MEMCACHED_VERSION));
    }else{
      throw new IllegalStateException("TestConfig singleton instance is already initialized");
    }
  }

  public static void initialize(ClientMode clientMode, engineTypeEnum ConfigType){
    if(testConfig == null){
      testConfig = new TestConfig(clientMode, ConfigType);
    }else{
      throw new IllegalStateException("TestConfig singleton instance is already initialized");
    }
  }
  
  public static TestConfig getInstance(){
    if(testConfig == null){
      testConfig = new TestConfig(ClientMode.Static, engineTypeEnum.fromString(MEMCACHED_VERSION));
    }
    
    return testConfig;
  }
  
  public ClientMode getClientMode(){
    return clientMode;
  }

  public engineTypeEnum getEngineType() {
      return this.engineType;
  }
  
  private static String resolveIpv6Addr() {
	//currently server host address ipv6 is always default to "::1", disabled in build.xml
    String ipv6 = System.getProperty(IPV6_PROP, "::1");
    // If the ipv4 address was set but the ipv6 address wasn't then
    // set the ipv6 address to use ipv4.
    if (!IPV4_ADDR.equals("127.0.0.1") && !IPV4_ADDR.equals("localhost")
        && ipv6.equals("::1")) {
      return "::ffff:" + IPV4_ADDR;
    }
    return ipv6;
  }

  public static boolean defaultToIPV4() {
    if (("::ffff:" + IPV4_ADDR).equals(IPV6_ADDR)) {
      return true;
    }
    return false;
  }

  public static boolean isCITest() {
    return TEST_TYPE.equals(TYPE_TEST_CI);
  }

}
