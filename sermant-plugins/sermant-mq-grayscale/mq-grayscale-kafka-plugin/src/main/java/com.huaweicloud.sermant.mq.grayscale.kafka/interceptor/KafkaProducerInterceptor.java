package com.huaweicloud.sermant.mq.grayscale.kafka.interceptor;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.agent.interceptor.AbstractInterceptor;
import com.huaweicloud.sermant.core.utils.StringUtils;
import com.huaweicloud.sermant.core.utils.tag.TrafficTag;
import com.huaweicloud.sermant.core.utils.tag.TrafficUtils;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaProducerInterceptor extends AbstractInterceptor {
    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        TrafficTag trafficTag = TrafficUtils.getTrafficTag();
        if (trafficTag == null || trafficTag.getTag() == null || trafficTag.getTag().get("x-cse-canary") == null
                || trafficTag.getTag().get("x-cse-canary").isEmpty()) {
            return context;
        }

        Object argument = context.getArguments()[0];
        if (!(argument instanceof ProducerRecord)) {
            return context;
        }

        String xCseCanary = trafficTag.getTag().get("x-cse-canary").get(0);
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
        }
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) throws Exception {
        return context;
    }
}
