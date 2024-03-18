# What is this?

This is a fork of the spymemcached java client maintained by Shortcut for use
in Datomic on-prem peers and transactors.

We maintain a patchset that we apply to the spymemcached library.
We then replace their spymemcached jars in the transactor and peers with this one.

Datomic <= 1.0.6316 used https://github.com/couchbase/spymemcached v2.12.3
and the branch based on that is master+patches-on-couchbase-spymemcached
and releases tagged like 2.12.3.chX or 2.12.3.scX.

Datomic now appears to use AWS's fork of it that adds ElastiCache auto-discovery
https://github.com/awslabs/aws-elasticache-cluster-client-memcached-for-java.

We have a patchset for that on master+patches-on-aws-elasticache
*but it is not functional*.
It is based on
https://github.com/awslabs/aws-elasticache-cluster-client-memcached-for-java/releases/tag/1.1.3
and our patchset is on branch
and releases tagged like 1.1.3.scX.

Cognitect appears to have added to their fork at least an additional configurable
timeout value for the ElastiCache-specific "config get <cluster>" command,
and without those patches this release will not work with Datomic.

However, Datomic appears to have some reflection in place to determine if the
spymemcached client lib supports AWS discovery.
So if we use a different spymemcached library *not* based on the AWS fork,
memcached caching with Datomic still works but doesn't support auto-discovery.

This is fine for us, because we don't need this: we proxy through McRouter.
The thing we need is overriding the protocol format to TEXT.
(More on this later.)

[auto-node-discovery]: https://docs.datomic.com/on-prem/overview/caching.html#node-auto-discovery


The patchset includes:

1. A fix for an NPE thrown from fillWriteBuffer. https://github.com/couchbase/spymemcached/pull/38
2. A fix for incoherent connection state when a Server responds before the client
   is finished sending: https://github.com/useshortcut/spymemcached/commit/74838cc0a1107676d6224bdee3abad0c03aa848a
3. Some additional logging to discover and monitor the above two issues:
    * https://github.com/useshortcut/spymemcached/commit/fb877bf0cd977b32b579515467f6a6dcecb0141b
    * https://github.com/useshortcut/spymemcached/commit/23c32e641751ae6458560013cefd089b6071a244
4. Some minor build.xml bitrot cleanup, changelog suppression, and targets Java 11.
5. Some system properties to override protocol and some timeout values.
   https://github.com/useshortcut/spymemcached/pull/2
6. A package rename from `net.spy.memcached` to `datomic.spy.memcached`.
   This is needed on datomic versions >= 1.0.6726 because of shading.
7. Removal of the pom.xml file, since it's a development-only tool that we do not need.

The last item requires explanation.
We use [McRouter] ([our fork][McRouter-shortcut]) as a memcached proxy
between Datomic peers and multiple ElastiCache clusters in separate AZs.
We do this to avoid cross-AZ traffic between the peer and the cluster
(saving enough money for more clusters!)
and to provide failover redundancy if a cluster goes down.
(It's also a flexible platform for more elaborate cache strategies
or additional tiers, but we haven't done that.)

However, McRouter only understands the Memcached text protocol;
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

1. Build with `ant jar`. This places a file in `build/jars/spymemcached-${version}.jar`
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
   e.g. `git tag 2.12.3.sc5 -m "Shortcut release 5 of Couchbase memcached 2.12.3"`
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

# Building

Spymemcached can be compiled using Apache Ant by running the following
command:

    ant

This will generate binary, source, and javadoc jars in the build
directory of the project.

To run the Spymemcached tests against Membase Server run the
following command:

    ant test -Dserver.type=membase

To test Spymemcached against Membase running on a different host
use the following command:

    ant test -Dserver.type=membase \
        -Dserver.address_v4=ip_address_of_membase

# Testing

The latest version of spymemcached has a set of command line arguments
that can be used to configure the location of your testing server. The
arguments are listed below.

    -Dserver.address_v4=ipv4_address_of_testing_server

This argument is used to specify the ipv4 address of your testing
server. By default it is set to localhost.

    -Dserver.address_v6=ipv6_address_of_testing_server

This argument is used to set the ipv6 address of your testing server.
By default it is set to ::1. If an ipv6 address is specified then an
ipv4 address must be specified otherwise there may be test failures.

    -Dserver.port_number=port_number_of_memcached

This argument is used when memcahched is started on a port other than
11211

    -Dtest.type=ci

This argument is used for CI testing where certain unit tests might
be temporarily failing.

# More Information

For more information about Spymemcached see the links below:

## Project Page The

[Spymemcached Project Home](http://code.google.com/p/spymemcached/)
contains a wiki, issue tracker, and downloads section.

## Github

[The gitub page](http://github.com/dustin/java-memcached-client)
contains the latest Spymemcached source.

## Couchbase.org

At [couchbase.org](http://www.couchbase.org/code/couchbase/java) you
can find a download's section for the latest release as well as an
extensive tutorial to help new users learn how to use Spymemcached.
