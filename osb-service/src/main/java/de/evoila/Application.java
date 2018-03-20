/**
 *
 */
package de.evoila;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

/**
 * @author Johannes Hiemer.
 */
@SpringBootApplication
public class Application {

    @Bean(name = "customProperties")
    public Map<String, String> customProperties() {
        Map<String, String> customProperties = new HashMap<>();
        return customProperties;
    }

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.addListeners(new ApplicationPidFileWriter());
        ApplicationContext ctx = springApplication.run(args);

        Assert.notNull(ctx, "ApplicationContext can not be null.");
    }

}