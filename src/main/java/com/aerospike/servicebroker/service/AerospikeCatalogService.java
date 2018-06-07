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

import com.aerospike.servicebroker.config.AerospikeCatalogConfig;

@Service
public class AerospikeCatalogService implements CatalogService {
	private static final String STORAGE_ENGINE_KEY = "storage-engine";
	private static final String MEMORY_SIZE_KEY = "memory-size";
	private static final String REPLICATION_FACTOR_KEY = "effective_replication_factor";
	
	private final String serviceId;
	private final String serviceName;
	private final String serviceDescription;
	
	@Autowired
	AerospikeAdminService adminService;
	
	@Autowired
	public AerospikeCatalogService(AerospikeCatalogConfig config) {
		this.serviceName = config.serviceName;
		this.serviceId = config.serviceId;
		this.serviceDescription = config.serviceDescription;
	}
	
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
				this.serviceId,
				this.serviceName,
				this.serviceDescription,
				true,
				false,
				getPlans(),					
				Arrays.asList("aerospike", "nosql"),
				getServiceDefinitionMetadata(),
				null,
				null);
	}
	
	private Map<String,Object> getPlanMetadata(Map<String, String> info) {
		Map<String, Object> planMetadata = new HashMap<>();
		planMetadata.put("costs", Collections.singletonList(new HashMap<>()));
		planMetadata.put("bullets", getBullets(info));
		return planMetadata;
	}
	
	private String getDescription(Map<String, String> info) {
		return "Storage:" + info.get(STORAGE_ENGINE_KEY) +
				", Size:" + info.get(MEMORY_SIZE_KEY) +
				", Replication Factor:" + info.get(REPLICATION_FACTOR_KEY);
	}
	
	private List<Plan> getPlans() {
		Map<String, Map<String,String>> namespaces = adminService.getNamespaceInfo();
		List<Plan> plans = new ArrayList<Plan>();
		
		for (String ns : namespaces.keySet()) {
			Map<String, String> info = namespaces.get(ns);
			plans.add(new Plan(ns,
					ns,
					getDescription(info),
					getPlanMetadata(info), true));
		}
		return plans;
	}
	
	private Map<String, Object> getServiceDefinitionMetadata() {
		Map<String, Object> sdMetadata = new HashMap<>();
		sdMetadata.put("displayName", "Aerospike");
		sdMetadata.put("imageUrl", "https://www.aerospike.com/assets/images/icons/apple-icon-144x144.png");
		sdMetadata.put("longDescription", "Aerospike Service");
		sdMetadata.put("providerDisplayName", "Aerospike");
		sdMetadata.put("documentationUrl", "https://github.com/aerospike/aerospike-service-broker");
		sdMetadata.put("supportUrl", "https://github.com/aerospike/aerospike-service-broker");
		return sdMetadata;
	}
	
	private List<String> getBullets(Map<String, String> info) {
		return Arrays.asList("Storage: " + info.get(STORAGE_ENGINE_KEY), 
				"Size: " + info.get(MEMORY_SIZE_KEY),
				"Replication Factor: " + info.get(REPLICATION_FACTOR_KEY));
	}
}
