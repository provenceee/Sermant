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

package io.sermant.router.spring.declarer;

import io.sermant.core.plugin.agent.declarer.AbstractPluginDeclarer;
import io.sermant.core.plugin.agent.declarer.InterceptDeclarer;
import io.sermant.core.plugin.agent.matcher.ClassMatcher;
import io.sermant.core.plugin.agent.matcher.MethodMatcher;
import io.sermant.router.spring.interceptor.DebugInterceptor;

/**
 * DebugPluginDeclarer
 *
 * @author provenceee
 * @since 2026-04-10
 */
public class DebugPluginDeclarer extends AbstractPluginDeclarer {
    @Override
    public ClassMatcher getClassMatcher() {
        String debugClassName = System.getenv("DEBUG_CLASS_NAME");
        return ClassMatcher.nameContains(debugClassName.split(","));
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        String debugMethodName = System.getenv("DEBUG_METHOD_NAME");
        return new InterceptDeclarer[] {
                InterceptDeclarer.build(MethodMatcher.nameContains(debugMethodName.split(",")),
                        new DebugInterceptor())};
    }
}
