/*
 *  Copyright (C) 2023-2023 Huawei Technologies Co., Ltd. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package io.sermant.mq.prohibition.controller.rocketmq.constant;

/**
 * rocketmq pulls the consumer subscription method enumeration class
 *
 * @author daizhenyu
 * @since 2023-12-05
 **/
public enum SubscriptionType {
    /**
     * The subscription type is NONE
     */
    NONE("NONE"),
    /**
     * consume data by subscribing to topics
     */
    SUBSCRIBE("SUBSCRIBE"),
    /**
     * Consumption through designated queues
     */
    ASSIGN("ASSIGN");

    private final String subscriptionName;

    SubscriptionType(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public String getSubscriptionTypeName() {
        return this.subscriptionName;
    }
}
