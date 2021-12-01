# Fonctionnalités techniques


## Connaissance attendues

A l’issue de ce module vous devriez:
* Savoir comment ajouter des vérifications de la santé de l’application
* Comprendre les différentes approches pour collecter des métriques
* Comprendre comment ajouter de la traçabilité


## Supervision
* L’action et les outils pour observer notre application sur une large période
    * Observer le statut global et mesurer le comportement
* C’est particulièrement pertinent dans un contexte d’environnements cloud (et kubernetes)

Note:
* On peut souligner ici la pertinence d'inclure le monitoring (bilans de santé, métriques, etc.) dans une formation Quarkus
  * Dans le contexte natif du cloud (le contexte principal pour Quarkus), les décisions de mise à l'échelle d'un service sont généralement basées sur l'état de disponibilité et de préparation, ainsi que sur des mesures de la façon dont le service réagit à la charge


## Liveness and Readiness
* L’extension Smallrye Health de Quarkus  implémente la spécification MicroProfile Health
* Ajouter cette extension fournit immédiatement des points d’entrées
    * `/health`
    * `/health/live`
    * `/health/ready`
* Liveness - qu’on peut traduire par vitalité - Le service est-il _démarré_ ou _éteint_, _joignable_ ou _injoignable_ ?
* Readiness - qu’on peut traduire par le fait d’être prêt - Le service peut-il traiter des requêtes utilisateurs ?
    * Un service peut être démarré et passer la vérification du Liveness mais échouer la vérification de Readiness
* Ceci correspond aux sondes liveness et readiness de Kubernetes


## Métriques (avec MicroProfile Metrics)
* L’extension Quarkus Smallrye Metrics implémente la spéc. MicroProfile Metrics
* Ajouter cette extension fournit immédiatement des points d’entrées:
    * `/metrics`
    * `/metrics/base`
    * `/metrics/vendor`
    * `/metrics/application`
* Supporte des retours en JSON et [OpenMetrics](https://openmetrics.io/)
    * OpenMetrics est un projet de la sandbox CNCF (Cloud Native Computing Fundation)
    * Le standard OpenMetrics est basé sur les représentations de Prometheus


## Métriques (avec Micrometer)
* L’approche recommandée !
* [Micrometer](https://micrometer.io/) est le pendant de SLF4J pour les métriques
    * Une façade indépendante des implémentations éditeurs
* Extension Quarkus Micrometer avec une registry Prometheus
* Ajouter cette extension fournit immédiatement le point d’entrée `/metrics`
    * Au format Prometheus, avec des métriques applicatives comme système, JVM et HTTP
    * Le format JSON peut également être activé par la configuration


## Traces
* L’extension Quarkus Smallrye OpenTracing implémente la spec. MicroProfile OpenTracing
* Utilise le traçeur [Jaeger](https://www.jaegertracing.io/)
* Les identifiant de traces peuvent être enregistrés avec la propagation MDC
* Il est possible de configurer le suivi des requêtes JDBC et de la remise des messages Kafka

Note:
Si nécessaire, voici une possible configuration de journalisation pour consigner les trace ID

```
quarkus.log.console.format=%d{HH:mm:ss} %-5p traceId=%X{traceId}, parentId=%X{parentId}, spanId=%X{spanId}, sampled=%X{sampled} [%c{2.}] (%t) %s%e%n
```


<!-- .slide: data-background="#abcdef" -->
## Exercise: Observability (Bonus)

* Ajouter dans `application.properties`

```
quarkus.log.console.format=%d{HH:mm:ss} %-5p traceId=%X{traceId}, parentId=%X{parentId}, spanId=%X{spanId}, sampled=%X{sampled} [%c{2.}] (%t) %s%e%n
```
* Ajouter un JBoss Logger dans `ProductsResource` et `PriceUpdatesResource` et logger quelques endpoints

```java
import org.jboss.logging.Logger;

private static final Logger LOGGER = Logger.getLogger(Foo.class)
```

* Ajouter les extensions:
    * Smallrye Health
    * Quarkus Micrometer
    * Quarkus Smallrye OpenTracing


## Recap

Dans ce module, nous avons vus:
* Comment ajouter des vérifications de liveness et readiness
* Deux façons permettant la collecte de métriques dans notre application Quarkus
* Comment activer le traçage distribué
