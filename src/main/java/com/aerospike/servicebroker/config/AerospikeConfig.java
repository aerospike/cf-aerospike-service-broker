/*
 * Copyright 2012-2016 Aerospike, Inc.
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

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AerospikeConfig {
	@Value("${aerospike_db_host:localhost}")
	private  String hostname;
	
	@Value("${aerospike_db_port:3000}")
	private  int port;
	
	@Value("${aerospike_license_type}")
	private String licenseType;
	
	@Bean
	public AerospikeClientConfig aerospikeClientConfig() throws UnknownHostException {
		return new AerospikeClientConfig(hostname, port, licenseType);
	}
}