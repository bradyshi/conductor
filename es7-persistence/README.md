# ES7 Persistence

This module provides ES7 persistence when indexing workflows and tasks.

## ES Breaking changes

From ES6 to ES7 there were significant breaking changes which affected ES7-persistence module implementation.
* Mapping type deprecation
* Templates API
* TransportClient deprecation

More information can be found here: https://www.elastic.co/guide/en/elasticsearch/reference/current/breaking-changes-7.0.html


## Build

1. In order to use the ES7, you must change the following files from ES6 to ES7:

https://github.com/Netflix/conductor/blob/main/build.gradle
https://github.com/Netflix/conductor/blob/main/server/src/main/resources/application.properties

In file:
 
- /build.gradle

change ext['elasticsearch.version'] from revElasticSearch6 to revElasticSearch7


In file:
 
- /server/src/main/resources/application.properties

change conductor.elasticsearch.version from 6 to 7

Also you need to recreate dependencies.lock files with ES7 dependencies. To do that delete all dependencies.lock files and then run: 

```
./gradlew generateLock updateLock saveLock
```


2. To use the ES7 for all modules include test-harness, you must change also the following files:

https://github.com/Netflix/conductor/blob/main/test-harness/build.gradle
https://github.com/Netflix/conductor/blob/main/test-harness/src/test/java/com/netflix/conductor/test/integration/AbstractEndToEndTest.java

In file:
 
- /test-harness/build.gradle

* change module inclusion from 'es6-persistence' to 'es7-persistence'

In file:
 
- /test-harness/src/test/java/com/netflix/conductor/test/integration/AbstractEndToEndTest.java

* change conductor.elasticsearch.version from 6 to 7
* change DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch-oss").withTag("7.6.2") to DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch-oss").withTag("7.6.2")


## Usage

This module uses the following configuration options:

* `conductor.elasticsearch.url` - A comma separated list of schema/host/port of the ES nodes to communicate with.
Schema can be `http` or `https`. If schema is ignored then `http` transport will be used;
Since ES deprecated TransportClient, conductor will use only the  REST transport protocol.
* `conductor.elasticsearch.indexPrefix` - The name of the workflow and task index.
Defaults to `conductor`
* `conductor.elasticsearch.asyncWorkerQueueSize` - Worker Queue size used in executor service for async methods in IndexDao 
Defaults to `100`
* `conductor.elasticsearch.asyncMaxPoolSize` - Maximum thread pool size in executor service for async methods in IndexDao        
Defaults to `12`
* `conductor.elasticsearch.asyncBufferFlushTimeout` - Timeout (in seconds) for the in-memory to be flushed if not explicitly indexed
Defaults to `10`

### BASIC Authentication
If you need to pass user/password to connect to ES, add the following properties to your config file
* conductor.elasticsearch.username
* conductor.elasticsearch.password

Example
```
conductor.elasticsearch.username=someusername
conductor.elasticsearch.password=somepassword
```
