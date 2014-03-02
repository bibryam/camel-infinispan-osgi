Camel Infinispan component on JBoss Fuse 6.0
===============================================

1. Clone and build Apache Camel trunk 2.13-SNAPSHOT
2. Clone and build JBoss Infinispan trunk 7.0.0-SNAPSHOT
3. Clone and build this project

4. Start infinispan server: infinispan-server-7.0.0-SNAPSHOT/bin/standalone.sh
5. Start JBoss fuse: jboss-fuse-6.0.0.redhat-024/bin/fuse

6. Install and run the demo:
features:addUrl mvn:com.ofbizian/demo/1.0.0/xml/features
features:install infinispan-demo

Remote-cache context shows how to interact with standlone Infinispan server
Local-cache context shows how to use an embedded cache.

Component module simply overides existing manifest to enable deploying the demo on JBoss Fuse 6.0 with still uses Camel 2.10