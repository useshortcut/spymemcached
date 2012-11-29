# Amazon ElastiCache Cluster Client

Amazon ElastiCache Cluster Client is an enhanced Java library to connect to ElastiCache clusters. This client library has been built upon Spymemcached and is released under the [Amazon Software License](http://aws.amazon.com/asl/).

# Building

AmazonElastiCacheClusterClient can be compiled using Apache Ant by running the following
command:

    ant

This will generate binary, source, and javadoc jars in the build
directory of the project.

More test info will be updated shortly.

# Testing

_Note: The ant test target is in the process of being updated to run the additional tests written for Auto Discovery._

The latest version of AmazonElastiCacheClusterClient has a set of command line arguments
that can be used to configure the location of your testing server. The
arguments are listed below.

    -Dserver.address_v4=ipv4_address_of_testing_server

This argument is used to specify the ipv4 address of your testing
server. By default it is set to localhost.

    -Dserver.port_number=port_number_of_memcached

This argument is used when memcahched is started on a port other than
11211

    -Dtest.type=ci

This argument is used for CI testing where certain unit tests might
be temporarily failing.

# More Information for Amazon ElastiCache Cluster Client
Github link: https://github.com/amazonwebservices/aws-elasticache-cluster-client-memcached-for-java
This repository is a fork of the spymemcached Java client for connecting to memcached (specifically the https://github.com/dustin/java-memcached-client repo).

Additional changes have been made to support Amazon ElastiCache Auto Discovery. To read more about Auto Discovery, please go here: http://docs.amazonwebservices.com/AmazonElastiCache/latest/UserGuide/AutoDiscovery.html.

For more information about Spymemcached see the link below:

[Spymemcached Project Home](http://code.google.com/p/spymemcached/)
contains a wiki, issue tracker, and downloads section.
