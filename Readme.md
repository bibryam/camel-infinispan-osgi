Camel Infinispan component on JBoss Fuse
===============================================

1. Get JBoss Fuse
jboss-fuse-full-6.0.0.redhat-024.zip from http://www.jboss.org/products/fuse and starts

2. Get JBoss Marshalling from https://github.com/jboss-remoting/jboss-marshalling
Build: mvn clean install -DskipTests
Install to Fuse: install -s mvn:org.jboss.marshalling/jboss-marshalling-osgi/2.0.0.Beta2-SNAPSHOT

3. Get JBoss Modules from https://github.com/jboss-modules/jboss-modules
Convert it into osgi bundle:

diff --git a/pom.xml b/pom.xml
index 8a25ada..a7c04db 100644
--- a/pom.xml
+++ b/pom.xml
@@ -29,6 +29,7 @@
     <artifactId>jboss-modules</artifactId>
     <version>1.3.0.Final</version>
     <name>JBoss Modules</name>
+    <packaging>bundle</packaging>

     <parent>
         <groupId>org.jboss</groupId>
@@ -150,6 +151,11 @@
                 </configuration>

             </plugin>
+            <plugin>
+                <groupId>org.apache.felix</groupId>
+                <artifactId>maven-bundle-plugin</artifactId>
+                <extensions>true</extensions>
+            </plugin>
         </plugins>
     </build>
     <dependencies>

Build: mvn clean install -DskipTests
Install to Fuse: install -s mvn:org.jboss.modules/jboss-modules/1.3.0.Final

4. Download infinispan-6.0.0.Final-all from http://infinispan.org/download/
Install to Fuse some of the libraries:
install -s file:/home/bibryam/Downloads/infinispan-6.0.0.Final-all/lib/jgroups-3.4.1.Final.jar
install -s file:/home/bibryam/Downloads/infinispan-6.0.0.Final-all/lib/jboss-logging-3.1.2.GA.jar

5.Add sun.misc to Fuse-home/etc/jre.properties and restart Fuse:

jre-1.7= \
  sun.misc, \
  javax.accessibility...


6. Get https://github.com/infinispan/infinispan
git checkout 6.0.0.Final

Change the following pom files
6.1. Core: add metadata file to bundle, increase marshalling version

          <plugin>
              <groupId>org.apache.felix</groupId>
              <artifactId>maven-bundle-plugin</artifactId>
              <configuration>
                  <instructions>
                      <Include-Resource>src/main/resources/infinispan-core-component-metadata.dat</Include-Resource>
                      <Export-Package>
                          !${project.groupId}.commons.*,
                          org.infinispan.marshall.core,
                          ${project.groupId}.*;version=${project.version};-split-package:=error
                      </Export-Package>
                      <Import-Package>
                      javax.management,javax.naming,javax.transaction;version=
                      "[1.1,2)",javax.transaction.xa;version="[1.1,2)",javax.xml.namespace,ja
                      vax.xml.parsers,javax.xml.stream,javax.xml.transform,javax.xml.transfor
                      m.dom,javax.xml.transform.stream,net.jcip.annotations;resolution:=optio
                      nal,org.infinispan.commons;version="[6.0,7)",org.infinispan.commons.api
                      ;version="[6.0,7)",org.infinispan.commons.configuration;version="[6.0,7
                      )",org.infinispan.commons.equivalence;version="[6.0,7)",org.infinispan.
                      commons.executors;version="[6.0,7)",org.infinispan.commons.hash;version
                      ="[6.0,7)",org.infinispan.commons.io;version="[6.0,7)",org.infinispan.c
                      ommons.marshall;version="[6.0,7)",org.infinispan.commons.marshall.jboss
                      ;version="[6.0,7)",org.infinispan.commons.util;version="[6.0,7)",org.in
                      finispan.commons.util.concurrent;version="[6.0,7)",org.infinispan.commo
                      ns.util.concurrent.jdk8backported;version="[6.0,7)",org.jboss.logging;v
                      ersion="[3.1,4)",
                          org.jboss.marshalling;version="[1.3,3)",
                          org.jboss.marshalling.util;version="[1.3,3)",
                          org.jgroups;version="[3.4,4)",org.jgroup
                      s.blocks;version="[3.4,4)",org.jgroups.blocks.mux;version="[3.4,4)",org
                      .jgroups.jmx;version="[3.4,4)",org.jgroups.logging;version="[3.4,4)",or
                      g.jgroups.protocols;version="[3.4,4)",org.jgroups.protocols.relay;versi
                      on="[3.4,4)",org.jgroups.protocols.tom;version="[3.4,4)",org.jgroups.st
                      ack;version="[3.4,4)",org.jgroups.util;version="[3.4,4)",org.w3c.dom,or
                      g.xml.sax
                      </Import-Package>
                  </instructions>
              </configuration>
          </plugin>

6.2.1. Commons: attach it to core as a fragment, increase marshalling version

         <plugin>
              <groupId>org.apache.felix</groupId>
              <artifactId>maven-bundle-plugin</artifactId>
              <configuration>
                  <instructions>
                      <Fragment-Host>org.infinispan.core;bundle-version=6.0.0.CR1</Fragment-Host>
                      <Export-Package>
                          ${project.groupId}.commons.*;version=${project.version};-split-package:=error
                      </Export-Package>
                      <Import-Package>
                          javax.naming,javax.net.ssl,net.jcip.annotations;resoluti
                          on:=optional,org.jboss.logging;version="[3.1,4)",org.jboss.marshalling;
                          version="[1.3,3)";resolution:=optional,org.jboss.marshalling.reflect;ve
                          rsion="[1.3,3)";resolution:=optional,org.jboss.marshalling.river;versio
                          n="[1.3,3)";resolution:=optional,org.osgi.framework;version="[1.6,2)";r
                          esolution:=optional,sun.misc
                      </Import-Package>
                  </instructions>
              </configuration>
         </plugin>

