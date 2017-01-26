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
	
	@Value("${aerospike_license_type:community}")
	private String licenseType;
	
	@Value("${aerospike_admin_namespace:cf_admin}")
	private String adminNamespace;
	
	@Value("${aerospike_admin_user:cf_user}")
	private String user;
	
	@Value("${aerospike_admin_password:cf_password}")
	private String password;
	
	@Value("${aerospike_service_id:aerospike-service-broker}")
	private String serviceId;
	
	@Value("${aerospike_service_name:aerospike}")
	private String serviceName;
	
	@Value("${aerospike_service_description:aerospike-service-broker}")
	private String serviceDescription;

	@Value("${aerospike_license_user:none}")
	private String licenseUser;
	
	@Value("${aerospike_license_password:none}")
	private String licensePassword;	
	
	@Bean
	public AerospikeClientConfig aerospikeClientConfig() throws UnknownHostException {
		return new AerospikeClientConfig(hostname, port, licenseType, adminNamespace, user, 
				password, licenseUser, licensePassword);
	}
	
	@Bean
	public AerospikeCatalogConfig aerospikeCatalogConfig() {
		return new AerospikeCatalogConfig(serviceId, serviceName, serviceDescription);
	}
}
