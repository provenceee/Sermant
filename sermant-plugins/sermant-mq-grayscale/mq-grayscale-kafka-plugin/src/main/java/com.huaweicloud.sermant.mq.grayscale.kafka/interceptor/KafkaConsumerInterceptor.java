package com.huaweicloud.sermant.mq.grayscale.kafka.interceptor;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.config.ConfigManager;
import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.agent.interceptor.AbstractInterceptor;
import com.huaweicloud.sermant.core.plugin.config.ServiceMeta;
import com.huaweicloud.sermant.core.utils.StringUtils;
import org.springframework.kafka.support.TopicPartitionOffset;

import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class KafkaConsumerInterceptor extends AbstractInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger();

    private static final Map<String, String> parameters = ConfigManager.getConfig(ServiceMeta.class).getParameters();

    private static final String CAS_LANE_TAG = "cas_lane_tag";

    private static final String BASE_LANE_TAG = "base";

    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        LOGGER.info("sermant kafka consumer: intercept");
        if (parameters == null || BASE_LANE_TAG.equals(parameters.getOrDefault(CAS_LANE_TAG, BASE_LANE_TAG))) {
            LOGGER.info("sermant kafka consumer: parameters is null or tag is base");
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
        LOGGER.info("sermant kafka consumer: type is TopicPartitionOffset[]");
        if (topicPartitionOffsets == null || topicPartitionOffsets.length == 0) {
            LOGGER.info("sermant kafka consumer: TopicPartitionOffset[] is empty");
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
            LOGGER.info("sermant kafka consumer: TopicPartitionOffset " + i + " before topic=" + topic);
            if (topic.endsWith("-" + parameters.get(CAS_LANE_TAG)) || StringUtils.isBlank(topic)) {
                continue;
            }
            topicPartitionOffsets[i] = new TopicPartitionOffset(
                    topic + "-" + parameters.get(CAS_LANE_TAG),
                    topicPartitionOffset.getTopicPartition().partition(),
                    topicPartitionOffset.getOffset(),
                    topicPartitionOffset.isRelativeToCurrent());
            LOGGER.info("sermant kafka consumer: TopicPartitionOffset " + i + " after topic=" + topicPartitionOffsets[i].getTopic());
        }

        context.getArguments()[0] = topicPartitionOffsets;
        return context;
    }

    public ExecuteContext parse(ExecuteContext context, Pattern pattern) {
        LOGGER.info("sermant kafka consumer: type is Pattern");
        if (pattern == null) {
            LOGGER.info("sermant kafka consumer: pattern is null");
            return context;
        }
        LOGGER.info("sermant kafka consumer: before pattern=" + pattern.pattern());
        if (pattern.pattern().endsWith("-" + parameters.get(CAS_LANE_TAG))) {
            return context;
        }
        Pattern newPattern = Pattern.compile(pattern.pattern() + "-" + parameters.get(CAS_LANE_TAG));
        context.getArguments()[0] = newPattern;
        LOGGER.info("sermant kafka consumer: after pattern=" + newPattern.pattern());
        return context;
    }

    public ExecuteContext parse(ExecuteContext context, String[] topics) {
        LOGGER.info("sermant kafka consumer: type is String[]");
        if (topics == null || topics.length == 0) {
            LOGGER.info("sermant kafka consumer: String[] is empty");
            return context;
        }

        for (int i = 0; i < topics.length; i++) {
            LOGGER.info("sermant kafka consumer: before topic=" + topics[i]);
            if (topics[i].endsWith("-" + parameters.get(CAS_LANE_TAG))) {
                continue;
            }
            topics[i] = topics[i] + "-" + parameters.get(CAS_LANE_TAG);
            LOGGER.info("sermant kafka consumer: after topic=" + topics[i]);
        }
        context.getArguments()[0] = topics;
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) throws Exception {
        return null;
    }
}
