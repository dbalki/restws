/**
 * 
 */
package org.example.sensor;

import org.example.sensor.data.SensorData;
import org.example.sensor.data.SensorSummary;

/**
 * Sensor controller receives sensor data and updates the summary queue.
 * @author Administrator
 * @ThreadSafe
 */
public class SensorController implements SensorReporter {
	
	private final SensorSummary summary = new SensorSummary();
	
	public SensorController() {
	}

	@Override
	public void pushSensorData(SensorData data) {
		synchronized(summary) {
			summary.addSensorData(data);
		}
		
	}

}
