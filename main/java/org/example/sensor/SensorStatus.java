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

import java.util.Locale;

/**
 * This is the listing of Sensor Status values.
 * <br>
 * <br>
 * The SensorStaus will reflect the current activity of a Sensor.
 * 
 * @author Jonathan Earl
 * @since 1.0
 * @version 1.0
 *
 */
public enum SensorStatus 
{
	/**
	 * An INITIALIZED Sensor has been created, and is not yet running.
	 */
	INITIALIZED, 
	
	/**
	 * A RUNNING Sensor is is sending the SensorData to a SensorController.
	 */
	RUNNING, 
	
	/**
	 * a STOPPED Sensor is no longer RUNNING, it can be restarted.
	 */
	STOPPED, 
	
	/**
	 * a SHUTDOWN Sensor is no longer RECORDING nor RUNNING, the Sensor is off-line.
	 * <br>
	 * <br>
	 * A SHUTDOWN Status is terminal, the Sensor cannot be restarted.
	 */
	SHUTDOWN, 
	
	/**
	 * an INVALID SensorStatus indicates the Sensor is in an error condition.
	 */
	INVALID;

	/**
	 * A convenience method to convert a String to a SensorStatus.
	 *
	 * @param str
	 *            the String to convert to a SensorStatus
	 * @return SensorStatus the SensorStatus as converted from the String
	 */
	public static SensorStatus toStatus(final String str) 
	{
		try 
		{
			return valueOf(str.toUpperCase(Locale.US));
		} 
		catch (Exception ex) 
		{
			return INVALID;

		}
	}
}
