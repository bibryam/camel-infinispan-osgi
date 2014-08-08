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
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryActivated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryInvalidated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryLoaded;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryPassivated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryVisited;
import org.infinispan.notifications.cachelistener.event.CacheEntryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Listener(sync = true)
public class LocalCacheListener {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(LocalCacheListener.class);
    private EmbeddedCacheManager cacheManager;

    public EmbeddedCacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(EmbeddedCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void start() {
        LOGGER.info("Registering event listener");
        cacheManager.getCache().addListener(this);
        LOGGER.info("Registered event listener");
    }

    public void stop() {
        cacheManager.getCache().removeListener(this);
    }

    @CacheEntryActivated
    @CacheEntryCreated
    @CacheEntryInvalidated
    @CacheEntryLoaded
    @CacheEntryModified
    @CacheEntryPassivated
    @CacheEntryRemoved
    @CacheEntryVisited
    public void processEvent(CacheEntryEvent<Object, Object> event) {
        if (event.isPre()) {
            LOGGER.info("Infinispan event received: " + event.getType() + " with key: " + event.getKey());
        }
    }
}
