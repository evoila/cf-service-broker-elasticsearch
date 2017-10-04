/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom;

import de.evoila.cf.broker.bean.OpenstackBean;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.cpi.openstack.fluent.HeatFluent;
import de.evoila.cf.cpi.openstack.util.StackProgressObserver;
import org.openstack4j.model.heat.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Christian Mueller, evoila
 *
 */
@Service(value = "defaultStackHandler")
@ConditionalOnBean(OpenstackBean.class)
public class StackHandler {
	/**
	 * 
	 */
	private static final String TEMPLATE = "template";

	public static final boolean DEFAULT_DISABLE_ROLLBACK = false;

	public static final long DEFAULT_TIMEOUT_MINUTES = 10;

	private static String DEFAULT_ENCODING = "UTF-8";

	public static final String IMAGE_ID = "image_id";
	public static final String KEYPAIR = "keypair";
	public static final String NETWORK_ID = "network_id";
	public static final String AVAILABILITY_ZONE = "availability_zone";
	public static final String LOG_PORT = "log_port";
	public static final String LOG_HOST = "log_host";
	
	public static final String SCRIPT_REPO_MAIN = "repo_main";
	public static final String SCRIPT_REPO_MONIT = "repo_monit";
	public static final String SCRIPT_REPO_SERVICE = "repo_service";

	private Logger log = LoggerFactory.getLogger(getClass());

	private String defaultHeatTemplate;

	private String networkId;

	private String imageId;

	private String keypair;
	
	private String availabilityZone;

	@Autowired
	protected HeatFluent heatFluent;

	@Autowired
	protected StackProgressObserver stackProgressObserver;

	@Autowired
	private ApplicationContext appContext;
	
	@Autowired
	private OpenstackBean openstackBean;

	@PostConstruct
	public void initialize() {
		networkId = openstackBean.getNetworkId();
		imageId = openstackBean.getImageId();
		keypair = openstackBean.getKeypair();
		availabilityZone = openstackBean.getCinder().getAz();
		
		final String templatePath = "openstack/template.yml";
		defaultHeatTemplate = accessTemplate(templatePath);
	}

	protected String accessTemplate(final String templatePath) {

		try {
			//InputStream inputStream = appContext.getResource(templatePath).getInputStream();
			InputStream inputStream = new ClassPathResource(templatePath).getInputStream();
			return this.readTemplateFile(inputStream);
		} catch (IOException | URISyntaxException e) {
			log.info("Failed to load heat template", e);
			return defaultHeatTemplate;
		}
	}

	private String readTemplateFile(InputStream inputStream) throws IOException, URISyntaxException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) != -1) {
			result.write(buffer, 0, length);
		}
		return result.toString(DEFAULT_ENCODING);
	}

	public String create(String instanceId, Map<String, String> customParameters)
			throws PlatformException, InterruptedException {

		Map<String, String> completeParameters = new HashMap<String, String>();
		completeParameters.putAll(defaultParameters());
		completeParameters.putAll(customParameters);

		String heatTemplate;
		if (customParameters.containsKey(TEMPLATE)) {
			heatTemplate = accessTemplate(customParameters.get(TEMPLATE));
			completeParameters.remove(TEMPLATE);
		} else {
			heatTemplate = getDefaultHeatTemplate();
		}

		String name = HeatFluent.uniqueName(instanceId);

		heatFluent.create(name, heatTemplate, completeParameters, DEFAULT_DISABLE_ROLLBACK, DEFAULT_TIMEOUT_MINUTES);

		stackProgressObserver.waitForStackCompletion(name);

		return name;
	}

	public void delete(String internalId) {
		Stack stack = heatFluent.get(internalId);
		heatFluent.delete(stack.getName(), stack.getId());
	}
	
	public void deleteAndWait(String internalId) throws PlatformException {
		Stack stack = heatFluent.get(internalId);
		heatFluent.delete(stack.getName(), stack.getId());
		stackProgressObserver.waitForStackDeletion(stack.getName());
	}

	protected Map<String, String> defaultParameters() {
		Map<String, String> defaultParameters = new HashMap<String, String>();
		defaultParameters.put(IMAGE_ID, imageId);
		defaultParameters.put(KEYPAIR, keypair);
		defaultParameters.put(NETWORK_ID, networkId);
		defaultParameters.put(AVAILABILITY_ZONE, availabilityZone);
		return defaultParameters;
	}

	public String getDefaultHeatTemplate() {
		return defaultHeatTemplate;
	}
}
