/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.synapse.aspects.flow.statistics.opentracing.management.helpers;

import io.opentracing.Span;
import org.apache.synapse.aspects.flow.statistics.data.raw.StatisticDataUnit;
import org.apache.synapse.aspects.flow.statistics.data.raw.StatisticsLog;
import org.apache.synapse.aspects.flow.statistics.opentracing.OpenTracingManagerHolder;
import org.apache.synapse.aspects.flow.statistics.opentracing.models.SpanWrapper;

/**
 * Applies tags to Spans.
 */
public class SpanTagger {

    /**
     * Prevents Instantiation.
     */
    private SpanTagger() {}

    /**
     * Sets tags to the span which is contained in the provided span wrapper, from information acquired from the
     * given basic statistic data unit.
     * @param spanWrapper               Span wrapper that contains the target span.
     */
    public static void setSpanTags(SpanWrapper spanWrapper) {
        StatisticsLog openStatisticsLog = new StatisticsLog(spanWrapper.getStatisticDataUnit());
        Span span = spanWrapper.getSpan();
        if (OpenTracingManagerHolder.isCollectingPayloads() || OpenTracingManagerHolder.isCollectingProperties()) {
            if (OpenTracingManagerHolder.isCollectingPayloads()) {
                if(openStatisticsLog.getBeforePayload() != null) {
                    span.setTag("beforePayload", openStatisticsLog.getBeforePayload());
                }
                StatisticDataUnit statisticDataUnit = spanWrapper.getCloseEventStatisticDataUnit();
                if (statisticDataUnit != null) {
                    if (statisticDataUnit.getPayload() != null) {
                        span.setTag("afterPayload", statisticDataUnit.getPayload());
                    }
                } else if (openStatisticsLog.getBeforePayload() !=null){
                    //This means a close event hasn't been triggered so payload is equal to before payload
                    span.setTag("afterPayload", openStatisticsLog.getBeforePayload());
                }
            }

            if (OpenTracingManagerHolder.isCollectingProperties()) {
                if (spanWrapper.getStatisticDataUnit().getContextPropertyMap() != null) {
                    span.setTag("beforeContextPropertyMap",
                            spanWrapper.getStatisticDataUnit().getContextPropertyMap().toString());
                }
                if (spanWrapper.getCloseEventStatisticDataUnit() != null) {
                    if (spanWrapper.getCloseEventStatisticDataUnit().getContextPropertyMap() != null) {
                        span.setTag("afterContextPropertyMap",
                                spanWrapper.getCloseEventStatisticDataUnit().getContextPropertyMap().toString());
                    }
                } else if (openStatisticsLog.getContextPropertyMap() != null) {
                    span.setTag("afterContextPropertyMap", openStatisticsLog.getContextPropertyMap().toString());
                }
                if (spanWrapper.getCloseEventStatisticDataUnit() != null &&
                        spanWrapper.getCloseEventStatisticDataUnit().getPropertyValue() != null) {
                    span.setTag("propertyMediatorValue",
                            spanWrapper.getCloseEventStatisticDataUnit().getPropertyValue());
                }
            }
        }
        if (openStatisticsLog.getComponentName() != null) {
            span.setTag("componentName", openStatisticsLog.getComponentName());
        }
        if(openStatisticsLog.getComponentTypeToString() != null){
            span.setTag("componentType", openStatisticsLog.getComponentTypeToString());
        }
        span.setTag("threadId", Thread.currentThread().getId());
        if(openStatisticsLog.getComponentId() != null){
            span.setTag("componentId", openStatisticsLog.getComponentId());
        }
        if(openStatisticsLog.getHashCode() != null){
            span.setTag("hashcode", openStatisticsLog.getHashCode());
        }
        if (openStatisticsLog.getTransportHeaderMap() != null) {
            span.setTag("Transport Headers", openStatisticsLog.getTransportHeaderMap().toString());
        }

        if (openStatisticsLog.getStatusCode() != null) {
            span.setTag("Status code", openStatisticsLog.getStatusCode());
        }
        if (openStatisticsLog.getStatusDescription() != null) {
            span.setTag("Status description", openStatisticsLog.getStatusDescription());
        }
        if (openStatisticsLog.getEndpoint() != null) {
            span.setTag("Endpoint", String.valueOf(openStatisticsLog.getEndpoint().getJsonRepresentation()));
        }
    }
}
