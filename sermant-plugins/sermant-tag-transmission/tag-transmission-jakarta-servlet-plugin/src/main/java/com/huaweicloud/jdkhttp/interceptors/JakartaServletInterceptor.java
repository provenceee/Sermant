package com.huaweicloud.jdkhttp.interceptors;

import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.utils.tag.TrafficUtils;
import com.huaweicloud.sermant.tag.transmission.config.strategy.TagKeyMatcher;
import com.huaweicloud.sermant.tag.transmission.interceptors.AbstractServerInterceptor;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

public class JakartaServletInterceptor extends AbstractServerInterceptor<HttpServletRequest> {
    /**
     * Filter multiple calls of interceptors during a single processing
     */
    protected static final ThreadLocal<Boolean> LOCK_MARK = new ThreadLocal<>();

    private static final Logger LOGGER = LoggerFactory.getLogger();

    @Override
    public ExecuteContext doBefore(ExecuteContext context) {
        if (LOCK_MARK.get() != null) {
            return context;
        }
        LOCK_MARK.set(Boolean.TRUE);

        HttpServletRequest httpServletRequest = (HttpServletRequest) context.getArguments()[0];
        Map<String, List<String>> tagMap = extractTrafficTagFromCarrier(httpServletRequest);
        TrafficUtils.updateTrafficTag(tagMap);
        return context;
    }

    @Override
    public ExecuteContext doAfter(ExecuteContext context) {
        TrafficUtils.removeTrafficTag();
        LOCK_MARK.remove();
        return context;
    }

    @Override
    public ExecuteContext onThrow(ExecuteContext context) {
        TrafficUtils.removeTrafficTag();
        LOCK_MARK.remove();
        return context;
    }

    /**
     * Parse the traffic tag from the HttpServletRequest
     *
     * @param httpServletRequest servlet carrier of the traffic tag on the server
     * @return traffic tag map
     */
    @Override
    protected Map<String, List<String>> extractTrafficTagFromCarrier(HttpServletRequest httpServletRequest) {
        Map<String, List<String>> tagMap = new HashMap<>();

        Enumeration<String> keyEnumeration = httpServletRequest.getHeaderNames();
        while (keyEnumeration.hasMoreElements()) {
            String key = keyEnumeration.nextElement();
            if (!TagKeyMatcher.isMatch(key)) {
                continue;
            }
            Enumeration<String> valuesEnumeration = httpServletRequest.getHeaders(key);
            if (valuesEnumeration != null && valuesEnumeration.hasMoreElements()) {
                List<String> values = Collections.list(valuesEnumeration);
                tagMap.put(key, values);
                LOGGER.log(Level.FINE, "Traffic tag {0}={1} have been extracted from servlet.",
                        new Object[]{key, values});
                continue;
            }

            // If the value of the traffic label is null, you need to store the local variable to override the original
            // value to prevent misuse of the old traffic label
            tagMap.put(key, null);
            LOGGER.log(Level.FINE, "Traffic tag {0}=null have been extracted from servlet.", key);
        }
        return tagMap;
    }
}