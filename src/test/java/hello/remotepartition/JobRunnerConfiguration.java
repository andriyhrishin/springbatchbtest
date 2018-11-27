package hello.remotepartition;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Dave Syer
 *
 */
@Configuration
@EnableBatchProcessing
public class JobRunnerConfiguration {

    @Bean
    public JobLauncherTestUtils utils() throws Exception {
        return new JobLauncherTestUtils();
    }

}
