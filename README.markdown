# Amazon ElastiCache Cluster Client

[![Build Status](https://travis-ci.org/awslabs/aws-elasticache-cluster-client-memcached-for-java.svg?branch=master)](https://travis-ci.org/awslabs/aws-elasticache-cluster-client-memcached-for-java)

Amazon ElastiCache Cluster Client is an enhanced Java library to connect to ElastiCache clusters. This client library has been built upon Spymemcached and is released under the [Amazon Software License](http://aws.amazon.com/asl/).

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

This argument is used to specify the version of your testing server. Currently supported memcached versions by Elasticache are _1.4.5_, _1.4.14_, _1.4.24_.
By default it is set to _1.4.24_.

    -Dtest.type=ci

This argument is used for CI testing where certain tests might be temporarily failing.

# More Information for Amazon ElastiCache Cluster Client
Github link: https://github.com/amazonwebservices/aws-elasticache-cluster-client-memcached-for-java
This repository is a fork of the spymemcached Java client for connecting to memcached (specifically the https://github.com/dustin/java-memcached-client repo).

Additional changes have been made to support Amazon ElastiCache Auto Discovery. To read more about Auto Discovery, please go here: http://docs.amazonwebservices.com/AmazonElastiCache/latest/UserGuide/AutoDiscovery.html.

For more information about Spymemcached see the link below:

[Spymemcached Project Home](http://code.google.com/p/spymemcached/)
contains a wiki, issue tracker, and downloads section.
