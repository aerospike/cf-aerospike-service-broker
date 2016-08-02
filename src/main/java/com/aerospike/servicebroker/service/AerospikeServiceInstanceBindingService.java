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
package com.aerospike.servicebroker.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.stereotype.Service;

import com.aerospike.servicebroker.model.ServiceInstance;
import com.aerospike.servicebroker.model.ServiceInstanceBinding;

@Service
public class AerospikeServiceInstanceBindingService implements ServiceInstanceBindingService {
	private static final String SETNAME_KEY = "setname";
	
	@Autowired
	private AerospikeAdminService adminService;
	
	private Logger logger = LoggerFactory.getLogger(AerospikeServiceInstanceBindingService.class);

	@Override
	public CreateServiceInstanceBindingResponse createServiceInstanceBinding(
			CreateServiceInstanceBindingRequest request) {

		String bindingId = request.getBindingId();
		String serviceInstanceId = request.getServiceInstanceId();
		
		logger.info("Creating Binding ID: " + bindingId);
		
		if (this.adminService.serviceBindingExists(bindingId)) {
			throw new ServiceInstanceBindingExistsException(bindingId, bindingId);
		}
				
		String setName = request.getServiceInstanceId();
		if (request.getParameters().containsKey(SETNAME_KEY)) {
			setName = (String)request.getParameters().get(SETNAME_KEY);
		}
		
		ServiceInstance si = this.adminService.getService(request.getServiceInstanceId());
		String password = RandomStringUtils.randomAlphabetic(16);
		
		Map<String, Object> credentials = new HashMap<String, Object>();
		if (setName != null) {
			credentials.put("set", setName);
		}
		credentials.put("namespace", si.getNamespace());
		credentials.put("user", request.getBindingId());
		credentials.put("password", password);
		credentials.put("hostname", adminService.getHostname());
		credentials.put("port", adminService.getPort());
		credentials.put("hosts", adminService.getHosts());
		
		this.adminService.createUser(request.getBindingId(), password);
		
		ServiceInstanceBinding binding = new ServiceInstanceBinding(bindingId, serviceInstanceId, credentials,
				null, request.getBoundAppGuid());
		this.adminService.createServiceBinding(binding);

		return new CreateServiceInstanceAppBindingResponse().withCredentials(credentials);
	}

	@Override
	public void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {
		String bindingId = request.getBindingId();
		logger.info("Deleting Binding ID: " + bindingId);
		
		if (!this.adminService.serviceBindingExists(bindingId)) {
			throw new ServiceInstanceBindingDoesNotExistException(bindingId);
		}
		
		this.adminService.dropUser(bindingId);
		
		ServiceInstanceBinding binding = this.adminService.getServiceBinding(bindingId);
		this.adminService.deleteServiceBinding(binding);
	}
}
