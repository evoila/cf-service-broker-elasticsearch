	package de.evoila.cf.broker.haproxy;

	import de.evoila.cf.broker.bean.HAProxyConfiguration;
	import de.evoila.cf.broker.model.Mode;
	import de.evoila.cf.broker.model.catalog.ServerAddress;
	import de.evoila.cf.broker.service.HAProxyService;
	import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
	import org.springframework.stereotype.Service;

	import java.util.ArrayList;
	import java.util.List;

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