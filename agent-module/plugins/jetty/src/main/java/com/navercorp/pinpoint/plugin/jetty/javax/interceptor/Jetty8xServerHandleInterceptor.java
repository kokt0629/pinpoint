/*
 * Copyright 2018 NAVER Corp.
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

package com.navercorp.pinpoint.plugin.jetty.javax.interceptor;

import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.plugin.RequestRecorderFactory;
import com.navercorp.pinpoint.common.util.ArrayArgumentUtils;
import org.eclipse.jetty.server.AbstractHttpConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author Taejin Koo
 * @author jaehong.kim
 * <p>
 * jetty-8.1, jetty-8.2
 */
public class Jetty8xServerHandleInterceptor extends AbstractServerHandleInterceptor {

    public Jetty8xServerHandleInterceptor(TraceContext traceContext, RequestRecorderFactory requestRecorderFactory) {
        super(traceContext, requestRecorderFactory);
    }

    @Override
    HttpServletRequest toHttpServletRequest(Object[] args) {
        AbstractHttpConnection connection = getArgument(args);
        if (connection != null) {
            try {
                return connection.getRequest();
            } catch (Throwable ignored) {
                // ignore
            }
        }
        return null;
    }

    @Override
    HttpServletResponse toHttpServletResponse(Object[] args) {
        AbstractHttpConnection connection = getArgument(args);
        if (connection != null) {
            try {
                return connection.getResponse();
            } catch (Throwable ignored) {
                // ignore
            }
        }
        return null;
    }

    private AbstractHttpConnection getArgument(Object[] args) {
        return ArrayArgumentUtils.getArgument(args, 0, AbstractHttpConnection.class);
    }
}