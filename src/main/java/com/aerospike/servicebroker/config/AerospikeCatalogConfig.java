package com.aerospike.servicebroker.config;

public class AerospikeCatalogConfig {
	public final String serviceName;
	public final String serviceId;
	public final String serviceDescription;
	
	public AerospikeCatalogConfig(String serviceId, String serviceName, String serviceDescription) {
		this.serviceId = serviceId;
		this.serviceName = serviceName;
		this.serviceDescription = serviceDescription;
	}
}
