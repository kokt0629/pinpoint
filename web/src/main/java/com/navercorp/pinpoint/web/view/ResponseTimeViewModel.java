/*
 * Copyright 2014 NAVER Corp.
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

package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author emeroad
 */
public class ResponseTimeViewModel implements TimeViewModel {

    private final String columnName;
    private final List<TimeCount> columnValue;

    public ResponseTimeViewModel(String columnName, List<TimeCount> columnValue) {
        this.columnName = Objects.requireNonNull(columnName, "columnName");
        this.columnValue = Objects.requireNonNull(columnValue, "columnValue");
    }

    @JsonProperty("key")
    public String getColumnName() {
        return columnName;
    }

    @JsonProperty("values")
    public List<TimeCount> getColumnValue() {
        return columnValue;
    }

    @JsonSerialize(using = TimeCountSerializer.class)
    public record TimeCount(long time, long count) {
    }

    public static class TimeCountSerializer extends JsonSerializer<TimeCount> {
        @Override
        public void serialize(ResponseTimeViewModel.TimeCount count, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeStartArray();
            jgen.writeNumber(count.time());
            jgen.writeNumber(count.count());
            jgen.writeEndArray();
        }
    }

}
