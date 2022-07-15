package com.richguy.config;

import com.zfoo.storage.model.config.StorageConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AutoConfiguration {

    @Bean
    public StorageConfig storageConfig(Environment environment) {
        var storageConfig = new StorageConfig();
        storageConfig.setId(environment.getProperty("storage.storageManager"));
        storageConfig.setScanPackage(environment.getProperty("storage.scan.package"));
        storageConfig.setResourceLocation(environment.getProperty("storage.resource.location"));
        return storageConfig;
    }

}
