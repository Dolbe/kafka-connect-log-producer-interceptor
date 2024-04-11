package com.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

@Slf4j
public class ProducerTraceLogInterceptor<K, V> implements ProducerInterceptor<K, V> {
    private String traceKeyName = "traceparent";

    private static String traceKeyConfig = "trace.log.header.key";

    @Override
    public ProducerRecord<K, V> onSend(final ProducerRecord<K, V> record) {
        if (record == null)
        {
            log.info("record is null. Skipping...");
            return record;
        }
        if (record.headers() == null)
        {
            log.info("record.headers() is null. Skipping...");
            return record;
        }

        var headers = record.headers().toArray();
        var traceHeader = Arrays.stream(headers)
                .filter(x -> x.key().equals(traceKeyName))
                .findFirst();
        if (traceHeader.isEmpty())
        {
            log.info("Record header with key '{}' not found. Skipping...", traceKeyName);
            return record;
        }

        var headerValue = new String(traceHeader.get().value(), StandardCharsets.UTF_8);

        log.info("ProducerTraceLogInterceptor: Message with trace Id '{}' sent to topic '{}'", headerValue, record.topic());

        return record;
    }

    @Override
    public void onAcknowledgement(final RecordMetadata metadata, final Exception exception) {
        log.info("ProducerTraceLogInterceptor: ack meta={}, exception={}", metadata, exception);
    }

    @Override
    public void close() {
        log.info("ProducerTraceLogInterceptor: closing");
    }

    @Override
    public void configure(final Map<String, ?> configs) {
        log.info("ProducerTraceLogInterceptor: configure with configs {}", configs);

        var keyConfigValue = (String)configs.get(traceKeyConfig);
        if (keyConfigValue != null)
            traceKeyName = keyConfigValue;

    }
}
