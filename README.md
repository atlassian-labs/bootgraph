# BootGraph - Visualize Your Spring Boot Architecture

BootGraph is a library that takes a Spring application context as input and creates one or more visual representations of it. 

With a couple lines of code, you can create a visual graph of part your Spring application that helps you understand the codebase better. 

If you put these lines of code into a test, **the graph will be created with every build and evolve together with your code**. No more lying diagrams!
  
## Installation

Bootgraph is published via [jitpack.io](https://jitpack.io/). 

### Installing in Maven

Add the jitpack.io repository:
```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```

Add the dependency:
```xml
<dependency>
  <groupId>com.github.atlassian-labs</groupId>
  <artifactId>bootgraph</artifactId>
  <version>1.2.4</version>
  <scope>test</scope>
</dependency>
```

### Installing in Gradle

Add the jitpack.io repository:
```groovy
repositories {
  ...
  maven { url 'https://jitpack.io' }
}
```

Add the dependency:
```groovy
dependencies {
  ...
  testImplementation 'com.github.atlassian-labs:bootgraph:1.2.4'
}
```

## Getting Started

### Basic Usage

Create a JUnit test that has access to your Spring application context and configure a `BootGraph` object:

```java
@SpringBootTest
public class BootGraphTests {

  @Autowired
  private ConfigurableApplicationContext applicationContext;

  @Test
  public void createApplicationGraph() {
    BootGraph.builder()
        .applicationName("my application")
        .applicationContext(applicationContext)
        .export();
  }
}
```

By calling `export()`, BootGraph will create a model of the application context and create a graph (by default to the file `target/bootgraph/bootgraph.png`).

### Telling BootGraph Which Beans to Include in the Graph
BootGraph doesn't know which beans to include in the output graph, so we have to help it out. To do this, we need to implement a `BeanMatcher` and a `BeanMapper`: 

* A `BeanMatcher` matches certain beans in the application context (for instance all beans that implement a certain interface or have a certain annotation). BootGraph will pass all these beans into a `BeanMapper`.
* A `BeanMapper` takes a Spring bean as input and maps it to a `Node` object, which will later be a node in the graph. We can add dependencies from one `Node` to another, which are later represented as arrows in the graph.

Code example:

```java
@SpringBootTest
public class BootGraphTests {

  @Autowired
  private ConfigurableApplicationContext applicationContext;

  @Test
  public void createApplicationGraph() {
    BootGraph.builder()
        .applicationName("my application")
        .mapper(new AnnotationMatcher<>(MessageHandler.class), new MessageHandlerMapper())
        .applicationContext(applicationContext)
        .export();
  }
}
```

In this example, we use an `AnnotationMatcher` to tell BootGraph to match all Spring beans annotated with `@RxSqsListener`. These listeners handle messages from a queue and we want to visualize which queue delivers messages to which listener.

The beans are mapped into the graph model using a `RxSqsMessageHandlerMapper`:

```java
class MessageHandlerMapper implements BeanMapper {

  @NotNull
  @Override
  public Node mapToModel(
          Object messageHandlerBean,
          @NotNull String beanName,
          @NotNull BeanDefinition beanDefinition,
          @NotNull NodeFactory nodeFactory) {
    
    MessageHandler annotation = messageHandlerBean.getClass()
      .getAnnotation(MessageHandler.class);
    
    String name = messageHandlerBean.getClass().getSimpleName();
    
    Node messageHandler = nodeFactory.internalNode(name);
    
    Node queue = nodeFactory
      .externalNode(annotation.queueSettings().name());
    
    messageHandler.addInputFrom(queue, annotation.inputType().getSimpleName());
    return messageHandler;
  }
}
```

This mapper extracts information from the custom `@MessageHandler` annotation and creates a `Node` for the queue this message handler receives messages from, and another one for the message handler itself. Finally, it connects the nodes to each other via `addInputFrom()`.

BootGraph will take it from there and create a graph showing all queues, all message handlers, and all connections between them. **BootGraph will automatically fill in connections between Spring beans by analyzing the dependencies in the Spring application context**.

### Marking Input and Output Beans

Instead of creating your own `BeanMatchers` and `BeanMappers`, you can use the default configuration, which will search for all beans annotated with `@InputNode` and `@OutputNode`:

```java
@Component
@InputNode(name = "messageHandler", connectionLabel = "messages", source = "queue")
class MessageHandler {
  ...
}

@Component
@OutputNode(name = "messagePublisher", connectionLabel = "messages", target = "queue")
class MessagePublisher {
  ...
}
```

or, using Spring's Java Config:

```java
@Configuration
class MyConfiguration {

    @Bean
    @InputNode(name = "messageHandler", connectionLabel = "messages", source = "queue")
    MessageHandler messageHandler(){
        return new MessageHandler();
    }

    @Bean
    @OutputNode(name = "messagePublisher", connectionLabel = "messages", target = "queue")
    MessagePublisher messagePublisher() {
        return new MessagePublisher();
    }
}
```

These annotations will be evaluated without adding any custom `BeanMatcher`s or `BeanMapper`s. The output graph will include all annotated beans.

---
**NOTE**

**If you have to mark your beans with documentation-only annotations like `@InputNode` and `@OutputNode` you're basically cheating!** 

It means that the beans in your application context are not architecturally expressive enough to create a visualization. 

Also, the chance is high that the information within the annotation will diverge from the actual code at some point in the future.

Consider refactoring the code so that each architecturally relevant element is in its own class that can be handled by a specific `BeanMapper`. The codebase will be easier to navigate for humans and the visualization created by BootGraph will be closer to the code.

--- 

### Filtering the Graph

An application may contain a lot of beans and we don't always want to see all of them in the same visualization. You can provide a filter to only include certain beans in the graph:

```java
BootGraph.builder()
  .applicationName("my application")
  .applicationContext(applicationContext)
  .filter(new InputPathFilter("queue"))
  .export();
```

Here, we applied an `InputPathFilter`, which filters the graph to only show those beans that can be reached from a certain starting node.
 
You can also implement your own `GraphModelFilter`.

### Configuring the Export Format

The output of BootGraph can be configured by creating an `ExportConfiguration` object:

```java
BootGraph.builder()
  .applicationName("my application")
  .applicationContext(applicationContext)
  .exportConfig(ExportConfiguration.builder()
    .outputFilePath(String.format("target/bootgraph/graph.png", filename))
    .outputFormat(OutputFormat.PNG)
    .showLabelsOnArrows(false)
    .arrowFormat(ArrowFormat.SPLINE)
    .fontName("Arial")
    .nodeWidthInInches(4f)
    .nodeHeightInInches(1f)
    .widthInPixels(2000)
    .heightInPixels(1000)
    .build())
  .export();
```

All options have sensible default values so you can change the ones you need and omit the rest.

### Evaluating Spring Expression Language (SpEL)

All expressions like `${foo.bar}` will be resolved against the Spring application context so that you can use expressions for node and connection labels when building your own `Node` objects.  

## Development

### Making a Release

To be done.

### Debugging GraphViz Output

BootGraph uses GraphViz under the hood to create the graphs. 

The class `GraphVizExporterTests` automatically creates .dot files into the `target` folder. These files can be transformed to graphs online at [https://dreampuf.github.io/GraphvizOnline/](https://dreampuf.github.io/GraphvizOnline/)

## License 

Copyright (c) 2020 Atlassian and others. Apache 2.0 licensed, see [LICENSE](LICENSE) file.

![With Thanks from Atlassian](https://raw.githubusercontent.com/atlassian-internal/oss-assets/master/banner-with-thanks.png)(https://www.atlassian.com)