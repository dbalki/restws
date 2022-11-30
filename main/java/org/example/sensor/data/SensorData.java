/**
 * 
 */
package org.example.sensor.data;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents the sensor data read from sensors.
 * @author Administrator
 * @Immutable
 */
public final class SensorData implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6188869921472613679L;

	public enum SensorType {
		TEMPERATURE, PRESSURE, VOLUME, DEFAULT
	}
	private volatile Logger log = LogManager.getLogger(SensorData.class);
	private final SensorType sensorType;
	private final Instant timeStamp;
	private final double data;
	private final String id;
	
	/**
	 * Constructs immutable Sensor Data object.
	 * @param sensorType
	 * @param timeStamp
	 * @param data
	 * @param id
	 * @throws IllegalArgumentException
	 */
	public SensorData(final SensorType sensorType, final Instant timeStamp, final double data, final String id) 
	{
		log.debug("In constructor");
		this.sensorType = sensorType;
		if (timeStamp == null) 
		{
			log.error("Time stamp can not be null");
			throw new IllegalArgumentException("Time stamp can not be null");
		}
		this.timeStamp = timeStamp;
		this.data = data;
		if (id == null || id.trim().length() == 0) 
		{
			log.error("Id can not be null or empty");
			throw new IllegalArgumentException("Id can not be null or empty");
		}
		this.id = id;
	}

	/**
	 * Get sensor type.
	 * @return sensor type
	 */
	public SensorType getSensorType() 
	{
		log.debug("Sensor type {}", sensorType);
		return sensorType;
	}

	/**
	 * Gets timestamp.
	 * @return time stamp
	 */
	public Instant getTimeStamp() 
	{
		log.debug("Time stamp {}", timeStamp);
		return timeStamp;
	}

	/**
	 * Get sensor data.
	 * @return sensor data
	 */
	public double getData() 
	{
		log.debug("Data {}", data);
		return data;
	}

	/**
	 * Get sensor id.
	 * @return sensor id
	 */
	public String getId() 
	{
		log.debug("Sensor ID {}", id);
		return id;
	}
	/**
	 * Outputs the SensorData as a String.
	 * <br>
	 * <br>
	 * The expected output contains the Timestamp truncated to Seconds.
	 * <br>
	 * The output will look like:
	 * <br>
	 * SensorData [type=TEMPERATURE, timestamp=2022-12-01T15:19:58Z, value=0.0, sensorId=unknown]
	 * 
	 * @return the SensorData as a String
	 */
	@Override
	public String toString()
	{
		return String.format("SensorData [type=%s, timestamp=%s, value=%s, sensorId=%s]", 
				sensorType, 
				timeStamp.truncatedTo(ChronoUnit.SECONDS), 
				data,
				id); 
	}

	/**
	 * The hashCode() method of the SensorData class.
	 * <br>
	 * <br>
	 * <strong>This method uses:</strong>
	 * <ul>
	 *   <li>Type</li>
	 *   <li>Timestamp</li>
	 *   <li>Value</li>
	 *   <li>Id</li>
	 * </ul>
	 * 
	 * @see java.lang.Object#hashCode()
	 * @return the hashCode value for this SensorData object
	 */
	@Override
	public int hashCode()
	{
		log.debug("building HashCode");
		return new HashCodeBuilder()
				.append(sensorType)
				.append(timeStamp)
				.append(data)
				.append(id)
				.toHashCode();
	}

	/**
	 * The equals() method of the SensorData class.
	 * <br>
	 * <br>
	 * <strong>This method uses:</strong>
	 * <ul>
	 *   <li>Type</li>
	 *   <li>Timestamp</li>
	 *   <li>Value</li>
	 *   <li>Id</li>
	 * </ul>
	 * 
	 * @see java.lang.Object#equals(Object obj)
	 * @param obj the incoming object to compare against
	 * @return true if the fields being compared are equal
	 */
	@Override
	public boolean equals(final Object obj)
	{
		log.debug("checking equals");
		if (obj instanceof SensorData)
		{
			final SensorData other = (SensorData) obj;
			return new EqualsBuilder()
					.append(sensorType, other.sensorType)
					.append(timeStamp.truncatedTo(ChronoUnit.SECONDS), 
							other.timeStamp.truncatedTo(ChronoUnit.SECONDS))
					.append(data, other.data)
					.append(id, other.id)
					.isEquals();
		}
		else
		{
			return false;
		}
	}
}
