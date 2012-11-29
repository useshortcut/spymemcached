package net.spy.memcached.ops;

/**
 * Enumeration for the types of config stored in the cache server.
 * If the config API is not supported in the cache server, the configuration is stored in 
 * the key space with a "AmazonElastiCache:" prefix.
 *
 */
public enum ConfigurationType {
  
  CLUSTER("cluster");
  
  private static final String NAMESPACE = "AmazonElastiCache:";
  private String value;
  private ConfigurationType(String value) {
    this.value = value;
  }
  
  public String getValue(){
    return value;
  }
  
  public String getValueWithNameSpace(){
    return NAMESPACE + value;
  }
}
