/*
 * Copyright (C) 2026-2026 Huawei Technologies Co., Ltd. All rights reserved.
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

package io.sermant.router.spring.interceptor;

import io.sermant.core.plugin.agent.entity.ExecuteContext;
import io.sermant.core.plugin.agent.interceptor.AbstractInterceptor;
import io.sermant.core.plugin.service.PluginServiceManager;
import io.sermant.router.spring.service.DebugService;

/**
 * DebugInterceptor
 *
 * @author provenceee
 * @since 2026-04-10
 */
public class DebugInterceptor extends AbstractInterceptor {
    private final DebugService debugService;

    /**
     * Constructor
     */
    public DebugInterceptor() {
        debugService = PluginServiceManager.getPluginService(DebugService.class);
    }

    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        debugService.logBefore(context);
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) throws Exception {
        debugService.logAfter(context);
        return context;
    }

    @Override
    public ExecuteContext onThrow(ExecuteContext context) throws Exception {
        debugService.logThrow(context);
        return context;
    }
}
