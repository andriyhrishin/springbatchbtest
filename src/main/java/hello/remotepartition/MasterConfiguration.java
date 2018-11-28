package hello.remotepartition;

import hello.BrokerConfiguration;
import hello.DataSourceConfiguration;
import org.apache.activemq.ActiveMQConnectionFactory;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningMasterStepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * This configuration class is for the master side of the remote partitioning sample.
 * The master step will create 3 partitions for workers to process.
 *
 * @author Mahmoud Ben Hassine
 */
@Configuration
@EnableBatchProcessing
@EnableBatchIntegration
@Import(value = {DataSourceConfiguration.class, BrokerConfiguration.class})
@Profile({"master", "test"})
public class MasterConfiguration {

    private static final int GRID_SIZE = 3;

    @Autowired
    protected JobRepository jobRepository;
    @Autowired
    protected JobLauncher jobLauncher;
    @Autowired
    protected JobRegistry jobRegistry;
    @Autowired
    protected PlatformTransactionManager txManager;

    private final JobBuilderFactory jobBuilderFactory;

    private final RemotePartitioningMasterStepBuilderFactory masterStepBuilderFactory;


    public MasterConfiguration(JobBuilderFactory jobBuilderFactory,
                               RemotePartitioningMasterStepBuilderFactory masterStepBuilderFactory) {

        this.jobBuilderFactory = jobBuilderFactory;
        this.masterStepBuilderFactory = masterStepBuilderFactory;
    }

    /*
     * Configure outbound flow (requests going to workers)
     */
    @Bean
    public DirectChannel requests() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outboundFlow(ActiveMQConnectionFactory connectionFactory) {
        return IntegrationFlows
                .from(requests())
                .handle(Jms.outboundAdapter(connectionFactory).destination("requests"))
                .get();
    }

    /*
     * Configure inbound flow (replies coming from workers)
     */
    @Bean
    public DirectChannel replies() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow inboundFlow(ActiveMQConnectionFactory connectionFactory) {
        return IntegrationFlows
                .from(Jms.messageDrivenChannelAdapter(connectionFactory).destination("replies"))
                .channel(replies())
                .get();
    }

    /*
     * Configure the master step
     */
    @Bean
    public Step masterStep() {
        return this.masterStepBuilderFactory.get("masterStep")
                .partitioner("workerStep", new BasicPartitioner())
                .gridSize(GRID_SIZE)
                .outputChannel(requests())
                .inputChannel(replies())
                .build();
    }

    @Bean
    public Job remotePartitioningJob() {
        return this.jobBuilderFactory.get("remotePartitioningJob")
                .start(masterStep())
                .preventRestart()
                .listener(new JobExecutionListener() {
                    public void beforeJob(JobExecution jobExecution) { System.out.println("JobExecutionListener.beforeJob: " + jobExecution.toString()); }
                    public void afterJob(JobExecution jobExecution) {
                        System.out.println("JobExecutionListener.afterJob: " + jobExecution.toString());
                        if( jobExecution.getStatus() == BatchStatus.COMPLETED ){
                            //job success
                        }
                        else if(jobExecution.getStatus() == BatchStatus.FAILED){
                            //job failure
                        }
                    }
                })
                .validator(new DefaultJobParametersValidator(new String[] {"systime"}, new String[0]))
                .build();
    }

}
