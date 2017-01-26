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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.aerospike.servicebroker.IntegrationTestBase;
import com.aerospike.servicebroker.fixture.ServiceInstanceBindingFixture;
import com.aerospike.servicebroker.fixture.ServiceInstanceFixture;
import com.aerospike.servicebroker.model.ServiceInstance;
import com.aerospike.servicebroker.model.ServiceInstanceBinding;

public class AerospikeAdminServiceIntegrationTest extends IntegrationTestBase {

	@Autowired
	private AerospikeAdminService adminService;
	
	@Test
	public void serviceDoesNotExist() {
		assertFalse(adminService.serviceExists("BOGUS"));
	}
	
	@Test
	public void serviceBindingDoesNotExist() {
		assertFalse(adminService.serviceBindingExists("BOGUS"));
	}
	
	@Test
	public void serviceCreatedSuccess() {
		ServiceInstance instance = ServiceInstanceFixture.getServiceInstance();
		adminService.createService(instance);
		assertTrue(adminService.serviceExists(instance.getServiceInstanceId()));
		adminService.deleteService(instance);
		assertFalse(adminService.serviceExists(instance.getServiceInstanceId()));
	}
	
	@Test
	public void bindingCreatedSuccess() {
		ServiceInstanceBinding binding = ServiceInstanceBindingFixture.getServiceInstanceBinding();
		adminService.createServiceBinding(binding);
		assertTrue(adminService.serviceBindingExists(binding.getId()));
		adminService.deleteServiceBinding(binding);
		assertFalse(adminService.serviceBindingExists(binding.getId()));
	}
	
}
