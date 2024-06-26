/*
 * Copyright 2024 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.plugin.jdk.httpclient;

import com.navercorp.pinpoint.bootstrap.logging.PluginLogManager;
import com.navercorp.pinpoint.bootstrap.logging.PluginLogger;
import com.navercorp.pinpoint.bootstrap.plugin.request.util.CookieExtractor;
import com.navercorp.pinpoint.common.util.StringUtils;
import jdk.internal.net.http.HttpRequestImpl;

import java.util.Optional;

public class JdkHttpClientCookieExtractor implements CookieExtractor<HttpRequestImpl> {

    public static final CookieExtractor<HttpRequestImpl> INSTANCE = new JdkHttpClientCookieExtractor();

    private final PluginLogger logger = PluginLogManager.getLogger(this.getClass());
    private final boolean isDebug = logger.isDebugEnabled();

    @Override
    public String getCookie(HttpRequestImpl httpMethod) {
        final Optional<String> cookie = httpMethod.headers().firstValue("Cookie");
        if (isDebug) {
            logger.debug("Get cookie. request={}, header={}", httpMethod, cookie);
        }

        if (cookie != null && cookie.isPresent()) {
            final String value = cookie.get();
            if (StringUtils.hasLength(value)) {
                return value;
            }
        }
        return null;
    }
}