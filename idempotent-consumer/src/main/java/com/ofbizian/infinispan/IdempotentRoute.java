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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.processor.idempotent.InfinispanIdempotentRepository;
import org.apache.camel.test.AvailablePortFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IdempotentRoute extends RouteBuilder {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(IdempotentRoute.class);

    private InfinispanIdempotentRepository infinispanRepo;
    private int port;

    @Override
    public void configure() throws Exception {
        from("restlet:http://localhost:" + port + "/idempotent/{key}?restletMethods=GET")

                .idempotentConsumer(header("key"), infinispanRepo)
                    .setBody(simple("UNIQUE REQUEST ACCEPTED: ${header.key}"))
                    .stop()
                 .end()

                .setBody(simple("REQUEST REJECTED: ${header.key}"));
    }

    public InfinispanIdempotentRepository getInfinispanRepo() {
        return infinispanRepo;
    }

    public void setInfinispanRepo(InfinispanIdempotentRepository infinispanRepo) {
        this.infinispanRepo = infinispanRepo;
    }

    public void start() {
         port = AvailablePortFinder.getNextAvailable(8080);
         LOGGER.info("Using port: " + port);
    }
}
