##Camel Infinispan component on JBoss Fuse 6.0


####Clustering Diagram
![Clustering Diagram](http://4.bp.blogspot.com/-8klGVWhIpNE/UyWIpn_Cx1I/AAAAAAAAAhI/i8gAyVqIdAg/s1600/camel-infinispan-clustering.png)
####Remote Connection Diagram

![Remote connection Diagram](http://2.bp.blogspot.com/-SknGJlX4_DQ/UyWIp6ySoKI/AAAAAAAAAhM/OfnPFPGyrfE/s1600/camel-infinispan-remote.png)

####Building the project
- Clone and build Apache Camel trunk 2.13-SNAPSHOT
- Clone and build JBoss Infinispan trunk 7.0.0-SNAPSHOT
- Clone and build this project

####Modules
- **Component** - modifies camel-infinispan manifest file so that camel-infinispan component with version 2.13-SNAPSHOT can be deployed on Fuse 6.0 with Camel version 2.10.
- **Infinispan-OSGI** - Allows creating Infinispan Caches declaratively by deploying the bundle on OSGI and using ManagedService.
- **Local-Consumer** - Demonstrates how to receive events from an Embedded Cache in the same JVM created with  Infinispan-OSGI bundle.
- **Local-Producer** - Demonstrates how to sent data to an Embedded Cache in the same JVM created with Infinispan-OSGI bundle.
- **Remote-Producer** - Demonstrates how to sent data to Remote standalone cache
- **Remote-Query** - Demonstrates how to search data based on the cache value using Remote standalone cache
- **Features** - created OSGI features that groups a number of bundles into a feature for easier deployment


####Running
- Start infinispan server: infinispan-server-7.0.0-SNAPSHOT/bin/standalone.sh (only needed for Remote Producer demo)
- Start JBoss fuse: jboss-fuse-6.0.0.redhat-024/bin/fuse

*Creates a new child karaf instance (destroy old one if it exists) and connect to it*

    admin:stop consumer  
    admin:destroy consumer  
    admin:create consumer  
    admin:start consumer  
    admin:connect -u karaf consumer

*Demo 1. Create an Infinispan EmbeddedCacheManager and wait till it becomes available as OSGI service*

    features:addUrl mvn:com.ofbizian/features/1.0.0/xml/features
    features:install infinispan-server
    dev:wait-for-service "org.infinispan.manager.EmbeddedCacheManager"

*Create a Camel route that listen for event in the EmbeddedCacheManager created above in the same JVM*

    features:addUrl mvn:org.apache.camel.karaf/apache-camel/2.10.0.redhat-60024/xml/features
    features:install demo-local-consumer
    log:tail

*Demo 2. Create another child karaf instance (destroy old one if it exists) and connect to it*

    cd fuse/bin ./client -u admin -p admin
    admin:stop producer
    admin:destroy producer
    admin:create producer
    admin:start producer
    admin:connect -u karaf producer

*This will create an Infinispan EmbeddedCacheManager and make it available as OSGI service*

    features:addUrl mvn:com.ofbizian/features/1.0.0/xml/features
    features:install infinispan-server
    dev:wait-for-service "org.infinispan.manager.EmbeddedCacheManager"

*Create a Camel route send data every 10s to the EmbeddedCacheManager created above in the same JVM*

    features:addUrl mvn:org.apache.camel.karaf/apache-camel/2.10.0.redhat-60024/xml/features
    features:install demo-local-producer
    log:tail


*Demo 3. Create another child karaf instance (destroy old one if it exists) and connect to it*

    cd fuse/bin ./client -u admin -p admin
    admin:stop remote-producer
    admin:destroy remote-producer
    admin:create remote-producer
    admin:start remote-producer
    admin:connect -u karaf remote-producer

*This will create a Camel route send data every 10s to a remote standalone Infinispan server*

    features:addUrl mvn:com.ofbizian/features/1.0.0/xml/features
    features:addUrl mvn:org.apache.camel.karaf/apache-camel/2.10.0.redhat-60024/xml/features
    features:install demo-remote-producer
    log:tail

*Demo 4. Same as 3 but do: features:install demo-remote-query*

*Install hawtio to see the caches*

    features:addurl mvn:io.hawt/hawtio-karaf/1.2.2/xml/features
    features:install hawtio


###Notes
 - Creating EmbeddedCacheManager can be done by installing infinispan-server bundle with some configuration file or manually.
 - Creating EmbeddedCacheManager manually is as following:
    `<bean id="globalConfiguration" class="com.ofbizian.infinispan.ConfigurationFactory" factory-method="build"/>
    <bean id="localCache" class="org.infinispan.manager.DefaultCacheManager" init-method="start" destroy-method="stop">
      <argument ref="globalConfiguration"/>
    </bean>`

 - Clustering is done by configuring Infinispan and JGroups, it is outside the scope of Camel. Infinispan-OSGI has infinispan.xml with replicated clustering configuration. It also uses jgroups-tcp-sample.xml to configure how EmbeddedCacheManager in each JVM will find EmbeddedCacheManager in the other JVM

####Some JGroup configurations for myself while playing with Docker  

    export JAVA_OPTS=-Djgroups.udp.mcast_addr=224.0.0.0  
    export JAVA_OPTS="-Djgroups.remote_addr=172.17.0.3[7800],172.17.0.4[7800], -Djgroups.tcp.address=172.17.0.2"  
    export JAVA_OPTS="-Djgroups.remote_addr=172.17.0.2[7800],172.17.0.4[7800], -Djgroups.tcp.address=172.17.0.3"  
    export JAVA_OPTS="-Djgroups.remote_addr=172.17.0.2[7800],172.17.0.3[7800], -Djgroups.tcp.address=172.17.0.4"  





