package com.test.oschina.utils;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import org.apache.kafka.clients.producer.Callback;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Kafka工具类：封装消息发送和接收功能
 */
public class KafkaUtils {

    // 默认Kafka Broker地址
    private static final String DEFAULT_BOOTSTRAP_SERVERS = "localhost:9092";

    /**
     * 创建Kafka生产者配置
     * @param bootstrapServers Broker地址（多个用逗号分隔）
     * @return 生产者配
     */
    private static Properties getProducerProperties(String bootstrapServers) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "1"); // 消息确认机制
        props.put(ProducerConfig.RETRIES_CONFIG, 3); // 重试次数
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // 批量发送大小（字节）
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1); // 批量发送延迟（毫秒）
        return props;
    }

    /**
     * 创建Kafka消费者配置
     * @param bootstrapServers Broker地址
     * @param groupId 消费者组ID
     * @return 消费者配置
     */
    private static Properties getConsumerProperties(String bootstrapServers, String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // 从最早位置消费
        return props;
    }

    // ------------------------------ 生产者相关方法 ------------------------------

    /**
     * 异步发送消息（带回调）
     * @param topic 主题名
     * @param key 消息键（可为null）
     * @param value 消息内容
     */
    public static void sendMessageAsync(String topic, String key, String value) {
        sendMessageAsync(DEFAULT_BOOTSTRAP_SERVERS, topic, key, value, (metadata, exception) -> {
            if (exception == null) {
                System.out.printf("发送成功 - 主题：%s，分区：%d，偏移量：%d%n",
                        metadata.topic(), metadata.partition(), metadata.offset());
            } else {
                System.err.printf("发送失败：%s%n", exception.getMessage());
            }
        });
    }

    /**
     * 异步发送消息（自定义回调）
     * @param bootstrapServers Broker地址
     * @param topic 主题名
     * @param key 消息键
     * @param value 消息内容
     * @param callback 发送回调
     */
    public static void sendMessageAsync(String bootstrapServers, String topic, String key, String value,
                                        org.apache.kafka.clients.producer.Callback callback) {
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(getProducerProperties(bootstrapServers))) {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
            producer.send(record, callback);
            producer.flush(); // 刷新缓冲区
        }
    }

    /**
     * 同步发送消息（阻塞等待结果）
     * @param topic 主题名
     * @param value 消息内容
     * @return 消息元数据（包含分区、偏移量等）
     * @throws ExecutionException 发送异常
     * @throws InterruptedException 线程中断异常
     */
    public static RecordMetadata sendMessageSync(String topic, String value) throws ExecutionException, InterruptedException {
        return sendMessageSync(DEFAULT_BOOTSTRAP_SERVERS, topic, null, value);
    }

    /**
     * 同步发送消息（自定义Broker和键）
     */
    public static RecordMetadata sendMessageSync(String bootstrapServers, String topic, String key, String value)
            throws ExecutionException, InterruptedException {
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(getProducerProperties(bootstrapServers))) {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
            return producer.send(record).get(); // 阻塞等待结果
        }
    }

    // ------------------------------ 消费者相关方法 ------------------------------

    /**
     * 消费消息（持续监听，需手动停止）
     * @param topic 主题名
     * @param groupId 消费者组ID
     * @param messageHandler 消息处理逻辑（自定义消费行为）
     */
    public static void consumeMessages(String topic, String groupId, Consumer<ConsumerRecord<String, String>> messageHandler) {
        consumeMessages(DEFAULT_BOOTSTRAP_SERVERS, topic, groupId, messageHandler);
    }

    /**
     * 消费消息（自定义Broker地址）
     */
    public static void consumeMessages(String bootstrapServers, String topic, String groupId,
                                       Consumer<ConsumerRecord<String, String>> messageHandler) {
        Properties props = getConsumerProperties(bootstrapServers, groupId);
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(topic)); // 订阅主题
            System.out.println("开始消费消息（按Ctrl+C停止）...");

            while (true) {
                // 拉取消息（超时时间1秒）
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    messageHandler.accept(record); // 调用自定义处理逻辑
                }
            }
        } catch (Exception e) {
            System.err.println("消费异常：" + e.getMessage());
        }
    }
// ------------------------------ 批量发送消息方法 ------------------------------

    /**
     * 批量异步发送消息（默认Broker地址，带默认回调）
     * @param topic 主题名
     * @param messages 消息列表（仅包含value，key为null）
     */
    public static void sendMessagesBatchAsync(String topic, List<String> messages) {
        sendMessagesBatchAsync(DEFAULT_BOOTSTRAP_SERVERS, topic, messages,
                (metadata, exception) -> {
                    if (exception == null) {
                        System.out.printf("批量消息中一条发送成功 - 主题：%s，分区：%d，偏移量：%d%n",
                                metadata.topic(), metadata.partition(), metadata.offset());
                    } else {
                        System.err.printf("批量消息中一条发送失败：%s%n", exception.getMessage());
                    }
                });
    }

    /**
     * 批量异步发送消息（带键值对，自定义Broker和回调）
     * @param bootstrapServers Broker地址
     * @param topic 主题名
     * @param keyValueMessages 键值对消息列表（格式：List.of("key1:value1", "key2:value2")）
     * @param callback 每条消息的发送回调
     */
    public static void sendMessagesBatchAsync(String bootstrapServers, String topic,
                                                     List<String> keyValueMessages, Callback callback) {
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(getProducerProperties(bootstrapServers))) {
            // 解析键值对（格式：key:value），若不含":"则key为null
            List<ProducerRecord<String, String>> records = keyValueMessages.stream()
                    .map(msg -> {
                        String[] keyValue = msg.split(":", 2); // 最多分割成2部分
                        String key = keyValue.length > 1 ? keyValue[0] : null;
                        String value = keyValue.length > 1 ? keyValue[1] : keyValue[0];
                        return new ProducerRecord<>(topic, key, value);
                    })
                    .collect(Collectors.toList());

            // 批量发送每条消息
            for (ProducerRecord<String, String> record : records) {
                producer.send(record, callback);
            }
            producer.flush(); // 批量刷新缓冲区
        }
    }

    /**
     * 批量同步发送消息（阻塞等待所有消息发送完成）
     * @param topic 主题名
     * @param messages 消息列表（仅value）
     * @return 所有消息的元数据列表
     * @throws ExecutionException 发送异常
     * @throws InterruptedException 线程中断
     */
    public static List<RecordMetadata> sendMessagesBatchSync(String topic, List<String> messages)
            throws ExecutionException, InterruptedException {
        return sendMessagesBatchSync(DEFAULT_BOOTSTRAP_SERVERS, topic, messages);
    }

    /**
     * 批量同步发送消息（自定义Broker）
     */
    public static List<RecordMetadata> sendMessagesBatchSync(String bootstrapServers, String topic, List<String> messages)
            throws ExecutionException, InterruptedException {
        try (KafkaProducer<Object, String> producer = new KafkaProducer<>(getProducerProperties(bootstrapServers))) {
            // 构建消息列表（key为null）
            List<ProducerRecord<Object, String>> records = messages.stream()
                    .map(value -> new ProducerRecord<>(topic, null, value))
                    .collect(Collectors.toList());

            // 同步发送所有消息并收集结果
            return records.stream()
                    .map(record -> {
                        try {
                            return producer.send(record).get(); // 阻塞等待单条消息结果
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException("批量同步发送失败：" + e.getMessage(), e);
                        }
                    })
                    .collect(Collectors.toList());
        }
}
}