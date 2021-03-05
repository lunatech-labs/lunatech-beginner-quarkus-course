# Non Functional Features


## Learning outcomes

After this module, you should:
* Understand how to add health checks
* Understand the different approaches to gathering metrics
* Understand how to add tracing


## Monitoring
* Task of / tools for observing our application over a period of time
    * Observing overall status and measuring behaviour
* Especially relevant in cloud-native (and K8s-native) context

Note:
* Here we can emphasise the relevance of including monitoring (health-checks, metrics, etc.) in a Quarkus training course
    * In the cloud-native context (the main context for Quarkus) decisions for scaling up and down a service is typically based on liveness and readiness status, as well as metrics of how the service is responding to load


## Liveness and Readiness
* Quarkus Smallrye Health extension (implementation of MicroProfile Health)
* Adding extension gives out-of-the-box endpoints
    * `/health`
    * `/health/live`
    * `/health/ready`
* Liveness - Is the service _up/down_, _reachable/unreachable_?
* Readiness - Can the service handle user requests?
    * A service can be "UP" and pass the Liveness check but fail the Readiness check
* Correspond to liveness and readiness Kubernetes probes


## Metrics (w/ MicroProfile Metrics)
* Quarkus Smallrye Metrics extension (implementation of MicroProfile Metrics)
* Adding extension gives out-of-the-box endpoints
    * `/metrics`
    * `/metrics/base`
    * `/metrics/vendor`
    * `/metrics/application`
* Supports returning both JSON and [OpenMetrics](https://openmetrics.io/)
    * OpenMetrics is a CNF sandbox project
    * OpenMetrics standard is based on Prometheus representation


## Metrics (w/ Micrometer)
* The recommended approach!
* [Micrometer](https://micrometer.io/) is the SLF4J for metrics
    * a vendor-neutral façade
* Quarkus Micrometer extension (with Prometheus registry)
* Adding extension gives out-of-the-box `/metrics` endpoint
    * Prometheus-formatted, with application metrics as well as system, jvm, and http metrics
    * JSON formatting can be enabled through config


## Tracing
* Quarkus Smallrye OpenTracing (implementation of MicroProfile OpenTracing)
* Uses [Jaeger](https://www.jaegertracing.io/) tracer
* Trace IDs can be logged via MDC propagation
* Can configure to trace JDBC requests and Kafka message deliver

Note:
If needed, here is a possible log configuration to log trace IDs

```
quarkus.log.console.format=%d{HH:mm:ss} %-5p traceId=%X{traceId}, parentId=%X{parentId}, spanId=%X{spanId}, sampled=%X{sampled} [%c{2.}] (%t) %s%e%n
```


<!-- .slide: data-background="#abcdef" -->
## Exercise: Observability (Bonus)


## Recap

In this module we have:
* Seen how to add liveness and readiness checks
* Seen two ways of enabling metrics gathering on our Quarkus app
* Seen how to enable distributed tracing
