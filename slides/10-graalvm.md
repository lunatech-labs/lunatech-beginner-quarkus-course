# GraalVM


## Learning outcomes

After this module, you should know:
* What Graal VM is and his role
* How to compile an application into a native executable
* How to test a native executable


## Introduction

GraalVM is a JDK distribution allowing to optimize applications in JVM languages (Java, Scala, ...) and also supports other languages such as JavaScript or Python for example.

GraalVM can therefore mix several programming languages in a single application while eliminating the costs of external calls.

Note:
* GraalVM increases the performance of Java applications by around 32% on average


## Features

* Graal Compiler
* GraalVM Native Image
* Truffle Language Implementation Framework and GraalVM SDK
* LLVM Runtime and JavaScript Runtime

Note:
* Graal Compiler, new JIT compiler for Java
* GraalVM Native Image for AOT compilation -> Direct compilation of Java code into native machine code
* Truffle Language Implementation Framework and GraalVM SDK providing runtime environments for other programming languages
* LLVM Runtime and JavaScript Runtime, runtime environments for LLVM and JavaScript
* Possibility to install other languages and define our own


## Architecture
![GraalVM Architecture](images/graalvm/architecture.jpeg)


## Distributions of GraalVM

3 distributions :
* Oracle GraalVM Community Edition (CE)
* Oracle GraalVM Enterprise Edition (EE)
* Mandrel


## Mandrel

Mandrel’s main goal is to provide a way to build native executables specifically designed to support Quarkus and that target Linux containerized environments.

Mandrel releases are built from a code base derived from the upstream Oracle GraalVM CE code base, with only minor changes but some significant exclusions that are not necessary for Quarkus native apps.


## Native executable compilation

Use this command line to compile

```
./mvnw package -Pnative
```


## Test the native executable

```
./mvnw verify -Pnative
```


## Full display of errors

* In the pom.xml file, add `<trimStackTrace>false</trimStackTrace>`

```xml
<artifactId>maven-failsafe-plugin</artifactId>
<version>${surefire-plugin.version}</version>
<configuration>
  <trimStackTrace>false</trimStackTrace>
</configuration>
```


## Test without compile again

To rerun tests with an existing native image

```
./mvnw test-compile failsafe:integration-test
```


## Performances

In the case of this course:

_Not in native-image executable_
```text
Boot time       between 3 and 3.5 seconds
Memory used     approx. 530M
```

_In native-image executable_
```text
Boot time between 0.07 and 1.2 second
Memory used approx. 52M
```


<!-- .slide: data-background="#abcdef" -->
## Exercise: Use Graal VM


## Recap

In this module we have :
* Learnt what Graal VM is and his goal
* Compiled an application into a native executable
* Tested a native executable