Camel Infinispan component on JBoss Fuse
===============================================


1. Clone and build Apache Camel trunk 2.13-SNAPSHOT
2. Clone and build JBoss Infinispan trunk 7.0.0-SNAPSHOT
3. Clone and build this project


4. Start infinispan server: infinispan-server-7.0.0-SNAPSHOT/bin/standalone.sh
5. Start JBoss fuse: /home/bibryam/Desktop/jboss-fuse-6.0.0.redhat-024/bin/fuse


6. Install the demo:
features:addUrl mvn:com.ofbizian/demo/1.0-SNAPSHOT/xml/features
features:install infinispan-demo


