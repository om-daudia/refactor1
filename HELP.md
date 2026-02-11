# Camunda 8 JobWorker Template

Copy and modify this project for your needs.

Out of the box:
1) Spring Zeebe Client
2) JobWorker Example
3) JobWorker Service Example
4) Dockerfile
5) gitlab ci/cd pipeline
6) Sonar Qube (requires config on SQ server side)

## Configuring Camunda 8 connections

Connections to **Camunda SaaS** can be configured by creating the following entries in your src/main/resources/application.properties:

```properties
zeebe.client.cloud.clusterId=xxx
zeebe.client.cloud.clientId=xxx
zeebe.client.cloud.clientSecret=xxx
zeebe.client.cloud.region=bru-2
```
You can also configure the connection to a **Self-Managed** Zeebe broker:

```properties
zeebe.client.broker.gateway-address=127.0.0.1:26500
zeebe.client.security.plaintext=true
```
You can enforce the right connection mode, for example if multiple contradicting properties are set:

```properties
zeebe.client.connection-mode=CLOUD
zeebe.client.connection-mode=ADDRESS
```
You can also configure other components like Operate. If you use different credentials for different components:

```properties
camunda.operate.client.clientId=xxx
camunda.operate.client.clientSecret=xxx
```

Otherwise, if you use same credentials across all components:

```properties
common.clientId=xxx
common.clientSecret=xxx
```

The full documentation located here: https://github.com/camunda-community-hub/spring-zeebe/?tab=readme-ov-file#configuring-camunda-8-saas-connection