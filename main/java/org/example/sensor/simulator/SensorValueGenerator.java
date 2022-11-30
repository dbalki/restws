package org.example.sensor.simulator;

import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sensor.data.SensorData.SensorType;

/**
 * This is a SensorValueGenerator.
 * <br>
 * <br>
 * This will simulate a IOT Sensor value, by:
 * <ul>
 *   <li>Generating a double value within a specific range for the SensorType</li>
 *   <li>Varying the current value by &plusmn; 5%</li>
 *   <li>The <em>Normal</em> ranges are:
 *      <ul>
 *        <li><strong>SensorType.TEMPERATURE</strong>: 0 &hArr; 250</li>
 *        <li><strong>SensorType.VOLUME</strong>: 0 &hArr; 1500</li>
 *        <li><strong>SensorType.PRESSURE</strong>: 0 &hArr; 100</li>
 *      </ul>
 *   </li>
 * </ul>
 * <br>
 * <br>
 * 
 * @Stateless
 * 
 * @author Jonathan Earl
 * @since 1.0
 * @version 1.0
 * 
 */
public final class SensorValueGenerator 
	implements SensorSimulator
{
	private static final Logger LOG = LogManager.getLogger();
	
	/**
	 * The default constructor
	 * <br>
	 * This constructor is empty as this is a <strong>Stateless</strong> class.
	 */
	public SensorValueGenerator() 
	{	}
	
	/**
	 * This will generate an initial value for the Sensor.
	 * <br>
	 * <br>
	 * The value will be within these ranges (min value is inclusive, max value is exclusive):
	 * <ul>
     *   <li><strong>SensorType.TEMPERATURE</strong>: 0 &hArr; 250</li>
     *   <li><strong>SensorType.VOLUME</strong>: 0 &hArr; 1500</li>
     *   <li><strong>SensorType.PRESSURE</strong>: 0 &hArr; 100</li>
     * </ul>
     *      
	 * @param type the SensorType to initialize
	 * @return the initialize value for the specific SensorType
	 */
	public double initialValue(final SensorType type)
	{
		LOG.debug("initializing " + type.toString());
		double min = 0.0;
		double max = 100.0;
		switch (type)
		{
			case PRESSURE:
				max = 100.0;
				break;
			case TEMPERATURE:
				max = 250.0;
				break;
			case VOLUME:
				max = 1500.0;
				break;
			default:
				break;
		}
		double value = ThreadLocalRandom.current().nextDouble(min, max);
		LOG.trace("Returning: " + value);
		return value;
	}
	
	/**
	 * This will generate a new value for the Sensor.
	 * <br>
	 * <br>
	 * The value returned will be the current value &plusmn; 5%.
     *      
	 * @param type the SensorType to initialize
	 * @param currentValue the current sensor value
	 * @return the current value for the specific SensorType by &plusmn; 5%
	 */
	public double getNewValue(final SensorType type, final double currentValue)
	{
		LOG.debug("getting a new value " + type.toString() + " current value: " + currentValue);
		final double fivePercent = currentValue * 0.05;
		
		double value = ThreadLocalRandom.current()
				.nextDouble(currentValue - fivePercent, currentValue + fivePercent);
		LOG.trace("Returning: " + value);
		return value;
	}
	
}
