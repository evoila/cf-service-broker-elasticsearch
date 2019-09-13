# Service Broker

This repository is part of our service broker project. For documentation see [evoila/osb-docs](https://github.com/evoila/osb-docs).

## osb-elasticsearch

Cloud Foundry Service Broker providing Elasticsearch Service Instances. Supports deployment to BOSH. Uses MongoDB for management. Uses MongoDB or CredHub for storing credentials. Configuration files and deployment scripts must be added.  

## Features

Our Elasticsearch service broker is rich in features whose functionality is documented in this section.

This documentation of the "Elasticsearch service broker" may not be complete at this time and will be continuously updated.

### Binding

All access data, whether from bindings created by the operator or from built-in users, are stored via CredentialStore. This means that the service broker automatically recognizes how to save the credentials.

If Credhub is configured according to the [evoila/osb-docs](https://github.com/evoila/osb-docs), credentials are safely stored in Credhub and will not show up in log files or deployment manifests. Otherwise the broker stores credentials in the mongoDB.

#### Ingress and egress binding

When you create a new service binding in cloudfoundry, you can provide an client mode, either ingress or egress. The broker automatically filters the nodes and returns only the IPs corresponding to the client mode passed. If you do not specify a client mode, egress will be used.

Example:

`cf bind-service APP_NAME SERVICE_INSTANCE -c '{"clientMode":"ingress"}'`

`cf bind-service APP_NAME SERVICE_INSTANCE -c '{"clientMode":"egress"}'`

#### Built-In user credentials

An Elasticsearch installation includes a small number of built-in users for example to have access to Kibana. In order to obtain these special access data, a new binding must be created and the corresponding client mode must be specified.

To obtain **Admin** credentials use `cf bind-service APP_NAME SERVICE_INSTANCE -c '{"clientMode":"superuser"}'`

To obtain **Kibana** credentials use `cf bind-service APP_NAME SERVICE_INSTANCE -c '{"clientMode":"kibana"}'`

To obtain **Logstash** credentials use `cf bind-service APP_NAME SERVICE_INSTANCE -c '{"clientMode":"logstash"}'`

## Versions

| Spec  | Version  |
|---|---|
| OSB-API Version  | [2.14](https://github.com/openservicebrokerapi/servicebroker/blob/v2.14/spec.md) |
| Java Version  | Java 8  |
