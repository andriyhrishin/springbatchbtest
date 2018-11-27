package hello;

import org.apache.activemq.broker.BrokerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@Profile({"brokerservice"})
public class BrokerServiceConfiguration {

    private static final String BROKER_DATA_DIRECTORY = "build/activemq-data";

    @Value("${broker.url}")
    private String brokerUrl;

    private BrokerService brokerService;

    @Bean(destroyMethod = "stop")
    public BrokerService connectionFactory() throws Exception {
        this.brokerService = new BrokerService();
        this.brokerService.addConnector(this.brokerUrl);
        this.brokerService.setDataDirectory(BROKER_DATA_DIRECTORY);
        this.brokerService.start();
        return brokerService;
    }
}
