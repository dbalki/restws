/**
 * 
 */
package org.example.sensor.data;

import java.io.Serializable;
import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Keeps track of sensor data produced by various sensors.
 * @author Administrator
 * @MultiThreaded
 */
public final class SensorSummary implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4790847208500685281L;
	private volatile Logger log = LogManager.getLogger(SensorSummary.class);
	private AtomicInteger numberOfSensors;
	private Queue<SensorData> sensorDataQueue;
	private Instant timeStamp;
	
	/**
	 * The constructor for SensorSumary.
	 * <br>
	 * <br>
	 * the default values are:
	 * <ul>
	 *   <li>numberOfSensors = 0</li>
	 *   <li>sensorDataCollection = an empty Queue</li>
	 *   <li>summaryTimestamp = current time</li>
	 * </ul>
	 */
	public SensorSummary()
	{
		log.debug("using constructor");
		numberOfSensors = new AtomicInteger(0);
		sensorDataQueue = new ConcurrentLinkedQueue<SensorData>();
		timeStamp = Instant.now();
	}
	
	/**
	 * Get number of sensors.
	 * @return number of sensors in the queue
	 */
	public int getNumberOfSensors() 
	{
		log.debug("Number of sensors {}", numberOfSensors.get());
		return numberOfSensors.get();
	}

	/**
	 * Sets the number of sensors.
	 * @param numberOfSensors
	 * @throws IllegalArgumentException
	 * This method is not thread-safe and can lead to data race. So, it should be removed to enable multi-threaded execution.
	 */
	public void setNumberOfSensors(final int numberOfSensors) 
	{
		if (numberOfSensors < 0) 
		{
			log.error("Number of sensors can not be negative {}", numberOfSensors);
			throw new IllegalArgumentException("Number of sensors can not be negative");
		}
		this.numberOfSensors.set(numberOfSensors);
	}

	/**
	 * Get sensor data queue.
	 * @return sensor data queue
	 * 
	 */
	public Queue<SensorData> getSensorDataQueue() 
	{
		log.debug("Sensor data queue size {}", sensorDataQueue.size());
		return sensorDataQueue;
	}
	
	/**
	 * Add sensor data to the queue.
	 * @param sensorData
	 */
	public void addSensorData(final SensorData sensorData) 
	{
		log.debug("Adding sensor data {}", sensorData);
		synchronized(sensorDataQueue) {
			sensorDataQueue.add(sensorData);
			numberOfSensors.getAndAdd(1);
		}
	}

	/**
	 * Get timestamp. 
	 * @return time stamp
	 */
	public Instant getTimeStamp() 
	{
		log.debug("Time stamp {}", timeStamp);
		return timeStamp;
	}
	

	/**
	 * Sets time stamp.
	 * @param timeStamp
	 * @throws IllegalArgumentException
	 * This method is not thread-safe and can lead to data race. So, it should be removed to enable multi-threaded execution.
	 */
	public void setTimeStamp(final Instant timeStamp) 
	{
		if (timeStamp == null) 
		{
			log.error("Time stamp can not be null");
			throw new IllegalArgumentException("Time stamp can not be null");
		}
		this.timeStamp = timeStamp;
	}
	
}
