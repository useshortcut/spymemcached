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
package net.spy.memcached.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import net.spy.memcached.TestConfig;
import net.spy.memcached.compat.log.Logger;
import net.spy.memcached.compat.log.LoggerFactory;

public class ConnectionUtilLinuxImpl implements ConnectionUtil {

	public static final Logger log = LoggerFactory.getLogger(ConnectionUtilLinuxImpl.class);
	public static final String PS = "ps -o pid,command -C ";
	public static final String KILL = "kill -9 ";
	
	/**
	 * Starts connection(s) to memcached server using username
	 * memcached_user, max memory usage of 4 MB, and given port(s).
	 * 
	 * @param ports
	 * @throws Exception 
	 */
	@Override
	public void addLocalMemcachedServer(int...ports) throws Exception {
		for(int p: ports) {
			addLocalMemcachedServer(TestConfig.MEMCACHED_PATH, p, getMemcachedParams());		  
		}
	}

	/**
	 * Starts connection(s) to memcached server with the given port(s)and other memcached parameters.
	 * 
	 * @param params
	 * @param ports
	 * @throws Exception 
	 */
	@Override
	public void addLocalMemcachedServer(Map<String, String> params,
			int... ports) throws Exception {
		for(int p: ports) {
			addLocalMemcachedServer(TestConfig.MEMCACHED_PATH, p, params);		  
		}		

	}

	/**
	 * Starts connection to memcached server with the given path, port, and other memcached parameters.
	 * 
	 * @param user
	 * @param memory
	 * @param port
	 * @throws Exception 
	 */
	@Override 
	public void addLocalMemcachedServer(String cmdPath, int port, Map<String,String> otherParams) throws Exception {
		try {
			String cmd = cmdPath;
			for(String param: otherParams.keySet()) {
				cmd = cmd + " " + param + " " + otherParams.get(param);
			}
			cmd = cmd + " -p " + port + " &"; //specify port, run in background

			InputStream stream = Runtime.getRuntime().exec(cmd).getErrorStream();
			InputStreamReader reader = new InputStreamReader(stream);		  
			BufferedReader buf = new BufferedReader(reader);
			try {
				Thread.sleep(2); // buffer is not immediately ready
			} catch (InterruptedException e) {
				log.warn("Sleep was interrupted.");
			}

			//warns and continues if previous connection or warnings arise
			//right now set to hard failure on all other failures
			if(buf.ready()) {
				String line = "";
				while ((line=buf.readLine())!=null) {
					if(line.matches(".*failed to listen on.*")) {
						log.warn(line);
					} else if(line.matches(".*WARNING.*") || line.matches(".*warning.*")) {
						log.warn(line);
					} else {
						throw new Exception(line);
					}

				}
			}
			buf.close(); 
			reader.close(); 
			stream.close(); 

		} catch (IOException e) {
			log.warn("Memcached process could not start for port: " + port +".");
			throw e; 
		}
	}

	/**
	 * Kills connection(s) to memcached server on given command name and port(s).
	 * NOTE: Do not enter full command path. Command name should just be "memcached", 
	 * even if it is not a built in command. 
	 * 
	 * @param ports
	 */
	@Override
	public void removeLocalMemcachedServer(String cmdName, int...ports) {
		String line = "";

		try {
			BufferedReader buf = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(PS + cmdName).getInputStream()));
			while ((line=buf.readLine())!=null) {
				if(line.matches(".*" + cmdName + ".*")) {
					for(int i=0; i< ports.length; i++) {
						if(line.matches(".*" + ports[i] + ".*")) {
							String pid = line.substring(0, line.indexOf(cmdName));
							if(pid.contains("/".subSequence(0,1))) {
								pid = pid.substring(0, pid.indexOf("/"));
							}
							Runtime.getRuntime().exec(KILL + pid); 
							log.info("Killed connection to port " + ports[i] + " with pid " + pid);
						}
					}
				}
			}
			buf.close();
		} catch (Exception e) {
			log.warn("Could not terminate memcached processes.");
		} 
	}

	/**
	 * Params:
	 * username = memcached_user
	 * memory = 4MB 
	 * 
	 * @return
	 */

	@Override
	public Map<String,String> getMemcachedParams() {
		Map<String,String> params = new HashMap<String,String>();
		params.put("-m", "4");
		return params;
	}


}
