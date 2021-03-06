/**
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 */
package com.heliosapm.streams.common.metrics;

import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import javax.management.ObjectName;

import com.codahale.metrics.ConsoleReporter;
import com.heliosapm.utils.jmx.JMXHelper;

/**
 * <p>Title: SharedMetricsRegistryMBean</p>
 * <p>Description: Management interface for {@link SharedMetricsRegistry}</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.streams.common.metrics.SharedMetricsRegistryMBean</code></p>
 */

public interface SharedMetricsRegistryMBean {
	/** The service JMX ObjectName */
	public static final ObjectName OBJECT_NAME = JMXHelper.objectName("com.heliosapm.tsdbex.tracing:service=SharedMetricsRegistry");
	
	/**
	 * Starts the console reporter to System.out if not already running
	 * @param secs The frequency to report in seconds, defaults to 5
	 */
	public void startConsoleReporter(final Long secs);
	
	/**
	 * Indicates if the console reporter is enabled
	 * @return true if the console reporter is enabled, false otherwise
	 */
	public boolean isConsoleReporting();
	
	/**
	 * Starts the console reporter if not already running.
	 * Reports to System.out every 5 seconds
	 */
	public void startConsoleReporter();
	
}
