package com.huaweicloud.sermant.mq.grayscale.kafka.interceptor;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.agent.interceptor.AbstractInterceptor;
import com.huaweicloud.sermant.core.utils.StringUtils;
import com.huaweicloud.sermant.core.utils.tag.TrafficTag;
import com.huaweicloud.sermant.core.utils.tag.TrafficUtils;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.logging.Logger;

public class KafkaProducerInterceptor extends AbstractInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger();

    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        LOGGER.info("sermant kafka producer: intercept");
        TrafficTag trafficTag = TrafficUtils.getTrafficTag();
        if (trafficTag == null || trafficTag.getTag() == null || trafficTag.getTag().get("x-cse-canary") == null
                || trafficTag.getTag().get("x-cse-canary").isEmpty()) {
            LOGGER.info("sermant kafka producer: traffic tag or x-cse-canary is empty");
            return context;
        }

        Object argument = context.getArguments()[0];
        if (!(argument instanceof ProducerRecord)) {
            LOGGER.info("sermant kafka producer: type is not producerRecord");
            return context;
        }

        String xCseCanary = trafficTag.getTag().get("x-cse-canary").get(0);
        LOGGER.info("sermant kafka producer: x-cse-canary=" + xCseCanary);
        if (StringUtils.isNoneBlank(xCseCanary) && !xCseCanary.contains("base")) {
            ProducerRecord producerRecord = (ProducerRecord) argument;
            ProducerRecord newProducerRecord =
                    new ProducerRecord(
                            producerRecord.topic() + "-" + xCseCanary,
                            producerRecord.partition(),
                            producerRecord.timestamp(),
                            producerRecord.key(),
                            producerRecord.value(),
                            producerRecord.headers());
            context.getArguments()[0] = newProducerRecord;
            LOGGER.info("sermant kafka producer: topic=" + newProducerRecord.topic());
        }

        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) throws Exception {
        return context;
    }
}
