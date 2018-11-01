	package de.evoila.cf.broker.haproxy;

import java.util.ArrayList;
import java.util.List;

import de.evoila.cf.broker.bean.HAProxyConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.model.Mode;
import de.evoila.cf.broker.model.ServerAddress;
import de.evoila.cf.broker.service.HAProxyService;

/**
 * @author Johannes Hiemer.
 */
@Service
@ConditionalOnBean(HAProxyConfiguration.class)
public class HAProxyServiceImpl extends HAProxyService {

	public HAProxyServiceImpl(HAProxyConfiguration haProxyConfiguration) {
        super(haProxyConfiguration);
    }

	@Override
	public Mode getMode(ServerAddress serverAddress) {
		return Mode.TCP;
	}
	
	@Override
	public List<String> getOptions(ServerAddress serverAddress) {
		return new ArrayList<String>();
	}
}