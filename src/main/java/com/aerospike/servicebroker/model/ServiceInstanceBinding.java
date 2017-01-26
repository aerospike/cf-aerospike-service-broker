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
package com.aerospike.servicebroker.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;

public class ServiceInstanceBinding implements Serializable {

	private static final long serialVersionUID = 8108058495040871289L;
	
	private String id;
	private String serviceInstanceId;
	private Map<String,Object> credentials = new HashMap<>();
	private String appGuid;
	
	public ServiceInstanceBinding(CreateServiceInstanceBindingRequest request) {
		this.id = request.getBindingId();
		this.serviceInstanceId = request.getServiceInstanceId();
		this.appGuid = request.getBoundAppGuid();
	}
	
	public ServiceInstanceBinding(String id, String serviceInstanceId, Map<String, Object> credentials,
			String syslogDrainUrl, String appGuid) {
		this.id = id;
		this.serviceInstanceId = serviceInstanceId;
		setCredentials(credentials);

		this.appGuid = appGuid;
	}

	public String getId() {
		return id;
	}

	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	public Map<String, Object> getCredentials() {
		return credentials;
	}

	private void setCredentials(Map<String, Object> credentials) {
		if (credentials == null) {
			this.credentials = new HashMap<>();
		} else {
			this.credentials = credentials;
		}
	}

	public String getAppGuid() {
		return appGuid;
	}
}
