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
package com.aerospike.servicebroker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.OperationState;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.stereotype.Service;

import com.aerospike.servicebroker.exception.AerospikeServiceException;
import com.aerospike.servicebroker.model.ServiceInstance;

@Service
public class AerospikeServiceInstanceService implements ServiceInstanceService{
	
	@Autowired
	private AerospikeAdminService adminService;

	private Logger logger = LoggerFactory.getLogger(AerospikeServiceInstanceService.class);

	@Override
	public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest request) {
		String serviceInstanceId = request.getServiceInstanceId();
		logger.info("Create Service Instance: " + serviceInstanceId);
		
		if (this.adminService.serviceExists(serviceInstanceId)) {
			throw new ServiceInstanceExistsException(serviceInstanceId, request.getServiceDefinitionId());
		}
		
		if (!this.adminService.validateLicense()) {
			throw new AerospikeServiceException(
				"User and password could not be validated. " +
			    "Please ensure your Aerospike Enterprise Edition user and password have been entered correctly in the tile.");
		}
		
		ServiceInstance instance = new ServiceInstance(request, request.getPlanId());
		this.adminService.createService(instance);
		
		return new CreateServiceInstanceResponse();
	}

	@Override
	public GetLastServiceOperationResponse getLastOperation(GetLastServiceOperationRequest request) {
		return new GetLastServiceOperationResponse().withOperationState(OperationState.SUCCEEDED);
	}

	@Override
	public DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest request) {
		String serviceInstanceId = request.getServiceInstanceId();
		logger.info("Deleting Service Instance: " + serviceInstanceId);
		
		if (!this.adminService.serviceExists(serviceInstanceId)) {
			throw new ServiceInstanceDoesNotExistException(serviceInstanceId);
		}

		ServiceInstance instance = this.adminService.getService(serviceInstanceId);
		this.adminService.deleteService(instance);
		
		return new DeleteServiceInstanceResponse();
	}

	@Override
	public UpdateServiceInstanceResponse updateServiceInstance(UpdateServiceInstanceRequest request) {
		String serviceInstanceId = request.getServiceInstanceId();
		logger.info("Updating Service Instance: " + serviceInstanceId);
		
		if (!this.adminService.serviceExists(serviceInstanceId)) {
			throw new ServiceInstanceDoesNotExistException(serviceInstanceId);
		}

		ServiceInstance instance = this.adminService.getService(serviceInstanceId);
		this.adminService.createService(instance);
		
		return new UpdateServiceInstanceResponse();
	}

}