6.2.2. Change in the marshaller API

         diff --git a/commons/src/main/java/org/infinispan/commons/marshall/jboss/AbstractJBossMarshaller.java b/commons/src/main/java/org/infinispan/commo
         index f5d55da..6d4c405 100644
         --- a/commons/src/main/java/org/infinispan/commons/marshall/jboss/AbstractJBossMarshaller.java
         +++ b/commons/src/main/java/org/infinispan/commons/marshall/jboss/AbstractJBossMarshaller.java
         @@ -65,7 +65,7 @@ protected PerThreadInstanceHolder initialValue() {
             public AbstractJBossMarshaller() {
                // Class resolver now set when marshaller/unmarshaller will be created
                baseCfg = new MarshallingConfiguration();
         -      baseCfg.setExternalizerCreator(new SunReflectiveCreator());
         +//      baseCfg.setExternalizerCreator(new SunReflectiveCreator());
                baseCfg.setExceptionListener(new DebuggingExceptionListener());
                baseCfg.setClassExternalizerFactory(new SerializeWithExtFactory());
                baseCfg.setInstanceCount(DEF_INSTANCE_COUNT);


6.3. Hotrod client: cannot remember what's the change here

          <plugin>
              <groupId>org.apache.felix</groupId>
              <artifactId>maven-bundle-plugin</artifactId>
              <configuration>
                  <instructions>
                      <Export-Package>
                          ${project.groupId}.client.hotrod.*;version=${project.version};-split-package:=error
                      </Export-Package>
                      <Import-Package>
                              javax.net.ssl,
                              net.jcip.annotations;resolution:=optional,
                              org.apache.avro;resolution:=optional,
                              org.apache.avro.generic;resolution:=optional,
                              org.apache.avro.io;resolution:=optional,
                              org.apache.avro.util;resolution:=optional,
                              org.apache.commons.pool;version="[1.6,2)",
                              org.apache.commons.pool.impl;version="[1.6,2)",
                              org.infinispan.commons;version="[6.0,7)",
                              org.infinispan.commons.api;version="[6.0,7)",
                              org.infinispan.commons.configuration;version="[6.0,7)",
                              org.infinispan.commons.executors;version="[6.0,7)",
                              org.infinispan.commons.hash;version="[6.0,7)",
                              org.infinispan.commons.io;version="[6.0,7)",
                              org.infinispan.commons.logging;version="[6.0,7)",
                              org.infinispan.commons.marshall;version="[6.0,7)",
                              org.infinispan.commons.marshall.jboss;version="[6.0,7)",
                              org.infinispan.commons.util;version="[6.0,7)",
                              org.infinispan.commons.util.concurrent;version="[6.0,7)",
                              org.infinispan.protostream;resolution:=optional,
                              org.infinispan.query.dsl;resolution:=optional,
                              org.infinispan.query.dsl.impl;resolution:=optional,
                              org.infinispan.query.remote.client;resolution:=optional,
                              org.jboss.logging;version="[3.1,4)"
                      </Import-Package>
                  </instructions>
              </configuration>
          </plugin>


Build: mvn clean install -DskipTests

install mvn:org.infinispan/infinispan-commons/6.0.0.Final
install mvn:org.infinispan/infinispan-core/6.0.0.Final
install mvn:org.infinispan/infinispan-client-hotrod/6.0.0.Final

//install -s file:/home/bibryam/Downloads/infinispan-6.0.0.Final-all/lib/commons-pool-1.6.jar
//features:install camel-blueprint


7. Camel-infinispan component - change camel version to the one from Fuse
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.felix</groupId>
            <artifactId>maven-bundle-plugin</artifactId>
            <extensions>true</extensions>
            <configuration>
                <instructions>
                    <Bundle-SymbolicName>${project.groupId}.${project.artifactId}</Bundle-SymbolicName>
                    <Import-Package>
                        org.apache.camel;version="[2.9,2.14)",org.apache.camel.
                        api.management;version="[2.9,2.14)",org.apache.camel.impl;version="[2.
                        9,2.14)",org.apache.camel.spi;version="[2.9,2.14)",org.apache.camel.s
                        upport;version="[2.9,2.14)",org.infinispan;version="[6.0,7)",org.infin
                        ispan.client.hotrod;version="[6.0,7)",org.infinispan.client.hotrod.conf
                        iguration;version="[6.0,7)",org.infinispan.commons.api;version="[6.0,7)
                        ",org.infinispan.manager;version="[6.0,7)",org.infinispan.notifications
                        ;version="[6.0,7)",org.infinispan.notifications.cachelistener.annotatio
                        n;version="[6.0,7)",org.infinispan.notifications.cachelistener.event;ve
                        rsion="[6.0,7)",org.slf4j;version="[1.6,2)"
                    </Import-Package>
                </instructions>
            </configuration>
        </plugin>
    </plugins>
</build>

Build: mvn clean install -DskipTests
install -s mvn:org.apache.camel/camel-infinispan/2.13-SNAPSHOT


7. Start infinispan server: infinispan-server-6.0.0.CR1/bin/./standalone.sh

8. Deploy and start the demo
Build: mvn clean install -DskipTests
osgi:install -s mvn:com.ofbizian/infinispan-osgi/1.0-SNAPSHOT
