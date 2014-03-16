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

import com.google.protobuf.Descriptors;
import org.infinispan.commons.api.BasicCacheContainer;
import org.infinispan.protostream.SerializationContext;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;

public class BasicCacheContainerFactory {
    public static final String BOOK_PROTOBIN = "/book.protobin";

    public static BasicCacheContainer build() throws IOException, Descriptors.DescriptorValidationException {

        Configuration config = new ConfigurationBuilder()
                .addServers("localhost")
                .marshaller(new ProtoStreamMarshaller())
//                .classLoader(SerializationContext.class.getClassLoader())
                .classLoader(Thread.currentThread().getContextClassLoader())
                .build();

        RemoteCacheManager cacheContainer = new RemoteCacheManager(config);

        SerializationContext srcCtx = ProtoStreamMarshaller.getSerializationContext(cacheContainer);
        srcCtx.registerProtofile(BasicCacheContainerFactory.class.getResourceAsStream(BOOK_PROTOBIN));
        srcCtx.registerMarshaller(Book.class, new BookMarshaller());

        return cacheContainer;

    }
}
