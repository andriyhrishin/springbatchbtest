package hello.remotepartition;


import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.SimplePartitioner;
import org.springframework.batch.item.ExecutionContext;

/**
 * Simple partitioner for demonstration purpose.
 *
 */
public class BasicPartitioner implements Partitioner {

    private static final String PARTITION_KEY = "partition";

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitions = new HashMap<>();
        long j = System.currentTimeMillis();
        for (int i = 0; i < gridSize; i++) {
            ExecutionContext context = new ExecutionContext();
            partitions.put(PARTITION_KEY+j, context);
            context.put(PARTITION_KEY, PARTITION_KEY + j);
            j++;
        }
        for (String key : partitions.keySet()) {
            System.out.println("Sending " + key);
        }
        return partitions;
    }

}
