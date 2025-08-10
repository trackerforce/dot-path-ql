[![Master CI](https://github.com/trackerforce/dot-path-ql/actions/workflows/master.yml/badge.svg)](https://github.com/trackerforce/dot-path-ql/actions/workflows/master.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=trackerforce_dot-path-ql&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=trackerforce_dot-path-ql)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.trackerforce/dot-path-ql.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.trackerforce/dot-path-ql)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

# dotPathQL

A powerful Java utility for dynamically filtering and extracting nested properties from complex objects using dot-notation paths. Perfect for API responses, data transformation, and selective object serialization.

## Overview

The `DotPathQL` is the core component of this project that allows you to extract specific properties from complex nested objects (including Records, POJOs, Collections, Arrays, and Maps) using simple dot-notation paths like `"user.address.street"` or `"orders.products.name"`.

## Features

- 🎯 **Selective Property Extraction**: Extract only the properties you need
- 🚫 **Property Exclusion**: Exclude specific properties and return everything else
- 🔍 **Deep Nested Support**: Navigate through multiple levels of object nesting
- 📋 **Collection Handling**: Process Lists, Arrays, and other Collections
- 🗺️ **Map Support**: Handle both simple and complex Map structures
- 📝 **Record & POJO Support**: Works with Java Records and traditional classes
- 📄 **JSON Output**: Convert results to pretty-formatted or compact JSON strings
- 🔒 **Private Field Access**: Can access private fields when getters aren't available (filtering only)
- 🚀 **Performance Optimized**: Efficient reflection-based property access

## Quick Start

## Install
- Using the source code `mvn clean install`
- Adding as a dependency - Maven

```xml
<dependency>
  <groupId>io.github.trackerforce</groupId>
  <artifactId>dot-path-ql</artifactId>
  <version>${dot-path-ql.version}</version>
</dependency>
```

### Filter Usage

```java
// Filter specific properties from an object
Map<String, Object> result = new DotPathQL().filter(userObject, List.of(
    "username",
    "address.street",
    "address.city"
));
```

### Exclude Usage

```java
// Exclude specific properties and return everything else
Map<String, Object> result = new DotPathQL().exclude(userObject, List.of(
    "password",
    "ssn",
    "address.country"
));
```

## Supported Data Structures

- Simple Properties (primitive and object types)
- Nested Objects
- Collections and Arrays
- Map Structures
- Grouped Paths

For scenarios where you need multiple properties from the same parent object, you can use the grouped paths syntax to write more concise filter expressions:

#### Syntax
```java
"parent[child1.property,child2.property,child3]"
```

#### Example
```java
List<String> paths = List.of(
    "locations[home.street,work.city]"
);
```

#### Internal Processing
The grouped paths are automatically expanded internally:
- `"locations[home.street,work.city]"` becomes `"locations.home.street"` and `"locations.work.city"`
- `"contact[email,phone.mobile]"` becomes `"contact.email"` and `"contact.phone.mobile"`

## How It Works

### Property Access Strategy

The utility uses a multi-layered approach to access object properties:

1. **Record Components** (Most Efficient): For Java Records, uses the generated accessor methods
2. **Getter Methods**: Tries standard getter methods (`getName()`, `getAddress()`)
3. **Direct Field Access**: Falls back to direct field access for private fields (except for the exclude API)

### Nested Structure Processing

- **Collections/Arrays**: Creates a list of maps, processing each element
- **Maps**: Handles both simple values and complex objects as Map values
- **Objects**: Recursively processes nested objects
- **Path Resolution**: Splits dot-notation paths and processes them hierarchically

## Use Cases

### API Response Filtering
Perfect for creating flexible APIs where clients can specify which fields they need:

```java
@GetMapping("/users/{id}")
public Map<String, Object> getUser(
    @PathVariable Long id,
    @RequestParam List<String> fields
) {
    User user = userService.findById(id);
    return filterUtil.filter(user, fields);
}
```

### Data Transfer Optimization
Reduce payload size by extracting only required fields:

```java
// Instead of sending full objects, send only what's needed
List<String> essentialFields = List.of("id", "name", "status");
List<Map<String, Object>> lightweightData = users.stream()
    .map(user -> filterUtil.filter(user, essentialFields))
    .collect(Collectors.toList());
```

### Data Privacy and Security
Remove sensitive information while preserving the rest of the data structure:

```java
// Exclude sensitive fields from user profiles
List<String> sensitiveFields = List.of(
    "password",
    "ssn", 
    "creditCard.number",
    "address.country"  // Remove specific nested fields
);

Map<String, Object> publicProfile = filterUtil.exclude(userObject, sensitiveFields);
```

### API Response Exclusion
Create APIs where clients can specify which fields to exclude:

```java
@GetMapping("/users/{id}")
public Map<String, Object> getUser(
    @PathVariable Long id,
    @RequestParam(required = false) List<String> exclude
) {
    User user = userService.findById(id);
    
    if (exclude != null && !exclude.isEmpty()) {
        return filterUtil.exclude(user, exclude);
    }
    
    return filterUtil.filter(user, Collections.emptyList()); // Return all fields
}
```

### Report Generation
Extract specific data points for reports:

```java
List<String> reportFields = List.of(
    "customer.name",
    "orders.total",
    "orders.date",
    "orders.products.category"
);
```

## JSON Output

Convert your filtered or excluded results to JSON format using the built-in `toJson` method. This feature supports both pretty-formatted (indented) and compact (single-line) output.

### Pretty JSON

```java
var dotPathQL = new DotPathQL();
var result = dotPathQL.filter(userObject, List.of(
    "username",
    "address.street",
    "address.city",
    "orders.products.name"
));

// Pretty formatted JSON with 2-space indentation
String prettyJson = dotPathQL.toJson(result, true);
//or
String prettyJson = dotPathQL.toJson(result, 4); // Custom indentation level
```

**Output:**
```json
{
  "username": "john_doe",
  "address": {
    "street": "123 Main St",
    "city": "Springfield"
  },
  "orders": [
    {
      "products": [
        {
          "name": "Laptop"
        },
        {
          "name": "Mouse"
        }
      ]
    }
  ]
}
```

### Compact JSON

```java
// Compact single-line JSON
String compactJson = dotPathQL.toJson(result, false);
System.out.println(compactJson);
```

**Output:**
```json
{"username": "john_doe", "address": {"street": "123 Main St", "city": "Springfield"}, "orders": [{"products": [{"name": "Laptop"}, {"name": "Mouse"}]}]}
```

## Map objects
Convert any object to a Map representation using the `toMap` method. This is useful for scenarios where you need a visual representation of the entire object structure.

```java
Map<String, Object> userMap = dotPathQL.toMap(userObject);
```

## Helper Utilities

You can also easy access the map result using the `DotUtils` utility methods:

### Path parsing

```java
List<String> paths = DotUtils.parsePaths("locations[home.street,work.city],contact[email,phone.mobile],age");
// Result: ["locations[home.street,work.city]", "contact[email,phone.mobile]", "age"]
```

### Quick Access Methods

```java
// Step 1
DotPathQL dotPathQL = new DotPathQL();
Map<String, Object> result = dotPathQL.filter(userObject, List.of(
    "address",
    "friendList",
    "games"
));

// Step 2: Accessing the result
Map<String, Object> address = DotUtils.mapFrom(result, "address");
List<Map<String, Object>> friendList = DotUtils.listFrom(result, "friendList");
Object[] games = DotUtils.arrayFrom(result, "games");
```

## Technical Requirements

- **Java**: 17 or higher
- **Build Tool**: Maven
- **Dependencies**: None

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## License

This project is available under the MIT License.
