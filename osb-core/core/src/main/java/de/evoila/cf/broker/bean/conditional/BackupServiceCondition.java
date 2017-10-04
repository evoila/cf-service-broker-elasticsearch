package de.evoila.cf.broker.bean.conditional;

import de.evoila.cf.broker.bean.BackupConfiguration;
import de.evoila.cf.broker.service.InstanceCredentialService;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

public class BackupServiceCondition extends AllNestedConditions{
    public BackupServiceCondition () {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @ConditionalOnBean(BackupConfiguration.class)
    void onBackupConfiguration() {}

    @ConditionalOnBean(ConnectionFactory.class)
    void onConnectionFactory() {}

    @ConditionalOnBean(InstanceCredentialService.class)
    void onCredentialService() {}
}
