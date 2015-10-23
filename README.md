# liquibase-edn

Liquibase-EDN is [EDN](https://github.com/edn-format/edn) changelog parser for [Liquibase](http://www.liquibase.org/).

## Usage

### Maven coordinates

```xml
<dependency>
  <groupId>liquibase-edn</groupId>
  <artifactId>liquibase-edn</artifactId>
  <version>3.0.8-0.1.0</version>
</dependency>
```

For non-Clojure projects you may have to mention the [Clojars](https://clojars.org/) Maven repository in your POM file:

```xml
<repository>
  <id>clojars.org</id>
  <url>http://clojars.org/repo</url>
</repository>
```

_Targets Liquibase 3.0.8 for compatibility with [clj-liquibase](https://github.com/kumarshantanu/clj-liquibase)._

### Standalone usage

- Include the Liquibase-EDN JAR in classpath
- Include [EDN-Java](https://github.com/bpsm/edn-java) JAR in classpath

## License

Copyright © 2015 Shantanu Kumar

_Note:_ The file `AbstractMapChangeLogParser.java` is adapted from the Liquibase source code.

Distributed under the Apache License, version 2.0, the same as Liquibase.
