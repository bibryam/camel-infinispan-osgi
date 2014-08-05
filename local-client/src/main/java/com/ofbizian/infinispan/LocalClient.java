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

import org.infinispan.manager.EmbeddedCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalClient {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(LocalClient.class);
    private EmbeddedCacheManager cacheManager;

    public EmbeddedCacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(EmbeddedCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public Object process(String operation, String key, String value) {
        LOGGER.info("Local " + operation + "{} : {} ",  key, value);
        if ("PUT".equals(operation)) {
            return cacheManager.getCache().put(key, value);
        } else  if ("GET".equals(operation)) {
            return cacheManager.getCache().get(key);
        } else if ("DELETE".equals(operation)) {
            return cacheManager.getCache().remove(key);
        } else throw new UnsupportedOperationException(operation);
    }
}
