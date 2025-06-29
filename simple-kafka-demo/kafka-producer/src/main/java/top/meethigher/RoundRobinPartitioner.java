
package top.meethigher;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * kafka官方提供的轮询策略有bug，会导致发送一条消息时，触发两次计算分区。
 * <p>
 * 因此如果想要实现在分区间均衡发送消息，则需要重写官方提供的轮询策略
 *
 * @see org.apache.kafka.clients.producer.RoundRobinPartitioner
 * @see <a href="https://issues.apache.org/jira/browse/KAFKA-9965">[KAFKA-9965] Uneven distribution with RoundRobinPartitioner in AK 2.4+ - ASF JIRA</a>
 * @see <a href="https://issues.apache.org/jira/browse/KAFKA-13303">[KAFKA-13303] RoundRobinPartitioner broken by KIP-480 - ASF JIRA</a>
 * @see <a href="https://github.com/apache/kafka/pull/11326">KAFKA-9965/KAFKA-13303: RoundRobinPartitioner broken by KIP-480 by jonmcewen · Pull Request #11326 · apache/kafka</a>
 */
public class RoundRobinPartitioner implements Partitioner {
    private final ConcurrentMap<String, AtomicInteger> topicCounterMap = new ConcurrentHashMap<>();

    public void configure(Map<String, ?> configs) {
    }

    /**
     * Compute the partition for the given record.
     *
     * @param topic      The topic name
     * @param key        The key to partition on (or null if no key)
     * @param keyBytes   serialized key to partition on (or null if no key)
     * @param value      The value to partition on or null
     * @param valueBytes serialized value to partition on or null
     * @param cluster    The current cluster metadata
     */
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        int nextValue = nextValue(topic);
        List<PartitionInfo> availablePartitions = cluster.availablePartitionsForTopic(topic);
        if (!availablePartitions.isEmpty()) {
            int part = Utils.toPositive(nextValue) % availablePartitions.size();
            return availablePartitions.get(part).partition();
        } else {
            // no partitions are available, give a non-available partition
            return Utils.toPositive(nextValue) % numPartitions;
        }
    }

    private int nextValue(String topic) {
        AtomicInteger counter = topicCounterMap.computeIfAbsent(topic, k -> new AtomicInteger(0));
        return counter.getAndIncrement();
    }

    public void close() {
    }

    @Override
    public void onNewBatch(String topic, Cluster cluster, int prevPartition) {
        AtomicInteger counter = topicCounterMap.computeIfAbsent(topic, k -> new AtomicInteger(0));
        counter.getAndDecrement();
    }
}
