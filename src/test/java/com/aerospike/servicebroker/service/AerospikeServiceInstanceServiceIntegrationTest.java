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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;

import com.aerospike.servicebroker.IntegrationTestBase;
import com.aerospike.servicebroker.fixture.ServiceInstanceFixture;

public class AerospikeServiceInstanceServiceIntegrationTest extends IntegrationTestBase {
	private static final String SVC_DEF_ID = "serviceDefinitionId";
	private static final String SVC_PLAN_ID = "servicePlanId";

	@Autowired
	AerospikeServiceInstanceService service;
	
	@Mock
	private ServiceDefinition serviceDefinition;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void newServiceInstanceCreatedSuccessfully() {
		CreateServiceInstanceResponse response = service.createServiceInstance(buildCreateRequest());
		assertNotNull(response);
		assertFalse(response.isAsync());
		
		DeleteServiceInstanceResponse delResponse = service.deleteServiceInstance(buildDeleteRequest());
		assertNotNull(delResponse);
		assertFalse(delResponse.isAsync());
	}
	
	@Test
	public void serviceInstanceCreationFailsWithExistingInstance() throws Exception {
		CreateServiceInstanceResponse response = service.createServiceInstance(buildCreateRequest());
		assertNotNull(response);
		
		boolean thrown = false;

		try {
			response = service.createServiceInstance(buildCreateRequest());
		} catch (ServiceInstanceExistsException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
		
		DeleteServiceInstanceResponse delResponse = service.deleteServiceInstance(buildDeleteRequest());
		assertNotNull(delResponse);
	}
	
	@Test(expected = ServiceInstanceDoesNotExistException.class)
	public void unknownServiceInstanceDeleteCallSuccessful() throws Exception {
		DeleteServiceInstanceResponse response = service.deleteServiceInstance(buildDeleteRequest());

		assertNotNull(response);
		assertFalse(response.isAsync());
	}
	
	private CreateServiceInstanceRequest buildCreateRequest() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("namespace", "cf_admin");
		return new CreateServiceInstanceRequest(SVC_DEF_ID, SVC_PLAN_ID, "organizationGuid", "spaceGuid", parameters)
				.withServiceInstanceId(ServiceInstanceFixture.getServiceInstance().getServiceInstanceId());

	}

	private DeleteServiceInstanceRequest buildDeleteRequest() {
		return new DeleteServiceInstanceRequest(ServiceInstanceFixture.getServiceInstance().getServiceInstanceId(),
				SVC_DEF_ID, SVC_PLAN_ID, serviceDefinition);
	}
}
