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

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.api.BasicCacheContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteClient {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(RemoteClient.class);
    private BasicCacheContainer cacheManager;
    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void start() {
        LOGGER.info("Creating client");
        Configuration config = new ConfigurationBuilder().classLoader(Thread.currentThread().getContextClassLoader()).addServers(getHost()).build();
        cacheManager = new RemoteCacheManager(config, true);
    }

    public void stop() {
        if (cacheManager != null) {
            cacheManager.stop();
        }
    }

    public Object process(String operation, String key, String value) {
        LOGGER.info("Remote " + operation + " {} : {} ",  key, value);
        if ("PUT".equals(operation)) {
            return cacheManager.getCache().put(key, value);
        } else  if ("GET".equals(operation)) {
            return cacheManager.getCache().get(key);
        } else if ("DELETE".equals(operation)) {
            return cacheManager.getCache().remove(key);
        } else throw new UnsupportedOperationException(operation);
    }


}
