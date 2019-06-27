/**
 * Copyright (C) 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved. 
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package net.spy.memcached.util;

import java.util.Map;

public interface ConnectionUtil {

	/**
	 * Starts connection(s) to memcached server using username
	 * memcached_user, max memory usage of 4 MB, and given port(s).
	 * 
	 * @param ports
	 * @throws Exception 
	 */
	public void addLocalMemcachedServer(int...ports) throws Exception;

	/**
	 * Starts connection(s) to memcached server with the given port(s)and other memcached parameters.
	 * 
	 * @param params
	 * @param ports
	 * @throws Exception 
	 */
	public void addLocalMemcachedServer(Map<String,String> params, int...ports) throws Exception;

	/**
	 * Starts connection to memcached server with the given path, port, and other memcached parameters.
	 * 
	 * @param user
	 * @param memory
	 * @param port
	 * @throws Exception 
	 */
	public void addLocalMemcachedServer(String cmdPath, int port, Map<String,String> otherParams) throws Exception;


	/**
	 * Kills connection(s) to memcached server on given command name and port(s).
	 * NOTE: Do not enter full command path. Command name should just be "memcached", 
	 * even if it is not a built in command. 
	 * 
	 * @param ports
	 */
	public void removeLocalMemcachedServer(String cmdName, int...ports);

	/**
	 * Params:
	 * username = memcached_user
	 * memory = 4MB 
	 * 
	 * @return
	 */
	public Map<String,String> getMemcachedParams();

}
