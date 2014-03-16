/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ofbizian.infinispan;

import java.io.IOException;
import java.io.InputStream;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.infinispan.commons.util.Util;

/**
 * User: bibryam
 * Date: 13/03/14
 */
public class RemoteQueryHelper {
    public static final String BOOK_PROTOBIN = "/book.protobin";

    public static void main(String [] args) throws Exception {
        updateServerSchema(BOOK_PROTOBIN);
    }

    private static void updateServerSchema(String protoFile) throws Exception {
        JMXServiceURL serviceURL = new JMXServiceURL("service:jmx:remoting-jmx://" + "127.0.0.1" + ":" + "9999");
        JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceURL, null);
        MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();

        byte[] descriptor = readClasspathResource(protoFile);
        ObjectName objName = new ObjectName("jboss.infinispan:type=RemoteQuery,name=\"local\",component=ProtobufMetadataManager");
        mBeanServerConnection.invoke(objName, "registerProtofile", new Object[]{descriptor}, new String[]{byte[].class.getName()});
    }

    private static byte[] readClasspathResource(String classPathResource) throws IOException {
        InputStream is = RemoteQueryHelper.class.getResourceAsStream(classPathResource);
        try {
            return Util.readStream(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
