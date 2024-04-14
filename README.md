# Embold Maven Plugin
Embold scanner for Maven builds
![build](https://github.com/embold/embold-maven-plugin/actions/workflows/build.yml/badge.svg?branch=development)
[![Maven Central](https://img.shields.io/maven-central/v/io.embold.scan/embold-maven-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.embold.scan%22%20AND%20a:%22embold-maven-plugin%22)

Maven plugin for Embold static analysis

Include the Embold maven plugin in your maven builds for design and code quality feedback. Results are posted to your Embold server.

## Usage

`mvn io.embold.scan:embold-maven-plugin:embold -Dembold.host.url=foo -Dembold.user.token=bar`

where:
* *embold.host.url* - Your Embold Server Url
* *embold.user.token* - Embold Access Token
* *embold.scanner.location* (optional) - Location where the Embold scanner will be auto-downloaded. By default, it is downloaded inside the embold-maven-plugin directory in your local repository.
* *embold.scanner.update" (optional) - Set to `false` if you want to disable auto-update of the Embold scanners
More info on Embold Access Token here: https://docs.embold.io/gamma-access-token-gat/#gamma-access-token-gat

The Embold plugin will create a repository on the Embold Server with the name <groupId>:<artifactId> if it doesn't already exist.
You can then link to a Project on Embold in order to view the scan results.

## Requirements
* JDK 1.8 or higher
* Embold on-premise server instance (version 1.8.7.0 or higher)



## Known Issues and Limitations
##### 1. Aborting the maven build doesn't always abort the scan on Windows
For now, you may need to manually kill the Java process if you abort on Windows

##### 2. Limited POM analysis
Currently the plugin does not process exclusions in the POM / build cycle
