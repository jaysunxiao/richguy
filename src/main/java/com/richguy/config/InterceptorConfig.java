/*
 * Copyright (C) 2020 The zfoo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.richguy.config;

import com.richguy.util.HttpUtils;
import com.zfoo.protocol.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 如果需要SEO，就要开启这个配置，这个使用的就是puppeteer作为服务端渲染，详细见：seo-puppeteer/spider.js
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-07-29 20:42
 */
// @Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(InterceptorConfig.class);

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SpiderInterceptor());
    }


    private static class SpiderInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            var uri = request.getRequestURI();

            if (HttpUtils.isSpiderRequest(request)) {
                var url = request.getRequestURL().toString();
                logger.warn("发现可疑爬虫spider[{}][ua:{}][url:{}][uri:{}]", request, request.getHeader("user-agent"), url, uri);

                response.setCharacterEncoding(StringUtils.DEFAULT_CHARSET_NAME);
                response.setHeader("Content-Type", "text/html;charset=UTF-8");
                response.getWriter().write("Hello World!");
                return false;
            }

            return true;
        }

        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        }
    }

}
