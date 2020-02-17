[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.tryadhawk/airtable-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.tryadhawk/airtable-java)
[![master build](https://github.com/adHawk/airtable-java/workflows/master%20build/badge.svg)](https://github.com/adHawk/airtable-java/actions?query=workflow%3A%22master+build%22)
[![Test Coverage](https://api.codeclimate.com/v1/badges/6003f4a94cb26e7f5298/test_coverage)](https://codeclimate.com/github/adHawk/airtable-java/test_coverage)
[![License](https://img.shields.io/github/license/mashape/apistatus.svg)](LICENSE)

# airtable-java
This is a Java API client for [Airtable](http://www.airtable.com). 

The Airtable API provides a simple way of accessing data within Java projects. More information about the Airtable API 
for a specific Airtable Base can be found at [https://airtable.com/api](https://airtable.com/api).

# Installation

airtable-java is available from Maven Central and requires at least Java 8.

## Gradle

Add airtable-java to the `dependencies` section of your `build.gradle`:
```groovy
dependencies {
    ...
    implementation 'com.tryadhawk:airtable-java:2.0.2'
    ...
}
```

## Maven

Add airtable-java to the `dependencies` section of your `pom.xml`
```xml
<dependencies>
    ...
    <dependency>
        <groupId>com.tryadhawk</groupId>
        <artifactId>airtable-java</artifactId>
        <version>2.0.2</version>
    </dependency>
    ...
</dependencies>
```

# Usage

## Creating an Instance

```java
Airtable airtable = Airtable.builder()
    .config(Configuration.builder().apiKey("API_KEY").build())
    .build()
    .buildAsyncTable("BASE_NAME", "TABLE_NAME", SomeClass.class);
```
where API_KEY is the key to authenticate to the Airtable API, BASE_NAME is the name of the Airtable Base, TABLE_NAME is 
the name of the table to access, and `SomeClass` is the class to map row data to.

See [How do I get my API key](https://support.airtable.com/hc/en-us/articles/219046777-How-do-I-get-my-API-key) for 
details on retrieving your API key
 
The `Airtable` class provides factory methods for building both synchronous and asynchronous table clients. Additional 
configuration settings and customization are available in the `Configuration` and `Airtable` classes.

airtable-java uses [AsyncHttpClient](https://github.com/AsyncHttpClient/async-http-client) for HTTP communication, 
[slf4j](https://www.slf4j.org/) for logging, [Jackson](https://github.com/FasterXML/jackson-databind) for object mapping, 
and [RxJava](https://github.com/ReactiveX/RxJava) for asynchronous programming.

## Customizing Column Mapping

airtable-java uses Jackson for mapping row data to objects. For cases where the field name and the column name are different, 
add `@JsonProperty("columnName")` to the field to configure the name used for mapping the column to a field.
 
## Request Limits

The Airtable API is limited to 5 requests per second. If you exceed this rate, you will receive a 429 status code and 
will need to wait 30 seconds before subsequent requests will succeed. By default, airtable-java will automatically wait 
between 30 and 35 seconds and retry the request up to 5 times.

# Building

The library is build with Java 8 and Gradle. Run `./gradlew build` in Linux/Mac or `gradlew build` in Windows.

airtable-java uses [Project Lombok](https://projectlombok.org/) for immutable value classes. Additional setup may be 
required to build using an IDE ([IntelliJ](https://projectlombok.org/setup/intellij), [Eclipse](https://projectlombok.org/setup/eclipse)).

## Contributing

See [CONTRIBUTING.md](./CONTRIBUTING.md) 

# License

MIT License, see [LICENSE](LICENSE)
