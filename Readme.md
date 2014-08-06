##JBoss Data Grid/Infinispan on JBoss Fuse
These are a number of examples demonstrating how to use Infinispan/JDG on OSGI container such as Karaf/JBoss Fuse.

####Prerequisite
- The project requires [Red Hat JBoss Data Grid 6.3.0 Maven Repository](https://access.redhat.com/jbossnetwork/restricted/softwareDownload.html?softwareId=31673&product=data.grid) but it would work also with Infinispan 7.0.
- The OSGI container is [Red Hat JBoss Fuse 6.1.0 Full Install](https://access.redhat.com/jbossnetwork/restricted/softwareDownload.html?softwareId=29253) but Karaf 3 would also work just fine.

####Building and running the project
- Download JDG 6.3 Maven Repository
- Update the pom.xml repository to point to JDG 6.3 Maven Repository folder
- Run "mvn clean install" to build the project and create the offline repo with all the dependencies.

####Running the Demo
- Update runDemoOnFuse61.sh to point to jboss-fuse-full-6.1.0.redhat-379.zip. This is where the demos will be deployed.
- Update runDemoOnFuse61.sh to point to camel-infinispan-osgi/offline-repo/target/repo. This is where Fuse will find all the features.
- Copy and paste the content of runDemoOnFuse61.sh (don't execute the file). This will start Fuse 6.1 and install all the demos. Remote demos require JDG running as standalone process.

####Modules/Features
- **Local-cache** - Instantiates an Embedded Cache and exposes it as OSGI services. All the clients running in the same container can access it as OSGI service.
- **Local-client** - A camel route that uses a bean to access the Embedded Cache instance exposed by Local-cache.
- **Remote-client** - A camel route that uses a bean to access remotely running JDG 6.3 over Hot Rot protocol.
- **Local-Camel-Consumer** - A camel route that uses camel-infinispan component to receive events from an Embedded Cache created by Local-cache module.
- **Local-Camel-Producer** - A camel route that uses camel-infinispan component to sent data to an Embedded Cache created by Local-cache module.
- **Remote-Camel-Producer** - A camel route that uses camel-infinispan component to sent data to remotely running JDG 6.3 over Hot Rot protocol.
- **Features** - Creates OSGI features so each demo is a standalone deployment unit.
- **Offline-Repo** - Creates a repo with all the dependencies for the examples. Fuse 6.1 needs access to this repo to deploy all the features and its dependencies (JDG, Camel, etc).
- **Camel-Infinispan-Component** - Alters camel-infinispan component's manifest so the component with version 2.13.2 can be deployed on Fuse 6.0 or Fuse 6.1

####Demos
Demos are a combination of the above maven features. To run a specific demo, uncomment it from runDemoOnFuse61.sh before executing the script content on a terminal. By default Demo1 will run.
*Demo 1. Create an Embedded Cache and write to it from camel route with a bean*

    fabric:profile-edit --features local-cache demo-profile 1.1
    fabric:profile-edit --features local-client demo-profile 1.1

*Demo 2. Connect to remotely running JDG instance from camel route using a bean and Hot Rod protocol*

    (Start infinispan server)sh jboss-datagrid-6.3.0-server/bin/standalone.sh
    fabric:profile-edit --features remote-client demo-profile 1.1

*Demo 3. Create an Embedded Cache and read/write to it using camel-infinispan component*

    fabric:profile-edit --features local-cache demo-profile 1.1
    fabric:profile-edit --features local-camel-consumer demo-profile 1.1
    fabric:profile-edit --features local-camel-producer demo-profile 1.1

*Demo 4. Connect to remotely running JDG instance using camel-infinispan component using Hot Rod protocol*

    (Start infinispan server)sh jboss-datagrid-6.3.0-server/bin/standalone.sh
    fabric:profile-edit --features remote-camel-producer demo-profile 1.1

####Questions?
[Bilgin Ibryam](https://github.com/bibryam)

