package com.ermnvldmr.w;

import com.ermnvldmr.w.config.PropertiesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PropertiesConfig.class)
public class WApplication {
    public static void main(String[] args) {
        SpringApplication.run(com.ermnvldmr.w.WApplication.class, args);
    }
}
