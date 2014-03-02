package com.ofbizian.infinispan;

import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;

/**
 * User: bibryam
 * Date: 01/03/14
 */
public class ConfigurationFactory {
    public static GlobalConfiguration build() {
       return new GlobalConfigurationBuilder().classLoader(GlobalConfigurationBuilder.class.getClassLoader()).build();
    }
}
