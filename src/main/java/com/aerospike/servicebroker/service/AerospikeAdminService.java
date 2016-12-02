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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.Info;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.admin.Privilege;
import com.aerospike.client.admin.PrivilegeCode;
import com.aerospike.client.cluster.Node;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.servicebroker.config.AerospikeClientConfig;
import com.aerospike.servicebroker.exception.AerospikeServiceException;
import com.aerospike.servicebroker.model.ServiceInstance;
import com.aerospike.servicebroker.model.ServiceInstanceBinding;

@Service
public class AerospikeAdminService {
	// These could be externalized
	private static final String ADMIN_BINDING = "binding";
	private static final String ADMIN_SERVICE = "service";

	private static final String SERVICE_INSTANCE_BINNAME = "instance";
	private static final String SERVICE_BINDING_BINNAME = "binding";
	
	private static final String NAMESPACES_INFO = "namespaces";
	
	private static final String ENTERPRISE = "enterprise";
	
	private Logger logger = LoggerFactory.getLogger(AerospikeAdminService.class);
	
	private AerospikeClient client;
	private Map<String, Map<String,String>> namespaceInfo = new HashMap<String, Map<String,String>>();
	
	private AerospikeClientConfig config;
	
	@Autowired
	public AerospikeAdminService(AerospikeClientConfig config) {
		logger.info("Intializing Admin Service");
		this.config = config;
		this.client = getClient();
		
		Set<String>	    namespaces;		
		namespaces = new HashSet<String>(Arrays.asList(Info.request(this.client.getNodes()[0], NAMESPACES_INFO).split(";")));
		if (!namespaces.contains(config.adminNamespace)) {
			throw new AerospikeServiceException("Namspace " + config.adminNamespace +
						" must be configured in order to use the service broker with this database.");
		}
		
		for (String ns : namespaces) {
			if (!ns.equalsIgnoreCase(config.adminNamespace)) {
				String info = Info.request(this.client.getNodes()[0], "namespace/" + ns);
				String[] infos = info.split(";");
				Map<String, String> infoHash = new HashMap<String, String>();
				
				for (String inf : infos) {
					String[] data = inf.split("=");
					if (data != null && data.length == 2) {
						infoHash.put(data[0], data[1]);
					}
				}
				namespaceInfo.put(ns, infoHash);
			}
		}
	}
	
	private AerospikeClient getClient() {
		ClientPolicy policy = new ClientPolicy();
		policy.failIfNotConnected = true;
			
		if (ENTERPRISE.equalsIgnoreCase(config.licenseType)) {
			policy.user = config.user;
			policy.password = config.password;
		}
		AerospikeClient client =  new AerospikeClient(policy, config.hostname, config.port);
		System.out.println("CONNECTED ? " + client.isConnected());
		return client;
	}
	
	public boolean serviceExists(String serviceId) {
		Key key = new Key(config.adminNamespace, ADMIN_SERVICE, serviceId);
		try {
			return this.client.exists(null, key);
		} catch (AerospikeException ae){
			System.out.println("serviceExists AerospikeException: " + ae.getMessage());
			return false;
		}
	}
	
	public boolean namespaceExists(String namespace) {
		return this.namespaceInfo.containsKey(namespace);
	}

	public void createService(ServiceInstance serviceInstance) {
		if (serviceInstance != null) {
			Key key = new Key(config.adminNamespace, ADMIN_SERVICE, serviceInstance.getServiceInstanceId());
			Bin bin = new Bin(SERVICE_INSTANCE_BINNAME, serviceInstance);
			this.client.put(null, key, bin);
		}
	}
	
	public ServiceInstance getService(String serviceId) {
		ServiceInstance service = null;
		Key key = new Key(config.adminNamespace, ADMIN_SERVICE, serviceId);
		try {
			Record record = this.client.get(null, key);
			
			if (record != null) {
				service = (ServiceInstance)record.getValue(SERVICE_INSTANCE_BINNAME);
			}
		} catch (AerospikeException ae) {
			System.out.println("getService AerospikeException: " + ae.getMessage());
		}
		return service;
	}
	
	public void deleteService(ServiceInstance serviceInstance) {
		if (serviceInstance != null) {
			Key key = new Key(config.adminNamespace, ADMIN_SERVICE, serviceInstance.getServiceInstanceId());
			try {
				this.client.delete(null, key);
			} catch (AerospikeException ae) {
				System.out.println("deleteService AerospikeException: " + ae.getMessage());
			}
		}
	}
	
