##JBoss Data Grid/Infinispan on JBoss Fuse/Karaf Demos
These are a number of examples demonstrating how to use Infinispan/JDG on OSGI container such as Karaf/JBoss Fuse.

####Prerequisite
- The examples requires [Red Hat JBoss Data Grid 6.3.0 Maven Repository](https://access.redhat.com/jbossnetwork/restricted/softwareDownload.html?softwareId=31673&product=data.grid) but it would work also with Infinispan 7.0.
- The OSGI container is [Red Hat JBoss Fuse 6.1.0 Full Install](https://access.redhat.com/jbossnetwork/restricted/softwareDownload.html?softwareId=29253) but Karaf 3 would also work just fine.

####Building and running the project
- Download JDG 6.3 Maven Repository
- Update the pom.xml to point to JDG 6.3 Maven Repository folder
- Run "mvn clean install" to build the project and create the offline repo with all the dependencies.

####Running the Demo
- Update runDemoOnFuse61.sh to point to camel-infinispan-osgi/offline-repo/target/repo
- Update runDemoOnFuse61.sh to point to jboss-fuse-full-6.1.0.redhat-379.zip
- Copy and paste the content of runDemoOnFuse61.sh (don't execute the file). This will start Fuse 6.1 and install all the demos.

####Modules/Features
- **Local-cache** - Instantiates an Embedded Cache in the same JVM and exposes as OSGI services.
- **Local-client** - A camel route that uses a bean to access the Embedded Cache instance exposed by Local-cache.
- **Remote-client** - A camel route that uses a bean to access remotely running JDG 6.3
- **Local-Camel-Consumer** - A camel route that uses camel-infinispan component to receive events from an Embedded Cache created by Local-cache module.
- **Local-Camel-Producer** - A camel route that uses camel-infinispan component to sent data to an Embedded Cache created by Local-cache module.
- **Remote-Camel-Producer** - A camel route that uses camel-infinispan component to sent data to remotely running JDG 6.3
- **Features** - creates OSGI features so each demo is standalone deployment unit
- **Offline-Repo** - creates a repo with all the dependencies for the demoes. Fuse 6.1 needs access to this repo to deploy all the demoes.
- **Camel-Infinispan-Component** - modifies camel-infinispan component manifest so that camel-infinispan component with version 2.13.2 can be deployed on Fuse 6.0 or Fuse 6.1

####Demos
*Demo 1. Create an Embedded Cache and send data to it from a bean*

    fabric:profile-edit --features local-cache demo-profile 1.1
    fabric:profile-edit --features local-client demo-profile 1.1

*Demo 2. Connect to remotely running JDG instance using Hot Rod protocol*

    (Start infinispan server)sh jboss-datagrid-6.3.0-server/bin/standalone.sh
    fabric:profile-edit --features remote-client demo-profile 1.1

*Demo 3. Create an Embedded Cache and read/write to it using camel-infinispan component*

    fabric:profile-edit --features local-cache demo-profile 1.1
    fabric:profile-edit --features local-camel-consumer demo-profile 1.1
    fabric:profile-edit --features local-camel-producer demo-profile 1.1

*Demo 4. Connect to remotely running JDG instance using camel-infinispan component using Hot Rod protocol*

    (Start infinispan server)sh jboss-datagrid-6.3.0-server/bin/standalone.sh
    fabric:profile-edit --features remote-camel-producer demo-profile 1.1


