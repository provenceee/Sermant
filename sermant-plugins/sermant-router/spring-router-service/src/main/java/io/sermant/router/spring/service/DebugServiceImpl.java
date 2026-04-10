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

package io.sermant.router.spring.service;

import com.alibaba.fastjson.JSONObject;

import io.sermant.core.common.LoggerFactory;
import io.sermant.core.plugin.agent.entity.ExecuteContext;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DebugServiceImpl
 *
 * @author provenceee
 * @since 2026-04-10
 */
public class DebugServiceImpl implements DebugService {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    @Override
    public void logBefore(ExecuteContext context) {
        StringBuilder sb = new StringBuilder("logBefore----");
        sb.append("method is ").append(context.getMethod().toGenericString()).append("------");
        Object[] arguments = context.getArguments();
        if (arguments != null) {
            for (int i = 0; i < arguments.length; i++) {
                sb.append("arg index is ").append(i).append("-----");
                sb.append("arg is ").append(getLogMsg(arguments[i])).append("-----");
            }
        }
        LOGGER.info(sb.toString());
    }

    @Override
    public void logAfter(ExecuteContext context) {
        StringBuilder sb = new StringBuilder("logAfter----");
        sb.append("method is ").append(context.getMethod().toGenericString()).append("------");
        Object[] arguments = context.getArguments();
        if (arguments != null) {
            for (int i = 0; i < arguments.length; i++) {
                sb.append("arg index is ").append(i).append("-----");
                sb.append("arg is ").append(getLogMsg(arguments[i])).append("-----");
            }
        }
        Object result = context.getResult();
        if (result != null) {
            sb.append("result is ").append(getLogMsg(result)).append("-----");
        }
        LOGGER.info(sb.toString());
    }

    @Override
    public void logThrow(ExecuteContext context) {
        StringBuilder sb = new StringBuilder("logThrow----");
        sb.append("method is ").append(context.getMethod().toGenericString()).append("------");
        Object[] arguments = context.getArguments();
        if (arguments != null) {
            for (int i = 0; i < arguments.length; i++) {
                sb.append("arg index is ").append(i).append("-----");
                sb.append("arg is ").append(getLogMsg(arguments[i])).append("-----");
            }
        }
        Throwable throwable = context.getThrowable();
        if (throwable != null) {
            sb.append("throwable is ");
            LOGGER.log(Level.SEVERE, sb.toString(), throwable);
        } else {
            sb.append("throwable is null");
            LOGGER.info(sb.toString());
        }
    }

    private String getLogMsg(Object object) {
        try {
            return JSONObject.toJSONString(object);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "toJSONString error {0}", e);
            return object.toString();
        }
    }
}
