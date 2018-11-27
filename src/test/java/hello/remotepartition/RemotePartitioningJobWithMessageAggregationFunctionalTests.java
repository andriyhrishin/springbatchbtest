package hello.remotepartition;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

/**
 * The master step of the job under test will create 3 partitions for workers
 * to process.
 *
 * @author Mahmoud Ben Hassine
 */
//@ContextConfiguration(classes = {JobRunnerConfiguration.class, MasterConfiguration.class})
//@ActiveProfiles({"test"})
public class RemotePartitioningJobWithMessageAggregationFunctionalTests { //extends RemotePartitioningJobFunctionalTests {

//    @Override
//    protected Class<WorkerConfiguration> getWorkerConfigurationClass() {
//        return WorkerConfiguration.class;
//    }

}
