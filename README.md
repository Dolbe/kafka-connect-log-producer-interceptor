# ProducerTraceLogInterceptor

Used to log Trace Id and topic to kafka-connect log file.
Trace Id is taken from kafka message header.

## Build

To build a package, run 
```
mvn clean package
```

## Installation

You need to add the JAR-file with Interceptor into the CLASSPATH environment variable.

To use the interceptor in kafka-connect, you need to set up `producer.interceptor.classes`.
Example:
```
producer.interceptor.classes=com.interceptors.ProducerTraceLogInterceptor
```

## Configuration

To configure field name if Kafka message header, that contains Trace Id, use `producer.trace.log.header.key`. If not specified, `traceparent` key is used.
Example:
```
producer.trace.log.header.key=traceid
```
