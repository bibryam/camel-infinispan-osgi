#!/bin/sh

#start JDG on separate terminal
# /home/bibryam/apps/_archive/jboss-datagrid-6.3.0-server/bin/standalone.sh

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

fabric:profile-create --parents feature-camel --version 1.0 demo-profile

container-add-profile root demo-profile

fabric:profile-edit --pid io.fabric8.agent/org.ops4j.pax.url.mvn.repositories='file:/home/bibryam/projects/camel-infinispan-osgi/offline-repo/target/repo@snapshots@id=ofbizian' default
fabric:profile-edit --pid org.fusesource.fabric.maven/checksumPolicy=warn  default
fabric:profile-edit --pid org.ops4j.pax.url.mvn/checksumPolicy=warn  default

fabric:version-create --parent 1.0 --default 1.1

fabric:profile-edit --repositories mvn:org.infinispan/infinispan-client-hotrod/6.1.0.Final-redhat-4/xml/features demo-profile 1.1
fabric:profile-edit --repositories mvn:org.infinispan/infinispan-core/6.1.0.Final-redhat-4/xml/features demo-profile 1.1
fabric:profile-edit --repositories mvn:org.infinispan/infinispan-commons/6.1.0.Final-redhat-4/xml/features demo-profile 1.1
fabric:profile-edit --repositories mvn:com.ofbizian/features/1.0.0/xml/features demo-profile 1.1

# Demo 1. Create an Embedded Cache and write to it from camel route with a bean*
fabric:profile-edit --features local-cache demo-profile 1.1
fabric:profile-edit --features local-client demo-profile 1.1

# Demo 2. Connect to remotely running JDG instance from camel route using a bean and Hot Rod protocol
# on separate terminal run: sh /home/bibryam/apps/_archive/jboss-datagrid-6.3.0-server/bin/standalone.sh
# fabric:profile-edit --features remote-client demo-profile 1.1

#Demo 3. Create an Embedded Cache and read/write to it using camel-infinispan component
#fabric:profile-edit --features local-cache demo-profile 1.1
#fabric:profile-edit --features local-camel-consumer demo-profile 1.1
#fabric:profile-edit --features local-camel-producer demo-profile 1.1

#Demo 4. Connect to remotely running JDG instance using camel-infinispan component using Hot Rod protocol
# on separate terminal run: sh /home/bibryam/apps/_archive/jboss-datagrid-6.3.0-server/bin/standalone.sh
#fabric:profile-edit --features remote-camel-producer demo-profile 1.1

container-upgrade --all 1.1

log:tail
