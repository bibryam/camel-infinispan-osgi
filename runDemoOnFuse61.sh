#!/bin/sh

# Update FUSE_61_ARCHIVE and org.ops4j.pax.url.mvn.repositories below before running!

jps -lm | grep karaf | grep -v grep | awk '{print $1}' | xargs kill -KILL

export FUSE_61_ARCHIVE=/home/bibryam/apps/_archive/jboss-fuse-full-6.1.0.redhat-379.zip
export FUSE_INSTALL_PATH=/tmp/

rm -rf ${FUSE_INSTALL_PATH}jboss-fuse-6.1.0.redhat-379/
unzip $FUSE_61_ARCHIVE -d $FUSE_INSTALL_PATH
sed -i 's/#admin/admin/' ${FUSE_INSTALL_PATH}jboss-fuse-6.1.0.redhat-379/etc/users.properties
cd ${FUSE_INSTALL_PATH}jboss-fuse-6.1.0.redhat-379/bin/
./start

sleep 20
./client

wait-for-service -t 300000 io.fabric8.api.BootstrapComplete

fabric:create --clean --wait-for-provisioning --profile fabric

fabric:profile-edit --pid io.fabric8.agent/org.ops4j.pax.url.mvn.repositories='file:/home/bibryam/projects/camel-infinispan-osgi/offline-repo/target/repo@snapshots@id=ofbizian' default
fabric:profile-edit --pid org.fusesource.fabric.maven/checksumPolicy=warn  default
fabric:profile-edit --pid org.ops4j.pax.url.mvn/checksumPolicy=warn  default

fabric:profile-edit --repositories mvn:org.infinispan/infinispan-client-hotrod/6.1.0.Final-redhat-4/xml/features default
fabric:profile-edit --repositories mvn:org.infinispan/infinispan-core/6.1.0.Final-redhat-4/xml/features default
fabric:profile-edit --repositories mvn:org.infinispan/infinispan-commons/6.1.0.Final-redhat-4/xml/features default
fabric:profile-edit --repositories mvn:com.ofbizian/features/1.0.0/xml/features default

fabric:profile-create --parents feature-camel --version 1.0 demo1
fabric:profile-create --parents feature-camel --version 1.0 demo2
fabric:profile-create --parents feature-camel --version 1.0 demo3
fabric:profile-create --parents feature-camel --version 1.0 demo4
fabric:profile-create --parents feature-camel --version 1.0 demo5

#container-add-profile root demo1

fabric:version-create --parent 1.0 --default 1.1

fabric:profile-edit --features local-cache demo1 1.1
fabric:profile-edit --features local-client demo1 1.1

fabric:profile-edit --features remote-client demo2 1.1

fabric:profile-edit --features local-cache demo3 1.1
fabric:profile-edit --features local-camel-consumer demo3 1.1
fabric:profile-edit --features local-camel-producer demo3 1.1

fabric:profile-edit --features remote-camel-producer demo4 1.1

fabric:profile-edit --features local-cache demo5 1.1
fabric:profile-edit --features idempotent-consumer demo5 1.1

container-upgrade --all 1.1
