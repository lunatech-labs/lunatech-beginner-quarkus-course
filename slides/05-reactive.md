# Reactive

// TODO
Here, we want to explain the following:

- Reactive systems in general. Back-pressure specifically (see https://quarkus.io/blog/mutiny-back-pressure/). Also teardown of streams in case of errors.
- How backpressure even works across HTTP connections
- Compare with JMS Synchronous (blocking) and Asynchronous (no back pressure)
- A bit of history of Netflix / Akka / Other reactive systems
- How 'Reactive Streams' spec came to life, and later java.util.concurrent.Flow
- How you can't really use Reactive Streams directly
- How Mutiny is one of the implementations
- How to convert between mutiny and reactive streams (easy; inheritance)
- Message acknowledgement  
- Relation to SSE and WebSockets
- When you need to buffer, or drop elements, or conflate elements (can we demo this? Rate limited API to which
  we want to push prices maybe?)


// 
Practically, we want to show:
- Injecting publisher or multi
- Filtering
- Supporting multiple subscribers
- Multiple output types (plain text, json)
- Dead letter queue
- Health & Metrics integration
- Message<T> instead of <T>