	public boolean serviceBindingExists(String serviceBindingId) {
		System.out.println("CHECKING IF SERVICE INSTANCE BINDING EXISTS : " + serviceBindingId);
		Key key = new Key(config.adminNamespace, ADMIN_BINDING, serviceBindingId);
		try {
			return this.client.exists(null, key);
		} catch (AerospikeException ae) {
			System.out.println("serviceBindingExists AerospikeException: " + ae.getMessage());
			return false;
		}
	}
	
	public void createServiceBinding(ServiceInstanceBinding binding) {
		System.out.println("CREATING SERVICE INSTANCE BINDING : " + binding.getId());
		if (binding != null) {
			Key key = new Key(config.adminNamespace, ADMIN_BINDING, binding.getId());
			Bin bin = new Bin(SERVICE_BINDING_BINNAME, binding);
			this.client.put(null, key, bin);
		}
	}
	
	public ServiceInstanceBinding getServiceBinding(String serviceBindingId) {
		System.out.println("GETTING SERVICE INSTANCE BINDING : " + serviceBindingId);
		ServiceInstanceBinding binding = null;
		Key key = new Key(config.adminNamespace, ADMIN_BINDING, serviceBindingId);
		try {
			Record record = this.client.get(null, key);
			if (record != null) {
				binding = (ServiceInstanceBinding)record.getValue(SERVICE_BINDING_BINNAME);
			}
		} catch (AerospikeException ae) {
			System.out.println("getServiceBinding AerospikeException: " + ae.getMessage());
		}
		return binding;
	}
	
	public void deleteServiceBinding(ServiceInstanceBinding binding) {
		if (binding != null) {
			Key key = new Key(config.adminNamespace, ADMIN_BINDING, binding.getId());
			try {
				this.client.delete(null, key);
			} catch (AerospikeException ae) {
				System.out.println("deleteServiceBinding AerospikeException: " + ae.getMessage());
			}
		}
	}
	
	public Map<String, Map<String, String>> getNamespaceInfo() {
		return namespaceInfo;
	}
	
	public String getHostname() {
		return this.client.getNodes()[0].getHost().name;
	}
	
	public int getPort() {
		return this.client.getNodes()[0].getHost().port;
	}
	
	public String[] getHosts() {
		Node[] nodes = this.client.getNodes();
		String[] hosts = new String[nodes.length];
		for (int i=0; i<nodes.length; i++) {
			hosts[i] = nodes[i].getHost().toString();
		}
		
		return hosts;
	}
	
	private String formatUserRole(String prefix, String key) {
		return (prefix + key.replaceAll("-", "")).substring(0, 30);
	}
	
	public String createUser(String user, String password, String namespace, String set) {
		String userName = null;
		if (config.licenseType.equalsIgnoreCase(ENTERPRISE)) {
			Privilege p = new Privilege();
			p.code = PrivilegeCode.READ_WRITE_UDF;
			p.namespace = namespace;
			p.setName = set;
			
			String roleName = formatUserRole("r", user);
			userName = formatUserRole("u" , user);
			
			int retries = 5;
			boolean createdRole = false;
			boolean createdUser = false;

			do {
				try {
					if (!createdRole) {
						System.out.println("CREATING ROLE: " + roleName);
						this.client.createRole(null, roleName, Collections.singletonList(p));
						createdRole = true;
					}
					if (!createdUser) {
						System.out.println("CREATING USER: " + userName);
						this.client.createUser(null, userName, password, 
								Collections.singletonList(roleName));
						createdUser = true;
					}
					System.out.println("CREATED USER/ROLE");
				} catch (AerospikeException ae) {
					System.out.println("createRole/User AerospikeException: " + ae.getMessage());
				} 
			} while (!(createdRole && createdUser) && retries-- > 0);
			
			if (!(createdRole && createdUser)) {
				throw new AerospikeServiceException("Could not bind service. Please try again.");
			}
		}
		return userName;
	}
	
	public void dropUser(String user) {
		if (config.licenseType.equalsIgnoreCase(ENTERPRISE)) {
			boolean droppedUser = false;
			boolean droppedRole = false;			
			int retries = 5;
			String userName = formatUserRole("u", user);
			String roleName = formatUserRole("r", user);
			
			do {			
				try {
					if (!droppedUser) {
						System.out.println("DROPPING USER: " + userName);
						this.client.dropUser(null, userName);
						droppedUser = true;
					}
					if (!droppedRole) {
						System.out.println("DROPPING ROLE: " + roleName);
						this.client.dropRole(null, roleName);
						droppedRole = true;
					}
					System.out.println("DROPPED USER/ROLE");
				} catch (AerospikeException ae) {
					System.out.println("dropRole/User AerospikeException: " + ae.getMessage());
				}
			} while (!(droppedRole && droppedUser) && retries-- > 0);
		}
	}
}
