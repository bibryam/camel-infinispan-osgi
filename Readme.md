##Camel Infinispan component on JBoss Fuse 6.0

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
- **Features** - created OSGI features that groups a number of bundles into a feature for easier deployment

####Running
- Start infinispan server: infinispan-server-7.0.0-SNAPSHOT/bin/standalone.sh (only needed for Remote Producer demo)
- Start JBoss fuse: jboss-fuse-6.0.0.redhat-024/bin/fuse

*Creates a new karaf instance (destroy old one if it exists)*  

    admin:stop consumer  
    admin:destroy consumer  
    admin:create consumer  
    admin:start consumer  
    admin:connect -u karaf consumer  

*Install camel*

    features:addUrl mvn:org.apache.camel.karaf/apache-camel/2.10.0.redhat-60024/xml/features
    features:install camel

*This will create an Infinispan EmbeddedCacheManager and make it available as OSGI service*  

    features:addUrl mvn:com.ofbizian/features/1.0.0/xml/features
    features:install infinispan-osgi
    dev:wait-for-service "org.infinispan.manager.EmbeddedCacheManager"

*Creates a Camel route that listens for event in the EmbeddedCacheManager created above in the same JVM*

    features:install local-consumer
    log:tail

*Creates a new karaf instance (destroy old one if it exists)*

    ./client -u admin -p admin
    admin:stop producer
    admin:destroy producer
    admin:create producer
    admin:start producer
    admin:connect -u karaf producer

*Install camel*

    features:addUrl mvn:org.apache.camel.karaf/apache-camel/2.10.0.redhat-60024/xml/features
    features:install camel

*This will create an Infinispan EmbeddedCacheManager and make it available as OSGI service*

    features:addUrl mvn:com.ofbizian/features/1.0.0/xml/features
    features:install infinispan-osgi
    dev:wait-for-service "org.infinispan.manager.EmbeddedCacheManager"

*Creates a Camel route send data every 10s to the EmbeddedCacheManager created above in the same JVM* 

    features:install local-producer
    log:tail

*Install hawtio to see the caches.*

    features:addurl mvn:io.hawt/hawtio-karaf/1.2.2/xml/features
    eatures:install hawtio


###Notes
 - Creating EmbeddedCacheManager can be done by installing infinispan-osgi bundle with some configuration file or manually.
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
