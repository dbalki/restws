package org.example.sensor;

/*
 * This is free and unencumbered software released into the public domain.
 * Anyone is free to copy, modify, publish, use, compile, sell, or distribute this software, 
 * either in source code form or as a compiled binary, for any purpose, commercial or 
 * non-commercial, and by any means.
 * 
 * In jurisdictions that recognize copyright laws, the author or authors of this 
 * software dedicate any and all copyright interest in the software to the public domain. 
 * We make this dedication for the benefit of the public at large and to the detriment of 
 * our heirs and successors. We intend this dedication to be an overt act of relinquishment in 
 * perpetuity of all present and future rights to this software under copyright law.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES 
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * For more information, please refer to: https://unlicense.org/
*/

/**
 * This is the main Sensor interface.
 * <br>
 * <br>
 * This interface will allow a SensorController to control the activity of
 * a Sensor.<br>
 * <br>
 * A Sensor runs as a <em>Push</em> object, it will send it's data every 3 seconds
 * rather than wait for a pull request.
 * 
 * @author Jonathan Earl
 * @since 1.0
 * @version 1.0
 *
 */
public interface Sensor
{
	/**
	 * Provides the current SensorStatus for this Sensor.
	 * 
	 * @return the current SensorStatus for this Sensor.
	 * @throws InterruptedException if the Status field cannot be locked
	 */
	SensorStatus getStatus() throws InterruptedException;
	
	/**
	 * Sets the SensorStatus.
	 * <br>
	 * <br>
	 * This is a convenience method to set the SensorStatus.
	 * 
	 * @param status the Sensor Status to set
	 * @throws InterruptedException if the Status field cannot be locked
	 */
	void setStatus(SensorStatus status) throws InterruptedException;
	
	/**
	 * Sets the SensorReporter for this Sensor.
	 * <br>
	 * <br>
	 * This is the target object for the Sensor data push.
	 * 
	 * @param recorder to set
	 * @throws InterruptedException if the Status field cannot be locked
	 * @throws IllegalArgumentException if the SensorRecorder is null 
	 * @throws IllegalStateException is the Sensor is not INITIALIZED
	 */
	void setReporter(SensorReporter recorder) throws InterruptedException;
		
	/**
	 * Starts pushing the SensorData to the SensorReporter.
	 * <br>
	 * <br>
	 * This causes the SensorStatus to change to RUNNING. 
	 * 
	 * @throws InterruptedException if the Status field cannot be locked
	 * @throws IllegalArgumentException if the SensorReporter is not set 
	 * @throws IllegalStateException if the Sensor is not INITIALIZED or STOPPED
	 */
	void startReporting() throws InterruptedException;
	
	/**
	 * Stops pushing SensorData to the SensorReporter.
	 * <br>
	 * <br>
	 * This causes the SensorStatus to change to STOPPED.
	 * @throws InterruptedException if the Status field cannot be locked
	 *  
	 * @throws IllegalStateException is the Sensor is not RUNNING
	 */
	void stopReporting() throws InterruptedException;
	
	/**
	 * Stops the Sensor from reporting, and shuts the Sensor down.
	 * <br>
	 * <br>
	 * This causes the SensorStatus to change to SHUTDOWN.
	 * @throws InterruptedException if the Status field cannot be locked
	 * 
	 * @throws IllegalStateException if the SensorRecorder is not STOPPED 
	 * or is already SHUTDOWN
	 */
	void shutdown() throws InterruptedException;
	
	/**
	 * pushes the current sensor value to the reporter.
	 * @throws InterruptedException 
	 */
	void pushValue() throws InterruptedException;

}
