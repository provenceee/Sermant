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

package io.sermant.monitor.declarer;

import io.sermant.monitor.interceptor.AlibabaDubboInterceptor;

/**
 * Alibaba dubbo Declarer
 *
 * @author zhp
 * @since 2022-11-01
 */
public class AlibabaDubboDeclarer extends AbstractDeclarer {
    private static final String[] ENHANCE_CLASS = {"com.alibaba.dubbo.monitor.support.MonitorFilter"};

    private static final String INTERCEPT_CLASS = AlibabaDubboInterceptor.class.getCanonicalName();

    private static final String METHOD_NAME = "invoke";

    /**
     * construction method
     */
    public AlibabaDubboDeclarer() {
        super(ENHANCE_CLASS, INTERCEPT_CLASS, METHOD_NAME);
    }
}
