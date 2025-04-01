package com.huaweicloud.sermant.mq.grayscale.kafka.interceptor;

import com.huaweicloud.sermant.core.config.ConfigManager;
import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.agent.interceptor.AbstractInterceptor;
import com.huaweicloud.sermant.core.plugin.config.ServiceMeta;
import com.huaweicloud.sermant.core.utils.StringUtils;
import org.springframework.kafka.support.TopicPartitionOffset;

import java.util.Map;
import java.util.regex.Pattern;

public class KafkaConsumerInterceptor extends AbstractInterceptor {

    private static final Map<String, String> parameters = ConfigManager.getConfig(ServiceMeta.class).getParameters();

    private static final String CAS_LANE_TAG = "cas_lane_tag";

    private static final String BASE_LANE_TAG = "base";

    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        if (parameters == null || BASE_LANE_TAG.equals(parameters.getOrDefault(CAS_LANE_TAG, BASE_LANE_TAG))) {
            return context;
        }

        Object argument = context.getArguments()[0];
        if (argument instanceof String[]) {
            return parse(context, (String[]) argument);
        }

        if (argument instanceof Pattern) {
            return parse(context, (Pattern) argument);
        }

        if (argument instanceof TopicPartitionOffset[]) {
            return parse(context, (TopicPartitionOffset[]) argument);
        }

        return context;
    }

    public ExecuteContext parse(ExecuteContext context, TopicPartitionOffset[] topicPartitionOffsets) {
        if (topicPartitionOffsets == null || topicPartitionOffsets.length == 0) {
            return context;
        }

        for (int i = 0; i < topicPartitionOffsets.length; i++) {
            TopicPartitionOffset topicPartitionOffset = topicPartitionOffsets[i];
            if (topicPartitionOffset == null) {
                continue;
            }
            String topic = "";
            if (topicPartitionOffset.getTopicPartition() != null && StringUtils.isNoneBlank(topicPartitionOffset.getTopicPartition().topic())) {
                topic = topicPartitionOffset.getTopicPartition().topic();
            }
            if (StringUtils.isNoneBlank(topicPartitionOffset.getTopic())) {
                topic = topicPartitionOffset.getTopic();
            }
            if (topic.endsWith("-" + parameters.get(CAS_LANE_TAG)) || StringUtils.isBlank(topic)) {
                continue;
            }
            topicPartitionOffsets[i] = new TopicPartitionOffset(
                    topic + "-" + parameters.get(CAS_LANE_TAG),
                    topicPartitionOffset.getTopicPartition().partition(),
                    topicPartitionOffset.getOffset(),
                    topicPartitionOffset.isRelativeToCurrent());
        }

        context.getArguments()[0] = topicPartitionOffsets;
        return context;
    }

    public ExecuteContext parse(ExecuteContext context, Pattern pattern) {
        if (pattern == null) {
            return context;
        }
        if (pattern.pattern().endsWith("-" + parameters.get(CAS_LANE_TAG))) {
            return context;
        }
        context.getArguments()[0] = Pattern.compile(pattern.pattern() + "-" + parameters.get(CAS_LANE_TAG));
        return context;
    }

    public ExecuteContext parse(ExecuteContext context, String[] topics) {
        if (topics == null || topics.length == 0) {
            return context;
        }

        for (int i = 0; i < topics.length; i++) {
            if (topics[i].endsWith("-" + parameters.get(CAS_LANE_TAG))) {
                continue;
            }
            topics[i] = topics[i] + "-" + parameters.get(CAS_LANE_TAG);
        }
        context.getArguments()[0] = topics;
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) throws Exception {
        return null;
    }
}
