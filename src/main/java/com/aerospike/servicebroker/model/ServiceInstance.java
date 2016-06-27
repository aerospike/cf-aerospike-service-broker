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
package com.aerospike.servicebroker.model;

import java.io.Serializable;

import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest;

public class ServiceInstance implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7675160182927500963L;
	
	private String id;
	private String serviceDefinitionId;
	private String planId;
	private String organizationGuid;
	private String spaceGuid;
	private String dashboardUrl;
	private String namespace;
	
	@SuppressWarnings("unused")
	private ServiceInstance() {}

	public ServiceInstance(String serviceInstanceId, String serviceDefinitionId, String planId,
						   String organizationGuid, String spaceGuid, String dashboardUrl, String namespace) {
		this.id = serviceInstanceId;
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		this.organizationGuid = organizationGuid;
		this.spaceGuid = spaceGuid;
		this.dashboardUrl = dashboardUrl;
		this.namespace = namespace;
	}

	/**
	 * Create a ServiceInstance from a create request and a namespace. If fields
	 * are not present in the request they will remain null in the
	 * ServiceInstance.
	 * @param request containing details of ServiceInstance
	 */
	public ServiceInstance(CreateServiceInstanceRequest request, String namespace) {
		this.serviceDefinitionId = request.getServiceDefinitionId();
		this.planId = request.getPlanId();
		this.organizationGuid = request.getOrganizationGuid();
		this.spaceGuid = request.getSpaceGuid();
		this.id = request.getServiceInstanceId();
		this.namespace = namespace;
	}

	/**
	 * Create a service instance from an update request. If fields
	 * are not present in the request they will remain null in the
	 * ServiceInstance.
	 * @param request containing details of ServiceInstance
	 */
	public ServiceInstance(UpdateServiceInstanceRequest request) {
		this.id = request.getServiceInstanceId();
		this.planId = request.getPlanId();
	}

	public String getServiceInstanceId() {
		return this.id;
	}

	public String getServiceDefinitionId() {
		return this.serviceDefinitionId;
	}

	public String getPlanId() {
		return this.planId;
	}

	public String getOrganizationGuid() {
		return this.organizationGuid;
	}

	public String getSpaceGuid() {
		return this.spaceGuid;
	}

	public String getDashboardUrl() {
		return this.dashboardUrl;
	}

	public ServiceInstance and() {
		return this;
	}
	
	public String getNamespace() {
		return this.namespace;
	}

	public ServiceInstance withDashboardUrl(String dashboardUrl) {
		this.dashboardUrl = dashboardUrl;
		return this;
	}
}
