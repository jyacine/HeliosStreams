/**
 * Helios, OpenSource Monitoring
 * Brought to you by the Helios Development Group
 *
 * Copyright 2016, Helios Development Group and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. 
 *
 */
package com.heliosapm.streams.metrics.processor;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import com.heliosapm.utils.time.SystemClock;

/**
 * <p>Title: TimestampedMetricKey</p>
 * <p>Description: A key representing the second based unix time and a metric key</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.streams.metrics.processor.TimestampedMetricKey</code></p>
 */

public class TimestampedMetricKey {
	/** The effective time of this key */
	protected final long unixTime;
	/** The count of instances of metrics with this key */
	protected long count = 0;	
	/** The metric key */
	protected final String metricKey;
	
	
	/** The string character set */
	public static final Charset UTF8 = Charset.forName("UTF8");
	
	private static final int LONG_BYTES = 16;
	
	/**
	 * Creates a new TimestampedMetricKey
	 * @param unixTime The effective time of this key
	 * @param initialCount The initial count
	 * @param metricKey The metric key
	 */
	public TimestampedMetricKey(final long unixTime, final long initialCount, final String metricKey) {
		this.unixTime = unixTime;
		this.metricKey = metricKey;
		this.count = initialCount;
	}
	
	private TimestampedMetricKey(final byte[] bytes) {
		unixTime = deserialize(bytes, 0);
		count = deserialize(bytes, 8);
		metricKey = new String(bytes, LONG_BYTES, bytes.length-LONG_BYTES, UTF8);
	}
	
	private byte[] toBytes() {
		final byte[] sbytes = metricKey.getBytes(UTF8);
		final byte[] ser = new byte[sbytes.length + LONG_BYTES];
		serialize(unixTime, count, ser);
		System.arraycopy(sbytes, 0, ser, LONG_BYTES, sbytes.length);
		return ser;
	}
	
	public static void main(String args[]) {
		log("Ser Test");
		final long now = SystemClock.unixTime();
		log("Time Width:" + ("" + now).length());
		final TimestampedMetricKey tmk = new TimestampedMetricKey(now, 7, "foo-bar");
		log("TMK1:" + tmk);
		final byte[] b = tmk.toBytes();
		final TimestampedMetricKey tmk2 = new TimestampedMetricKey(b);
		log("TMK2:" + tmk2);
		
	}

	public static void log(Object msg) {
		System.out.println(msg);
	}
	
	
	public String toString() {
		return metricKey + ":" + unixTime + ":" + count;
	}
	
	
	/**
	 * Returns the effective time of this key
	 * @return the unixTime
	 */
	public long getUnixTime() {
		return unixTime;
	}

	/**
	 * Returns the metric key
	 * @return the metricKey
	 */
	public String getMetricKey() {
		return metricKey;
	}
	
	/**
	 * Returns the metric instance count
	 * @return the metric instance count
	 */
	public long getCount() {
		return count;
	}
	
	/**
	 * Indicates if the passed timestamp is in the same seco
	 * @param mstime The ms timestamp
	 * @param count The number to increment by
	 * @param windowSize The window period in seconds
	 * @return true if in the range, false otherwise
	 */
	public boolean isSameSecondAs(final long mstime, final long count, final long windowSize) {
		final long sec = TimeUnit.MILLISECONDS.toSeconds(mstime);
		final boolean in = sec >= unixTime && sec <= (unixTime + windowSize);
		if(in) this.count += count;
		return in;
	}
	
	/**
	 * Indicates if this TimestampedMetricKey is expired
	 * @param mstime The ms timestamp
	 * @param windowSize The window period in seconds
	 * @return true if this TimestampedMetricKey is expired, false otherwise
	 */
	public boolean isExpired(final long mstime, final long windowSize) {
		final long sec = TimeUnit.MILLISECONDS.toSeconds(mstime);
		return sec >= unixTime && sec <= (unixTime + windowSize);		
	}
	
	
	public static class TimestampedMetricKeySerializer implements Serializer<TimestampedMetricKey> {
		@Override
		public void configure(final Map<String, ?> configs, final boolean isKey) {
			/* Nop Op */			
		}
		@Override
		public void close() {
			/* No Op */
		}
		@Override
		public byte[] serialize(final String topic, final TimestampedMetricKey data) {
			return data.toBytes();
		}
	}
	
	public static class TimestampedMetricKeyDeserializer implements Deserializer<TimestampedMetricKey> {
		@Override
		public void configure(final Map<String, ?> configs, final boolean isKey) {
			/* Nop Op */			
		}
		@Override
		public void close() {
			/* No Op */
		}
		@Override
		public TimestampedMetricKey deserialize(final String topic, final byte[] data) {
			if(data==null || data.length==0) return null;
			return new TimestampedMetricKey(data);
		}
	}
	
	
    private static void serialize(final long time, final long count, final byte[] into) {
    	into[0] = (byte)(time >>> 56);
    	into[1] = (byte)(time >>> 48);
    	into[2] = (byte)(time >>> 40);
    	into[3] = (byte)(time >>> 32);
    	into[4] = (byte)(time >>> 24);
    	into[5] = (byte)(time >>> 16);
    	into[6] = (byte)(time >>> 8);
    	into[7] = (byte)time;
    	into[8] = (byte)(count >>> 56);
    	into[9] = (byte)(count >>> 48);
    	into[10] = (byte)(count >>> 40);
    	into[11] = (byte)(count >>> 32);
    	into[12] = (byte)(count >>> 24);
    	into[13] = (byte)(count >>> 16);
    	into[14] = (byte)(count >>> 8);
    	into[15] = (byte)count;
    	
    }	
    
    public static long deserialize(final byte[] data, final int offset) {
        if (data.length < 8) {
            throw new SerializationException("Size of data received by LongDeserializer is " +
                    "not >= 8");
        }
        long value = 0;
        final int offsetEnd = offset + 8;
        for(int i = offset; i < offsetEnd; i++) {
            value <<= 8;
            value |= data[i] & 0xFF;        	
        }
        return value;
    }

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((metricKey == null) ? 0 : metricKey.hashCode());
		result = prime * result + (int) (unixTime ^ (unixTime >>> 32));
		return result;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TimestampedMetricKey))
			return false;
		TimestampedMetricKey other = (TimestampedMetricKey) obj;
		if (metricKey == null) {
			if (other.metricKey != null)
				return false;
		} else if (!metricKey.equals(other.metricKey))
			return false;
		if (unixTime != other.unixTime)
			return false;
		return true;
	}
    

}