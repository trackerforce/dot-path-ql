[![Master CI](https://github.com/trackerforce/dot-path-ql/actions/workflows/master.yml/badge.svg)](https://github.com/trackerforce/dot-path-ql/actions/workflows/master.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.trackerforce/dot-path-ql.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.trackerforce/dot-path-ql)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

# dotPathQL

A powerful Java utility for dynamically filtering and extracting nested properties from complex objects using dot-notation paths. Perfect for API responses, data transformation, and selective object serialization.

## Overview

The `DotPathQL` is the core component of this project that allows you to extract specific properties from complex nested objects (including Records, POJOs, Collections, Arrays, and Maps) using simple dot-notation paths like `"user.address.street"` or `"orders.products.name"`.

## Features

- 🎯 **Selective Property Extraction**: Extract only the properties you need
- 🔍 **Deep Nested Support**: Navigate through multiple levels of object nesting
- 📋 **Collection Handling**: Process Lists, Arrays, and other Collections
- 🗺️ **Map Support**: Handle both simple and complex Map structures
- 📝 **Record & POJO Support**: Works with Java Records and traditional classes
- 🔒 **Private Field Access**: Can access private fields when getters aren't available
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

### Basic Usage

```java
DotPathQL filterUtil = new DotPathQL();

// Filter specific properties from an object
List<String> filterPaths = List.of(
    "username",
    "address.street",
    "address.city"
);

Map<String, Object> result = filterUtil.filter(userObject, filterPaths);
```

### Example Output

Given an input object with complex nested structure, the filter will return:

```json
{
  "username": "john_doe",
  "address": {
    "street": "123 Main St",
    "city": "Springfield"
  }
}
```

## Supported Data Structures

### 1. Simple Properties
```java
List<String> paths = List.of("username", "email");
```

### 2. Nested Objects
```java
List<String> paths = List.of("address.street", "address.zipCode");
```

### 3. Collections and Arrays
```java
// Extract from Lists
List<String> paths = List.of("orders.products.name");

// Extract from Arrays
List<String> paths = List.of("occupations.title");
```

### 4. Map Structures

#### Simple Maps
```java
// Map<String, String>
List<String> paths = List.of(
    "additionalInfo.preferredLanguage",
    "additionalInfo.subscriptionStatus"
);
```

#### Complex Maps
```java
// Map<String, Address>
List<String> paths = List.of(
    "locations.home.street",
    "locations.work.city"
);
```

### 5. Grouped Paths

For scenarios where you need multiple properties from the same parent object, you can use the grouped paths syntax to write more concise filter expressions:

#### Syntax
```java
"parent[child1.property,child2.property,child3]"
```

#### Example
Instead of writing:
```java
List<String> paths = List.of(
    "locations.home.street",
    "locations.work.city"
);
```

You can also write:
```java
List<String> paths = List.of(
    "locations[home.street,work.city]"
);
```

#### More Complex Examples
```java
// Multiple grouped paths
List<String> paths = List.of(
    "locations[home.street,home.zipCode,work.city]",
    "contact[email,phone.mobile]",
    "orders[products.name,total,date]"
);

// Mixed with regular paths
List<String> paths = List.of(
    "username",                           // Regular path
    "locations[home.street,work.city]",   // Grouped path
    "address.state"                       // Regular path
);
```

#### Internal Processing
The grouped paths are automatically expanded internally:
- `"locations[home.street,work.city]"` becomes `"locations.home.street"` and `"locations.work.city"`
- `"contact[email,phone.mobile]"` becomes `"contact.email"` and `"contact.phone.mobile"`

This feature maintains full backward compatibility while providing a more readable way to specify multiple related paths.

## Advanced Examples

### Complex Nested Filtering

```java
UserDetail user = UserDetail.of(); // Creates sample data

List<String> filterPaths = List.of(
    "username",                          // Simple property
    "address.street",                    // Nested object
    "orders.products.name",              // Collection of objects
    "orders.products.description",       // Multiple properties from same collection
    "additionalInfo.preferredLanguage",  // Simple Map value
    "locations.home.street",             // Complex Map value
    "occupations.title"                  // Array of objects
);

Map<String, Object> filtered = filterUtil.filter(user, filterPaths);
```

### Result Structure

The above filtering would produce:

```json
{
  "username": "john_doe",
  "address": {
    "street": "123 Main St"
  },
  "orders": [
    {
      "products": [
        {
          "name": "Laptop",
          "description": "High-end gaming laptop"
        },
        {
          "name": "Smartphone", 
          "description": "Latest model smartphone"
        }
      ]
    },
    {
      "products": [
        {
          "name": "Headphones",
          "description": "Noise-cancelling headphones"
        }
      ]
    }
  ],
  "additionalInfo": {
    "preferredLanguage": "English"
  },
  "locations": {
    "home": {
      "street": "456 Elm St"
    }
  },
  "occupations": [
    {
      "title": "Software Engineer"
    },
    {
      "title": "Project Manager"
    }
  ]
}
```

## How It Works

### Property Access Strategy

The utility uses a multi-layered approach to access object properties:

1. **Record Components** (Most Efficient): For Java Records, uses the generated accessor methods
2. **Getter Methods**: Tries standard getter methods (`getName()`, `getAddress()`)
3. **Direct Field Access**: Falls back to direct field access for private fields

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
