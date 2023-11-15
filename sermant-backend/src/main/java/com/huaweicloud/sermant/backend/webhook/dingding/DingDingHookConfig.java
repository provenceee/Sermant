/*
 * Copyright (C) 2023-2023 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huaweicloud.sermant.backend.webhook.dingding;

import com.huaweicloud.sermant.backend.webhook.WebHookConfig;
import com.huaweicloud.sermant.backend.webhook.WebhookConfigImpl;

/**
 * 钉钉webhook 配置
 *
 * @author xuezechao
 * @since 2023-03-02
 */
public class DingDingHookConfig extends WebhookConfigImpl {
    private static final WebHookConfig CONFIG = new WebhookConfigImpl();

    private DingDingHookConfig() {
    }

    /**
     * 获取webhook配置单例
     *
     * @return webhook配置单例
     */
    public static WebHookConfig getInstance() {
        return CONFIG;
    }
}
