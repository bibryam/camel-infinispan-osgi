##Camel Infinispan component on JBoss Fuse 6.0
This requires infinispan with 7.0.0-SNAPSHOT and https://github.com/infinispan/infinispan/pull/2640

####Clustering Diagram
![Clustering Diagram](http://4.bp.blogspot.com/-8klGVWhIpNE/UyWIpn_Cx1I/AAAAAAAAAhI/i8gAyVqIdAg/s1600/camel-infinispan-clustering.png)
####Remote Connection Diagram
![Remote connection Diagram](http://2.bp.blogspot.com/-SknGJlX4_DQ/UyWIp6ySoKI/AAAAAAAAAhM/OfnPFPGyrfE/s1600/camel-infinispan-remote.png)

####Building the project
- Clone and build JBoss Infinispan trunk 7.0.0-SNAPSHOT
- Clone and build this project

####Modules
- **Camel-infinispan** - modifies camel-infinispan manifest file so that camel-infinispan component with version 2.13.1 can be deployed on Fuse 6.0 and Fuse 6.1
- **Cache-instance** - Allows creating Infinispan Caches declaratively by deploying the bundle on OSGI and using ManagedService.
- **Local-Consumer** - Demonstrates how to receive events from an Embedded Cache in the same JVM created with  Infinispan-OSGI bundle.
- **Local-Producer** - Demonstrates how to sent data to an Embedded Cache in the same JVM created with Infinispan-OSGI bundle.
- **Remote-Producer** - Demonstrates how to sent data to Remote standalone cache
- **Features** - created OSGI features that groups a number of bundles into a feature for easier deployment


####Running
- Start JBoss fuse: jboss-fuse-6.0.0.redhat-024/bin/fuse

*Demo 1. Create an Infinispan EmbeddedCacheManager and wait till it becomes available as OSGI service*

    features:addUrl mvn:com.ofbizian/features/1.0.0/xml/features
    features:install cache-instance
    dev:wait-for-service "org.infinispan.manager.EmbeddedCacheManager"

*Create a Camel route that listen for event in the EmbeddedCacheManager created above in the same JVM*

    features:addUrl mvn:org.apache.camel.karaf/apache-camel/2.10.0.redhat-60024/xml/features
    features:install demo-local-consumer
    log:tail

*Demo 2. Create another child karaf instance (destroy old one if it exists) and connect to it*

    cd fuse/bin ./client -u admin -p admin
    admin:create producer
    admin:start producer
    admin:connect -u admin producer

*This will create an Infinispan EmbeddedCacheManager and make it available as OSGI service*

    features:addUrl mvn:com.ofbizian/features/1.0.0/xml/features
    features:install cache-instance
    dev:wait-for-service "org.infinispan.manager.EmbeddedCacheManager"

*Create a Camel route send data every 10s to the EmbeddedCacheManager created above in the same JVM*

    features:addUrl mvn:org.apache.camel.karaf/apache-camel/2.10.0.redhat-60024/xml/features
    features:install demo-local-producer
    log:tail

*Demo 3. Remote Producer example for sending and retrieving data from standalone Infinispan server.
Start infinispan server: infinispan-server-7.0.0-SNAPSHOT/bin/standalone.sh (only needed for Remote Producer demo)

    features:install demo-remote-producer
    log:tail

*Install hawtio to see the caches*

    features:addurl mvn:io.hawt/hawtio-karaf/1.2.2/xml/features
    features:install hawtio


###Notes
 - Creating EmbeddedCacheManager can be done by installing cache-instance bundle with some configuration file or manually.
 - Clustering is done by configuring Infinispan and JGroups, it is outside the scope of Camel. Infinispan-OSGI has infinispan.xml with replicated clustering configuration. It also uses jgroups-tcp-sample.xml to configure how EmbeddedCacheManager in each JVM will find EmbeddedCacheManager in the other JVM

####Some JGroup configurations for myself while playing with Docker  

    export JAVA_OPTS=-Djgroups.udp.mcast_addr=224.0.0.0  
    export JAVA_OPTS="-Djgroups.remote_addr=172.17.0.3[7800],172.17.0.4[7800], -Djgroups.tcp.address=172.17.0.2"  
    export JAVA_OPTS="-Djgroups.remote_addr=172.17.0.2[7800],172.17.0.4[7800], -Djgroups.tcp.address=172.17.0.3"  
    export JAVA_OPTS="-Djgroups.remote_addr=172.17.0.2[7800],172.17.0.3[7800], -Djgroups.tcp.address=172.17.0.4"
