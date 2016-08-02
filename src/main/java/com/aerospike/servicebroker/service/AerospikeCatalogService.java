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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.model.Plan;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.cloud.servicebroker.service.CatalogService;
import org.springframework.stereotype.Service;

@Service
public class AerospikeCatalogService implements CatalogService {
	private static final String STORAGE_ENGINE_KEY = "storage-engine";
	private static final String MEMORY_SIZE_KEY = "memory-size";
	private static final String REPLICATION_FACTOR_KEY = "repl-factor";
	
	@Autowired
	AerospikeAdminService adminService;
	
	@Override
	public Catalog getCatalog() {
		return new Catalog(Collections.singletonList(getServiceDefinition()));
	}

	@Override
	public ServiceDefinition getServiceDefinition(String serviceId) {
		return getServiceDefinition();
	}
	
	private ServiceDefinition getServiceDefinition() {
		return new ServiceDefinition(
				"aerospike-service-broker",
				"aerospike",
				"Service Broker implementation for an Aerospike database",
				true,
				false,
				getPlans(),					
				Arrays.asList("aerospike", "nosql"),
				getServiceDefinitionMetadata(),
				null,
				null);
	}
	
	private Map<String,Object> getPlanMetadata() {
		Map<String, Object> planMetadata = new HashMap<>();
		planMetadata.put("costs", getCosts());
		planMetadata.put("bullets", getBullets());
		return planMetadata;
	}
	
	private List<Plan> getPlans() {
		Map<String, Map<String,String>> namespaces = adminService.getNamespaceInfo();
		List<Plan> plans = new ArrayList<Plan>();
		
		for (String ns : namespaces.keySet()) {
			Map<String, String> info = namespaces.get(ns);
			plans.add(new Plan(ns,
					ns,
					"Storage:" +                info.get(STORAGE_ENGINE_KEY) + 
					", Size:"  +                info.get(MEMORY_SIZE_KEY) +
					", Replication Factor:" +   info.get(REPLICATION_FACTOR_KEY),
					getPlanMetadata(), true));
		}
		return plans;
	}
	
	private Map<String, Object> getServiceDefinitionMetadata() {
		Map<String, Object> sdMetadata = new HashMap<>();
		sdMetadata.put("displayName", "Aerospike");
		sdMetadata.put("imageUrl", "http://www.datamojo.com/wp-content/uploads/2013/07/aerospike_logo_square1.png");
		sdMetadata.put("longDescription", "Aerospike Service");
		sdMetadata.put("providerDisplayName", "Aerospike");
		sdMetadata.put("documentationUrl", "https://github.com/aerospike/aerospike-service-broker");
		sdMetadata.put("supportUrl", "https://github.com/aerospike/aerospike-service-broker");
		return sdMetadata;
	}
	
	private List<Map<String,Object>> getCosts() {
		return Collections.singletonList(new HashMap<>());
	}
	
	private List<String> getBullets() {
		return Arrays.asList("Shared Aerospike server", 
				"100 MB Storage (not enforced)");
	}
}
