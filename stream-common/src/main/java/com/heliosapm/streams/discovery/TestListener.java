/*
 * Copyright 2015 the original author or authors.
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
package com.heliosapm.streams.discovery;

import com.heliosapm.utils.io.StdInCommandHandler;

/**
 * <p>Title: TestListener</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.streams.discovery.TestListener</code></p>
 */

public class TestListener {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final EndpointPubSub p = EndpointPubSub.getInstance();
		p.addEndpointListener(new AdvertisedEndpointListener() {
			public void onOnlineAdvertisedEndpoint(final AdvertisedEndpoint endpoint) {
				log("ONLINE:" + endpoint);
			}
			public void onOfflineAdvertisedEndpoint(final AdvertisedEndpoint endpoint) {
				elog("OFFLINE:" + endpoint);
			}
		});
		StdInCommandHandler.getInstance()
		.registerCommand("stop", new Runnable(){
			public void run() {
				p.close();
				System.exit(0);
			}
		})
		.run();

	}
	
	public static void log(Object msg) {
		System.out.println(msg);
	}
	public static void elog(Object msg) {
		System.err.println(msg);
	}
	

}