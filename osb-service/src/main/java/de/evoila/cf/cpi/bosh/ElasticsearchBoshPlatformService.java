package de.evoila.cf.cpi.bosh;

import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.DashboardClient;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServerAddress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.broker.service.availability.ServicePortAvailabilityVerifier;
import io.bosh.client.deployments.Deployment;
import io.bosh.client.errands.ErrandSummary;
import io.bosh.client.tasks.Task;
import io.bosh.client.vms.Vm;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnBean(BoshProperties.class)
public class ElasticsearchBoshPlatformService extends BoshPlatformService {

    private static final int defaultPort = 9200;

    ElasticsearchBoshPlatformService(PlatformRepository repository, CatalogService catalogService,
                             ServicePortAvailabilityVerifier availabilityVerifier,
                             BoshProperties boshProperties, Optional<DashboardClient> dashboardClient) throws PlatformException {
        super(repository, catalogService, availabilityVerifier, boshProperties, dashboardClient, new ElasticsearchDeploymentManager(boshProperties));
    }

    protected void runDeleteErrands(ServiceInstance instance, Deployment deployment, Observable<List<ErrandSummary>> errands) { }

    // TODO: Discuss how the deployment name, which is currently prefixed with "sb-" should be handled
    @Override
    protected void updateHosts(ServiceInstance serviceInstance, Plan plan, Deployment deployment) {
        final int port;
        if (plan.getMetadata().containsKey(ElasticsearchDeploymentManager.PORT)) {
            port = (int) plan.getMetadata().get(ElasticsearchDeploymentManager.PORT);
        } else {
            port = defaultPort;
        }

        List<Vm> vms = connection.connection().vms().listDetails("sb-" + serviceInstance.getId()).toBlocking().first();
        if(serviceInstance.getHosts() == null)
            serviceInstance.setHosts(new ArrayList<>());

        serviceInstance.getHosts().clear();

        vms.forEach(vm -> {
            serviceInstance.getHosts().add(new ServerAddress("Host-" + vm.getIndex(), vm.getIps().get(0), port));
        });
    }
}
