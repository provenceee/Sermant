package com.huaweicloud.sermant.mq.grayscale.kafka.declarer;

import com.huaweicloud.sermant.core.plugin.agent.declarer.AbstractPluginDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.declarer.InterceptDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.matcher.ClassMatcher;
import com.huaweicloud.sermant.core.plugin.agent.matcher.MethodMatcher;
import com.huaweicloud.sermant.mq.grayscale.kafka.interceptor.KafkaConsumerInterceptor;

public class KafkaConsumerDeclarer extends AbstractPluginDeclarer {

    private static final String ENHANCE_CLASS = "org.springframework.kafka.listener.ContainerProperties";

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameEquals(ENHANCE_CLASS);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
                InterceptDeclarer.build(MethodMatcher.isConstructor(),
                    new KafkaConsumerInterceptor())
        };
    }
}
