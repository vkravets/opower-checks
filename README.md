opower-checks
=============

Opower Engineering checkstyle for Java and Scala artifacts.

Usage
-----

To run checkstyle add the following plugin to your Maven POM file's `plugins` section.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <dependencies>
        <dependency>
            <groupId>com.opower</groupId>
            <artifactId>opower-checks</artifactId>
            <version>${version.opower.checkstyle}</version>
        </dependency>
    </dependencies>
    <executions>
        <execution>
            <id>checkstyle</id>
            <phase>compile</phase>
            <goals>
                <goal>checkstyle</goal>
            </goals>
            <configuration>
                <excludes>${maven.checkstyle.excludes}</excludes>
                <configLocation>opower_checks.xml</configLocation>
                <includeTestSourceDirectory>true</includeTestSourceDirectory>
                <failsOnError>true</failsOnError>
                <consoleOutput>true</consoleOutput>
            </configuration>
        </execution>
        <execution>
            <id>checkstyle-libs</id>
            <phase>compile</phase>
            <goals>
                <goal>checkstyle</goal>
            </goals>
            <configuration>
                <skip>${maven.checkstyle.checks.opower_libs_checks}</skip>
                <excludes>${maven.checkstyle.excludes}</excludes>
                <configLocation>opower_libs_checks.xml</configLocation>
                <includeTestSourceDirectory>false</includeTestSourceDirectory>
                <failsOnError>true</failsOnError>
                <consoleOutput>true</consoleOutput>
            </configuration>
        </execution>
    </executions>
</plugin>
```

The stanza above includes 2 checkstyle executions. The first, `checkstyle`, runs `opower_checks.xml`.
The second, `checkstyle-libs` runs `opower_libs_checks.xml` and can be disabled by setting the
`maven.checkstyle.checks.opower_libs_checks` property to `true`.

Contributing
------------

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request
