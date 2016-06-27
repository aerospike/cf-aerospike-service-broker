## Aerospike Service Broker for Cloud Foundry

This project implements the Cloud Foundry Service Broker interface for an Aerospike database. The service uses the Aerospike DB to persist service instances and bindings.

### Getting Started

This is a Spring Boot application and is designed to be embedded into either the [cf-service-broker-tile](https://github.com/aerospike/cf-service-broker-tile.git) project or the [cf-managed-service-tile](https://github.com/aerospike/cf-managed-service-tile.git) project. 

The cf-service-broker-tile is used to provide a Cloud Foundy Service Broker implementation for an existing Aerospike database. 

The cf-managed-service-tile is used to create an Aerospike database from within Cloud Foundry and also includes the Service Broker implementation for application binding.

### Build

In order to build the Spring Boot jar which will be embedded in one of the Cloud Foundry tile projects mentioned above, run this command:

```gradle assemble```

This will generate the Spring Boot jar file in the ```build/lib``` directory.

### Creating Cloud Foundry Tile

See the documentation for the [cf-service-broker-tile](https://github.com/aerospike/cf-service-broker-tile.git) or the [cf-managed-service-tile](https://github.com/aerospike/cf-managed-service-tile.git) project to see how to include the Spring Boot jar to create a Pivotal Cloud Foundy tile.



