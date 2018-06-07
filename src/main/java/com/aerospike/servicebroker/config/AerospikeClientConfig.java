/*
 * Copyright 2012-2017 Aerospike, Inc.
 *
 * Portions may be licensed to Aerospike, Inc. under one or more contributor
 * license agreements WHICH ARE COMPATIBLE WITH THE APACHE LICENSE, VERSION 2.0.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.aerospike.servicebroker.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;

public class AerospikeClientConfig {
	public final String hostname;
	public final int port;
	public final String licenseType;
	public final String adminNamespace;
	public final String user;
	public final String password;
	public final String licenseUser;
	public final String licensePassword;
	public final String serviceName;

	public AerospikeClientConfig(String hostname, int port, String licenseType, 
			String adminNamespace, String user, String password, String licenseUser,
			String licensePassword, String serviceName, String jsonNodeProperties,
			String aerospikeNodeHost) {

		this.hostname = aerospikeNodeHost == null ? hostname : aerospikeNodeHost;

		this.port = getPortFromLink(jsonNodeProperties, port);
		this.licenseType = licenseType;
		this.adminNamespace = adminNamespace;
		this.user = user;
		this.password = password;
		this.licenseUser = licenseUser;
		this.licensePassword = licensePassword;
		this.serviceName = serviceName;
	}

	/*
	Our bosh deploy script on CF **may** push properties from a link which will look like:
	{
		"aerospike": {
			"network": {
				"service_port": ####,
				"fabric_port": ####,
				"heartbeat_port": ####,
				"info_port": ####
			}
		}
	}
	If the link was null, this is a noop.
	*/
	private int getPortFromLink(String jsonNodeProperties, int defaultPort) {
		if (jsonNodeProperties == null) {
			return defaultPort;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode aerospikeNode = mapper.readTree(jsonNodeProperties);
			return aerospikeNode.get("aerospike").get("network").get("service_port").intValue();
		} catch(IOException e) {
			e.printStackTrace();
			return defaultPort;
		} catch(NullPointerException e) {
			/* If any of these intermediate nodes don't exist, we'll get this error */
			e.printStackTrace();
			return defaultPort;
		}
	}
}
