/*
 * Copyright (c) 2016-2019 VMware, Inc. All Rights Reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 *
 * This product may include a number of subcomponents with separate copyright notices
 * and license terms. Your use of these subcomponents is subject to the terms and
 * conditions of the subcomponent's license, as noted in the LICENSE file.
 */

package com.vmware.mangle.metric.wavefront.reporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.util.CollectionUtils;

import com.wavefront.integrations.Wavefront;

import lombok.extern.log4j.Log4j2;

import com.vmware.mangle.metric.common.Metric;
import com.vmware.mangle.metric.common.MetricReporter;
import com.vmware.mangle.metric.utils.MetricsHelper;

/**
 * @author dbhat
 *
 */
@Log4j2
public class WavefrontMetricReporter implements MetricReporter {
    private Wavefront wavefrontReporter;
    private String wavefrontProxy;
    private Integer wavefrontProxyPort;
    private String metricSource;
    private HashMap<String, String> staticTags;

    public WavefrontMetricReporter(String wavefrontProxy, Integer wavefrontProxyPort, String metricSource,
            HashMap<String, String> staticTags) {
        this.wavefrontProxy = wavefrontProxy;
        this.wavefrontProxyPort = wavefrontProxyPort;
        this.metricSource = metricSource;
        this.staticTags = staticTags;
        wavefrontReporter = new Wavefront(wavefrontProxy, wavefrontProxyPort);
    }

    /**
     * Helper to send the specified metric with parameters to wavefront.
     *
     * @param metricName
     *            : Name of the metric. Valid metric name: request.count , system.load.avg.1m and
     *            Invalid Metric Names: requestCount, sytemLoadAvg1m
     *
     * @param metricValue:
     *            metric value associated with the metric.
     *
     * @param tags:
     *            Tags to be send along with metric.
     * @return
     */
    @Override
    public Boolean sendMetric(String metricName, double metricValue, HashMap<String, String> tags) {
        log.info(" Sending the Metric " + metricName + " with assosicated value: " + metricValue);
        log.info("Tags: " + tags.toString());
        if (null == wavefrontReporter) {
            return false;
        }
        if (validateMetric(metricName, metricValue)) {
            log.info("Sending the metric to wavefront Proxy");
            try {
                double doubleEqMetricValue = MetricsHelper.getDoubleEquivalent(metricValue);
                HashMap<String, String> allTags = WavefrontMetricHelper.constructTags(tags, this.staticTags);
                wavefrontReporter.send(metricName, doubleEqMetricValue, metricSource, allTags);
                wavefrontReporter.flush();
            } catch (IOException e) {
                log.error(" Exception while sending the metric to wavefront Proxy ");
                e.printStackTrace();
                log.error(e.getMessage());
                return false;
            }
            log.info(" Metric is sent");
            return true;
        }
        log.info(" Sending the metric Failed ");
        return false;
    }

    /**
     * @param metric
     *            : Metric to send
     * @return: true: if sending metric was successful. false: if the metric send operation has
     *          failed
     */
    public Boolean sendMetric(Metric metric) {
        log.info(" Sending the Metric: " + metric.getMetricName() + " with assosicated value: "
                + metric.getMetricValue());
        if (null == wavefrontReporter) {
            return false;
        }
        if (validateMetric(metric.getMetricName(), metric.getMetricValue())) {
            log.info("Metric details: " + metric.toString());
            try {
                double metricValue = MetricsHelper.getDoubleEquivalent(metric.getMetricValue());
                HashMap<String, String> allTags =
                        WavefrontMetricHelper.constructTags(metric.getTags(), this.staticTags);
                wavefrontReporter.send(metric.getMetricName(), metricValue, metric.getMetricTimeStamp(),
                        metric.getSource(), allTags);
                wavefrontReporter.flush();
            } catch (IOException e) {
                log.error(" Exception while sending the metric to wavefront Proxy ");
                e.printStackTrace();
                log.error(e.getMessage());
                return false;
            }
            log.info(" Metric is sent");
            return true;
        }
        log.info(" Sending the metric Failed ");
        return false;
    }

    /**
     * Method to send list of Metrics at a time.
     *
     * @param metrics
     *            : List of metrics.
     * @return: true: if all the metrics are sent to wavefront. false: if any one of the metric
     *          reporting has failed.
     */
    public Boolean sendMetrics(ArrayList<Metric> metrics) {
        log.info(" Sending list of metrics ");
        if (null == wavefrontReporter) {
            return false;
        }
        Boolean metricsSent = true;
        if (CollectionUtils.isEmpty(metrics)) {
            log.error(" Emptry metric list. No valid metrics to send ");
            return false;
        }
        for (Metric metric : metrics) {
            Boolean metricSent = sendMetric(metric) ? true : false;
            if (!metricSent) {
                metricsSent = false;
            }
        }
        log.info("Completed sending the list of metrics and status of Sending the metric: " + metricsSent);
        return metricsSent;
    }

    @Override
    public Boolean validateMetric(String metricName, Object metricValue) {
        if (MetricsHelper.isAValidMetricName(metricName) && MetricsHelper.isAValidMetricValue(metricValue)) {
            log.info("Specified metric is a valid metric");
            return true;
        }
        log.info(" Specified Metric is NOT a valid metric ");
        return false;
    }

    public String getWavefrontProxy() {
        return wavefrontProxy;
    }

    public String getMetricSource() {
        return metricSource;
    }

}
