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

package com.huawei.dubbo.registry.interceptor;

import com.huaweicloud.sermant.core.utils.ReflectUtils;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.jboss.resteasy.core.MessageBodyParameterInjector;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.ResourceLocator;

import java.io.ByteArrayInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

/**
 * resteasy反序列化器
 *
 * @author provenceee
 * @since 2022-08-18
 */
public class MethodInjectorImpl extends org.jboss.resteasy.core.MethodInjectorImpl {
    private final ObjectMapper mapper;

    /**
     * 构造方法
     *
     * @param resourceMethod 方法
     * @param factory 工厂
     */
    public MethodInjectorImpl(ResourceLocator resourceMethod, ResteasyProviderFactory factory) {
        super(resourceMethod, factory);
        mapper = new ObjectMapper();
    }

    @Override
    public Object[] injectArguments(HttpRequest input, HttpResponse response) {
        MediaType mediaType = input.getHttpHeaders().getMediaType();
        if (mediaType == null) {
            mediaType = MediaType.WILDCARD_TYPE;
        }
        MultivaluedMap<String, String> headers = input.getHttpHeaders().getRequestHeaders();
        try {
            Object[] args = new Object[this.params.length];
            List<Object> list = mapper.readValue(input.getInputStream(), new TypeReference<List<Object>>() {
            });
            if (this.params != null && this.params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    if (!(params[i] instanceof MessageBodyParameterInjector)) {
                        continue;
                    }
                    Optional<Object> type = ReflectUtils.getFieldValue(params[i], "type");
                    Optional<Object> genericType = ReflectUtils.getFieldValue(params[i], "genericType");
                    if (type.isPresent() && genericType.isPresent()) {
                        MessageBodyReader<?> reader = factory
                            .getServerMessageBodyReader((Class<?>) type.get(),
                                (Type) genericType.get(), new Annotation[0], MediaType.APPLICATION_JSON_TYPE);
                        String str;
                        if (reader instanceof JacksonJsonProvider) {
                            str = mapper.writeValueAsString(list.get(i));
                        } else {
                            str = String.valueOf(list.get(i));
                        }
                        Object obj = reader.readFrom((Class) type.get(), (Type) genericType.get(),
                            new Annotation[0], mediaType, headers,
                            new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)));
                        args[i] = obj;
                    }
                }
            }
            return args;
        } catch (WebApplicationException | Failure ex) {
            throw ex;
        } catch (Exception ex) {
            BadRequestException badRequest = new BadRequestException(
                Messages.MESSAGES.failedProcessingArguments(this.method.toString()), ex);
            badRequest.setLoggable(true);
            throw badRequest;
        }
    }
}
