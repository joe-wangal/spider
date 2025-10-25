package com.test.oschina;

import com.test.oschina.utils.KafkaUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Kafka工具类测试
 */
public class KafkaUtilsTest {

    private static final String TOPIC = "test-topic";
    private static final String GROUP_ID = "test-group";

    public static void main(String[] args) {
        // 1. 启动消费者（单独线程，避免阻塞生产者）
        new Thread(() -> {
            KafkaUtils.consumeMessages(TOPIC, GROUP_ID, record -> {
                // 自定义消息处理逻辑
                System.out.printf("收到消息 - 键：%s，值：%s，分区：%d，偏移量：%d%n",
                        record.key(), record.value(), record.partition(), record.offset());
            });
        }).start();

        // 2. 发送消息
        try {
            // 同步发送1条消息
            KafkaUtils.sendMessageSync(TOPIC, "同步消息：Hello Kafka Sync!");

            // 异步发送3条消息
            for (int i = 0; i < 3; i++) {
                KafkaUtils.sendMessageAsync(TOPIC, "key-" + i, "异步消息 " + i + "：Hello Kafka Async!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}