package com.huaweicloud.jdkhttp.declarers;

import com.huaweicloud.sermant.core.plugin.agent.declarer.AbstractPluginDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.declarer.InterceptDeclarer;
import com.huaweicloud.sermant.core.plugin.agent.matcher.ClassMatcher;
import com.huaweicloud.sermant.core.plugin.agent.matcher.MethodMatcher;
import com.huaweicloud.jdkhttp.interceptors.JakartaServletInterceptor;

/**
 * Servlet 5.x, 6.x enhanced declarer of traffic label transparent transmission
 *
 * @since 2025-05-22
 */
public class JakartaServletDeclarer extends AbstractPluginDeclarer {
    private static final String ENHANCE_CLASS = "jakarta.servlet.http.HttpServlet";

    private static final String METHOD_NAME = "service";

    private static final String[] METHOD_ARGS = {
            "jakarta.servlet.http.HttpServletRequest",
            "jakarta.servlet.http.HttpServletResponse"
    };

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.isExtendedFrom(ENHANCE_CLASS);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
                InterceptDeclarer.build(
                        MethodMatcher.nameEquals(METHOD_NAME)
                                .and(MethodMatcher.paramTypesEqual(METHOD_ARGS[0],METHOD_ARGS[1])),
                        new JakartaServletInterceptor())
        };
    }
}
