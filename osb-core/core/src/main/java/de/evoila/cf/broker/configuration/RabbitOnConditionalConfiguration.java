package de.evoila.cf.broker.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import de.evoila.cf.broker.bean.RabbitMQCredentials;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.config.java.AbstractCloudConfig;


/**
 * Created by reneschollmeyer on 09.08.17.
 */
@Configuration
class RabbitOnConditionalConfiguration{

      @Configuration
      @Profile({"default", "local"})
      @ConditionalOnBean(RabbitMQCredentials.class)
      static class Default {

            @Autowired
            RabbitMQCredentials conf;
            @Bean
            public CachingConnectionFactory connectionFactory() {
                  CachingConnectionFactory cachingConnectionFactory =
                        new CachingConnectionFactory(conf.getHost(), conf.getPort());
                  cachingConnectionFactory.setUsername(conf.getUsername());
                  cachingConnectionFactory.setPassword(conf.getPassword());
                  cachingConnectionFactory.setVirtualHost(conf.getVhost());
                  return cachingConnectionFactory;
            }

            @Bean
            public AmqpAdmin amqpAdmin() {
                  return new RabbitAdmin(connectionFactory());
            }

            @Bean
            public RabbitTemplate rabbitTemplate() {
                  return new RabbitTemplate(connectionFactory());
            }

      }

      @Configuration
      @Profile("cloud")
      static class Cloud extends AbstractCloudConfig {

            @Bean
            public ConnectionFactory rabbitConnectionFactory() {
                  return connectionFactory().rabbitConnectionFactory();
            }

            @Bean
            public AmqpAdmin amqpAdmin() {
                  return new RabbitAdmin(rabbitConnectionFactory());
            }

            @Bean
            public RabbitTemplate rabbitTemplate() {
                  return new RabbitTemplate(rabbitConnectionFactory());
            }
      }
}
