# Reactive

// TODO
Here, we want to explain the following:

- Reactive systems in general. Back-pressure specifically. Also teardown of streams in case of errors.
- How backpressure even works across HTTP connections
- Compare with JMS Synchronous (blocking) and Asynchronous (no back pressure)
- A bit of history of Netflix / Akka / Other reactive systems
- How 'Reactive Streams' spec came to life, and later java.util.concurrent.Flow
- How you can't really use Reactive Streams directly
- How Mutiny is one of the implementations
- How to convert between mutiny and reactive streams (easy; inheritance)
  
- Relation to SSE and WebSockets
- When you need to buffer, or drop elements, or conflate elements (can we demo this? Rate limited API to which
  we want to push prices maybe?)




