/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huaweicloud.sermant.router.spring.service;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.utils.tag.TrafficTag;
import com.huaweicloud.sermant.core.utils.tag.TrafficUtils;
import com.huaweicloud.sermant.router.common.request.RequestData;
import com.huaweicloud.sermant.router.spring.handler.HandlerChainEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * BaseLoadBalancerInterceptor服务
 *
 * @author provenceee
 * @since 2022-07-20
 */
public class LoadBalancerServiceImpl implements LoadBalancerService {

    private static final Logger LOGGER = LoggerFactory.getLogger();

    @Override
    public List<Object> getTargetInstances(String targetName, List<Object> instances, RequestData requestData) {
        TrafficTag trafficTag = TrafficUtils.getTrafficTag();
        if (requestData != null && trafficTag != null && trafficTag.getTag() != null) {
            LOGGER.info("sermant getTargetInstances: intercept");
            Map<String, List<String>> newRequestHeader = new HashMap<>();
            newRequestHeader.putAll(trafficTag.getTag());
            newRequestHeader.putAll(requestData.getTag());
            requestData = new RequestData(newRequestHeader, requestData.getPath(), requestData.getHttpMethod());
        }
        if (requestData == null) {
            LOGGER.info("sermant getTargetInstances: requestData is null");
        } else {
            LOGGER.info("sermant getTargetInstances: requestData path=" + requestData.getPath() + " httpMethod=" + requestData.getHttpMethod());
            if (requestData.getTag() != null && !requestData.getTag().isEmpty()) {
                LOGGER.info("sermant getTargetInstances: requestData tags=" + requestData.getTag().toString());
            }
        }
        return HandlerChainEntry.INSTANCE.process(targetName, instances, requestData);
    }
}