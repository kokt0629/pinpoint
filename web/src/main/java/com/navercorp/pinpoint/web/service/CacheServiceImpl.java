/*
 * Copyright 2021 NAVER Corp.
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

package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.cache.CacheConfiguration;
import com.navercorp.pinpoint.web.view.TagApplications;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author yjqg6666
 * @author emeroad
 */
@Service
public class CacheServiceImpl implements CacheService {

    private final Cache cache;

    public CacheServiceImpl(@Qualifier(CacheConfiguration.APPLICATION_LIST_CACHE_NAME) CacheManager cacheManager) {
        Objects.requireNonNull(cacheManager, "cacheManager");
        this.cache = Objects.requireNonNull(cacheManager.getCache(CacheConfiguration.APPLICATION_LIST_CACHE_NAME));
    }

    @Override
    public TagApplications get(String key) {
        return this.cache.get(key, TagApplications.class);
    }

    @Override
    public void put(String key, TagApplications tagApplications) {
        Objects.requireNonNull(key, "key");

        this.cache.put(key, tagApplications);
    }


    public void remove(String key) {
        Objects.requireNonNull(key, "key");
        this.cache.evict(key);
    }


}
