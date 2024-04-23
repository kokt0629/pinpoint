/*
 * Copyright 2017 NAVER Corp.
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

package com.navercorp.pinpoint.batch.alarm.collector;

import com.navercorp.pinpoint.batch.alarm.vo.DataSourceAlarmVO;
import com.navercorp.pinpoint.common.server.bo.stat.DataSourceBo;
import com.navercorp.pinpoint.common.server.bo.stat.DataSourceListBo;
import com.navercorp.pinpoint.common.server.util.time.Range;
import com.navercorp.pinpoint.common.util.CollectionUtils;
import com.navercorp.pinpoint.web.alarm.DataCollectorCategory;
import com.navercorp.pinpoint.web.dao.stat.AgentStatDao;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Taejin Koo
 */
public class DataSourceDataCollector extends DataCollector implements DataSourceDataGetter {
    private final AgentStatDao<DataSourceListBo> dataSourceDao;
    private final List<String> agentIds;
    private final long timeSlotEndTime;
    private final long slotInterval;

    private final MultiValueMap<String, DataSourceAlarmVO> agentDataSourceConnectionUsageRateMap = new LinkedMultiValueMap<>();

    private final AtomicBoolean init = new AtomicBoolean(false); // need to consider a race condition when checkers start simultaneously.

    public DataSourceDataCollector(
            DataCollectorCategory dataCollectorCategory,
            AgentStatDao<DataSourceListBo> dataSourceDao,
            List<String> agentIds,
            long timeSlotEndTime,
            long slotInterval
    ) {
        super(dataCollectorCategory);

        this.dataSourceDao = dataSourceDao;
        this.agentIds = agentIds;
        this.timeSlotEndTime = timeSlotEndTime;
        this.slotInterval = slotInterval;
    }

    @Override
    public void collect() {
        if (init.get()) {
            return;
        }

        Range range = Range.reverse(timeSlotEndTime - slotInterval, timeSlotEndTime);

        for (String agentId : agentIds) {
            List<DataSourceListBo> dataSourceListBos = dataSourceDao.getAgentStatList(agentId, range);
            MultiValueMap<Integer, DataSourceBo> partitions = partitionDataSourceId(dataSourceListBos);

            for (Map.Entry<Integer, List<DataSourceBo>> entry : partitions.entrySet()) {
                List<DataSourceBo> dataSourceBoList = entry.getValue();

                if (CollectionUtils.hasLength(dataSourceBoList)) {
                    double activeConnectionAvg = dataSourceBoList.stream()
                            .mapToInt(DataSourceBo::getActiveConnectionSize)
                            .average()
                            .orElse(-1);
                    double maxConnectionAvg = dataSourceBoList.stream()
                            .mapToInt(DataSourceBo::getMaxConnectionSize)
                            .average()
                            .orElse(-1);
                    DataSourceBo dataSourceBo = org.springframework.util.CollectionUtils.firstElement(dataSourceBoList);
                    if (dataSourceBo == null) {
                        continue;
                    }

                    DataSourceAlarmVO dataSourceAlarmVO = new DataSourceAlarmVO(dataSourceBo.getId(), dataSourceBo.getDatabaseName(),
                            (int) Math.floor(activeConnectionAvg), (int) Math.floor(maxConnectionAvg));

                    agentDataSourceConnectionUsageRateMap.add(agentId, dataSourceAlarmVO);
                }
            }
        }

        init.set(true);
    }

    private MultiValueMap<Integer, DataSourceBo> partitionDataSourceId(List<DataSourceListBo> dataSourceListBos) {
        MultiValueMap<Integer, DataSourceBo> result = new LinkedMultiValueMap<>();

        for (DataSourceListBo dataSourceListBo : dataSourceListBos) {
            List<DataSourceBo> dataSourceBos = dataSourceListBo.getList();
            if (CollectionUtils.isEmpty(dataSourceBos)) {
                continue;
            }

            for (DataSourceBo dataSourceBo : dataSourceBos) {
                int id = dataSourceBo.getId();

                if (dataSourceBo.getMaxConnectionSize() <= 0 || dataSourceBo.getActiveConnectionSize() < 0) {
                    continue;
                }

                result.add(id, dataSourceBo);
            }
        }

        return result;
    }


    @Override
    public Map<String, List<DataSourceAlarmVO>> getDataSourceConnectionUsageRate() {
        return agentDataSourceConnectionUsageRateMap;
    }

}
