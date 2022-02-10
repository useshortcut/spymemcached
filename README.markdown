# What is this?

This is a fork of the spymemcached java client maintained by Shortcut for use
in Datomic on-prem peers and transactors.

We maintain a patchset that we apply to whatever spymemcached library Cognitect
appears to be using in their Datomic releases.
We then replace their spymemcached jars in the transactor and peers with this one.

Formerly this was https://github.com/couchbase/spymemcached v2.12.3
and the branch based on that is master+patches-on-couchbase-spymemcached
and releases tagged like 2.12.3.chX.

But now it appears to be AWS's fork of it that adds ElastiCache auto-discovery
https://github.com/awslabs/aws-elasticache-cluster-client-memcached-for-java.

This is currently based on
https://github.com/awslabs/aws-elasticache-cluster-client-memcached-for-java/releases/tag/1.1.3 
and our patchset is on branch master+patches-on-aws-elasticache
and releases tagged like 1.1.3.scX.


The patchset includes:

1. A fix for an NPE thrown from fillWriteBuffer. https://github.com/couchbase/spymemcached/pull/38
2. A fix for incoherent connection state when a Server responds before the client
   is finished sending: https://github.com/useshortcut/spymemcached/commit/74838cc0a1107676d6224bdee3abad0c03aa848a
3. Some additional logging to discover and monitor the above two issues:
   * https://github.com/useshortcut/spymemcached/commit/fb877bf0cd977b32b579515467f6a6dcecb0141b
   * https://github.com/useshortcut/spymemcached/commit/23c32e641751ae6458560013cefd089b6071a244
4. Some build.xml changes to get it to build.
5. The build target (and minimum supported JVM) is Java 11.
6. Some system properties to override protocol and some timeout values.
   https://github.com/useshortcut/spymemcached/pull/2

The last item requires explanation.
We use [McRouter] ([our fork][McRouter-shortcut]) as a memcached proxy
between Datomic peers and multiple ElastiCache clusters in separate AZs.
We do this to avoid cross-AZ traffic between the peer and the cluster
(saving enough money for more clusters!)
and to provide failover redundancy if a cluster goes down.
(It's also a flexible platform for more elaborate cache strategies
or additional tiers, but we haven't done that.)

However McRouter only understands the Memcached text protocol;
Datomic constructs a memcached client that uses the binary protocol in code
we do not control.
The system properties are a hack to let us override Datomic's builder choices
so we can force use of the text protocol.

This fork adds the following system properties:

  * `shortcut.spymemcached.forceProtocol=TEXT` (or `BINARY`)
  * `shortcut.spymemcached.forceOpQueueMaxBlockTimeMs`
    How long to wait for an operation to be enqueued.
  * `shortcut.spymemcached.forceOpTimeoutMs`
    How long to wait for an operation to complete.
    This timer begins when the op is created, so it includes the enqueing time
    mentioned by the previous property.
    (We think Datomic may set this timeout to 50.)

[McRouter]: https://github.com/facebook/mcrouter
[McRouter-shortcut]: https://github.com/useshortcut/mcrouter

## Building for Shortcut

We only use part of the build system: all we need is a jar and to run tests.
`${version}` is the result of `git describe --abbrev=0`.

1. Build with `ant jar`. This places a file in `build/jars/memcache-asg-java-client-${version}.jar`
2. Run the integration tests `ant test`
3. Run the integration tests with a server using the
   `spymemcached-release-tools.sh` script in the backend repo.
   This includes a stress test designed to trigger the fillWriteBuffer NPE.

For more thorough building, testing, and deployment docs see the [datomic guide].

[datomic guide]: https://github.com/useshortcut/backend/blob/main/modules/server/resources/datomic/README.md

## Rebasing on upstream changes

1. Rebase, reapply patches, and check for dependency changes.
2. Determine the new version number. It should be `{upstream-version}.sc1`.
   Increment sc1 (sc2, etc) only if our build changed but upstream did not.
3. Bump filename and version of spymemcached pom in root and change
   dependencies as needed. Commit locally.
4. Temporarily tag the new version with an annotated git tag,
   e.g. `git tag 1.1.3.sc1 -m "Shortcut release 1 of AWS memcached 1.1.3"`
   *Do not push this tag!*
   The build system uses git tags for versioning.
5. Then build, test and deploy artifact as above.
6. Then put the artifact in our local repo, adjust versions, 
   and test with our applications.
7. PR your changes to this fork.
8. After review and merge (merge-commit please!),
   delete the tag from your local repository, 
   create the same annotated tag on master, and push it.

**Text below this line is from upstream and kept unchanged.**

# Amazon ElastiCache Cluster Client

[![Build Status](https://travis-ci.org/awslabs/aws-elasticache-cluster-client-memcached-for-java.svg?branch=master)](https://travis-ci.org/awslabs/aws-elasticache-cluster-client-memcached-for-java)

Amazon ElastiCache Cluster Client is an enhanced Java library to connect to ElastiCache clusters. This client library has been built upon Spymemcached and is released under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0).

# Building

Amazon ElastiCache Cluster Client can be compiled using Apache Ant by running the following
command:

    ant

This will generate binary, source, and javadoc jars in the build
directory of the project.

# Testing

The latest version of Amazon ElastiCache Cluster Client supports unit tests and integration tests.

Unit tests do not require any running memcached servers, and can be run using Apache Ant by the following command:

    ant test

Integration tests are always run against local memcached servers. Start integration tests by the
following command:

    ant it

It has a set of command line arguments that can be used to configure your client mode and your local testing server. The arguments are listed below.

    -Dclient.mode=memcached_client_mode

This argument is used to specify the mode of the client that you want to run. Supported options are _Static_ and _Dynamic_.
_Dynamic_ mode enables Auto Discovery feature. _Static_ mode runs without Auto Discovery and has the same functionality as a classic spymemcached client. By default it is set to _Dynamic_.

    -Dserver.bin=local_binary_of_testing_server

This argument is used to specify the location of your testing
server binary. By default it is set to _/usr/bin/memcached_.

    -Dserver.version=version_of_testing_server

This argument is used to specify the version of your testing server. Currently supported memcached versions by Elasticache are _1.4.5_, _1.4.14_, _1.4.24_, _1.5.10_, _1.5.16_.
By default it is set to _1.5.16_.

    -Dtest.type=ci

This argument is used for CI testing where certain tests might be temporarily failing.

# More Information for Amazon ElastiCache Cluster Client
Github link: https://github.com/amazonwebservices/aws-elasticache-cluster-client-memcached-for-java
This repository is a fork of the spymemcached Java client for connecting to memcached (specifically the https://github.com/dustin/java-memcached-client repo).

Additional changes have been made to support Amazon ElastiCache Auto Discovery. To read more about Auto Discovery, please go here: http://docs.amazonwebservices.com/AmazonElastiCache/latest/UserGuide/AutoDiscovery.html.

For more information about Spymemcached see the link below:

[Spymemcached Project Home](http://code.google.com/p/spymemcached/)
contains a wiki, issue tracker, and downloads section.
