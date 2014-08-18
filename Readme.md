##JBoss Data Grid/Infinispan on JBoss Fuse
These are a number of examples demonstrating how to use Infinispan/JDG on OSGI container such as Karaf/JBoss Fuse.

####Prerequisite
- The OSGI container is [Red Hat JBoss Fuse 6.1.0 Full Install](https://access.redhat.com/jbossnetwork/restricted/softwareDownload.html?softwareId=29253) but Karaf 3 would also work just fine.

####Building the project
- "mvn clean install" to build the project and create the offline repo with all the dependencies.

####Modules/Features
- **Local-cache** - Instantiates an Embedded Cache and exposes it as OSGI services. All the clients running in the same container can access it as OSGI service.
- **Local-client** - A camel route that uses a bean to access the Embedded Cache instance exposed by Local-cache.
- **Remote-client** - A camel route that uses a bean to access remotely running JDG 6.3 over Hot Rot protocol.
- **Local-Camel-Consumer** - A camel route that uses camel-infinispan component to receive events from an Embedded Cache created by Local-cache module.
- **Local-Camel-Producer** - A camel route that uses camel-infinispan component to sent data to an Embedded Cache created by Local-cache module.
- **Remote-Camel-Producer** - A camel route that uses camel-infinispan component to sent data to remotely running JDG 6.3 over Hot Rot protocol.
- **Idempotent-Consumer** - A camel route with replicated idempotent consumer that keeps in sync with other idempotent consumers running in other JVMs. If you create multiple containers with this route deployed in it, each container starts a rest endpoint and gets a new port allocated starting from 8080 and increasing by one. A request in the form of *http://localhost:8080/idempotent/123* will be replicated to other containers so that subsequent requests to other containers with the same query key *123* like *http://localhost:8081/idempotent/123* will be rejected.
- **Features** - Creates OSGI features so each demo is a standalone deployment unit.
- **Offline-Repo** - Creates a repo with all the dependencies for the examples. Fuse 6.1 needs access to this repo to deploy all the features and its dependencies (JDG, Camel, etc).
- **Camel-Infinispan-Component** - Alters camel-infinispan component's manifest so the component with version 2.13.2 can be deployed on Fuse 6.0 or Fuse 6.1

####Running the Demos
- Update runDemoOnFuse61.sh FUSE_61_ARCHIVE to point to jboss-fuse-full-6.1.0.redhat-379.zip. This is where the demos will be deployed.
- Update runDemoOnFuse61.sh org.ops4j.pax.url.mvn.repositories to point to camel-infinispan-osgi/offline-repo/target/repo. This is where Fuse will find all the features.
- Copy and paste the content of runDemoOnFuse61.sh (don't execute the file). This will start Fuse 6.1 and setup the repositories and demos ready to be run.
- Demos are combination of the above features. To run a demo follow the instructions below by adding profiles to root container from CLI or Hawtio.

*Demo 1. Creates an Embedded Cache and writes to it from a camel route with a bean (no camel-infinispan component)*

    to install: container-add-profile root demo1; log:tail
    to uninstall: container-remove-profile root demo1

*Demo 2. Connects to remotely running JDG instance from camel route using a bean and Hot Rod protocol(no camel-infinispan component)*

    (Start infinispan server on a separate terminal) sh jboss-datagrid-6.3.0-server/bin/standalone.sh
    to install: container-add-profile root demo2; log:tail
    to uninstall: container-remove-profile root demo2

*Demo 3. Creates an Embedded Cache and reads/writes to it from camel route using camel-infinispan component*

    to install: container-add-profile root demo3; log:tail
    to uninstall: container-remove-profile root demo3

*Demo 4. Connects to remotely running JDG instance from a camel route using camel-infinispan component and Hot Rod protocol*

    (Start infinispan server on a separate terminal) sh jboss-datagrid-6.3.0-server/bin/standalone.sh
    to install: container-add-profile root demo4; log:tail
    to uninstall: container-remove-profile root demo4

*Demo 5. Creates three containers with idempotent consumers. The idempotent consumers discover each other and replicate state to form a distributed idempotent behaviour*

    to install: container-create-child --profile demo5 root idempotent1
    to install: container-create-child --profile demo5 root idempotent2
    to install: container-create-child --profile demo5 root idempotent3

    This will create 3 containers with rest endpoints on incremental ports starting from 8080.
    A request like http://localhost:8080/idempotent/123 to the first container will replicate to other two containers, so that
    subsequent requests to container two on port 8081 and container three on 8082 will be rejected.

    to uninstall: container-delete idempotent1
    to uninstall: container-delete idempotent2
    to uninstall: container-delete idempotent3

![Clustering Diagram](http://1.bp.blogspot.com/-BS2C1LzZ1uI/U_KPzRTj2kI/AAAAAAAAArA/JcZx0guOcm0/s1600/IdempotentConsumer.png)

