# liquibase-edn

Liquibase-EDN is [EDN](https://github.com/edn-format/edn) changelog parser for [Liquibase](http://www.liquibase.org/).

## Usage

### Maven coordinates

Currently not on any Maven repo. Install locally `mvn clean install` and put the following in your dependency management:

```xml
<dependency>
  <groupId>liquibase-edn</groupId>
  <artifactId>liquibase-edn</artifactId>
  <version>3.0.8-0.1.0-SNAPSHOT</version>
</dependency>
```

_Targets Liquibase 3.0.8 for compatibility with [clj-liquibase](https://github.com/kumarshantanu/clj-liquibase)._

### Standalone usage

- Include the Liquibase-EDN JAR in classpath
- Include [EDN-Java](https://github.com/bpsm/edn-java) JAR in classpath

## License

Copyright © 2015 Shantanu Kumar

Distributed under the Apache License, version 2.0, the same as Liquibase.
