package com.classifiedads.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "file")
@Data
public class FileStorageConfig {
    private Upload upload;

    @Data
    public static class Upload {
        private String dir;
    }

    public String getUploadDir() {
        return upload.getDir();
    }
}
