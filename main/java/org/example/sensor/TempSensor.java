/**
 * 
 */
package org.example.sensor;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sensor.data.SensorData;
import org.example.sensor.data.SensorData.SensorType;
import org.example.sensor.simulator.SensorValueGenerator;

/**
 * @author Administrator
 * Temperature sensor that senses the temperature and push the data to reporter.
 * @ThreadSafe
 */
public class TempSensor implements Sensor {
	
	private SensorStatus status;
	private SensorReporter recorder;
	private SensorValueGenerator simulator;
	private double currentValue;
	private String id;
	private ReentrantLock lock = new ReentrantLock();
	private volatile Logger log = LogManager.getLogger();
	/**
	 * Construct new temperature sensor.
	 */
	public TempSensor(String id) {
		log.debug("In constructor");
		this.id = id;
		status = SensorStatus.INITIALIZED;
		simulator = new SensorValueGenerator();
		currentValue = simulator.initialValue(SensorType.TEMPERATURE);
	}

	@Override
	public void pushValue() throws InterruptedException {
		log.debug("Push value");
		boolean lockAcquired = lock.tryLock(1, TimeUnit.SECONDS);
		if(lockAcquired) {
			try {
				double newValue = simulator.getNewValue(SensorType.TEMPERATURE, currentValue);
				SensorData data = new SensorData(SensorType.TEMPERATURE, Instant.now(),
						newValue, id);
				currentValue = newValue;
				recorder.pushSensorData(data);
			} finally {
				lock.unlock();
			}
		} else {
			log.error("Could not acquire lock on status");
			throw new InterruptedException("Could not acquire lock on status...");
		}
	}

	@Override
	public SensorStatus getStatus() throws InterruptedException {
		log.debug("Get status");
		boolean lockAcquired = lock.tryLock(1, TimeUnit.SECONDS);
		if(lockAcquired) {
			try {
				return status;
			} finally {
				lock.unlock();
			}
		} else {
			log.error("Could not acquire lock on status");
			throw new InterruptedException("Could not acquire lock on status...");
		}
	}

	@Override
	public void setStatus(SensorStatus status) throws InterruptedException {
		boolean lockAcquired = lock.tryLock(1, TimeUnit.SECONDS);
		if(lockAcquired) {
			try {
				this.status = status;
			} finally {
				lock.unlock();
			}
		} else {
			log.error("Could not acquire lock on status");
			throw new InterruptedException("Could not acquire lock on status...");
		}
	}

	@Override
	public void setReporter(SensorReporter recorder) throws InterruptedException {
		boolean lockAcquired = lock.tryLock(1, TimeUnit.SECONDS);
		if(lockAcquired) {
			try {
				if (recorder == null) {
					log.error("Sensor reporter is null");
					throw new IllegalArgumentException("Sensor reporter is null");
				}
				if(status != SensorStatus.INITIALIZED) {
					log.error("Sensor not initialized");
					throw new IllegalStateException("Sensor not initialized");
				}
				this.recorder = recorder;
				log.debug("recorder is set");
			} finally {
				lock.unlock();
			}
		} else {
			log.error("Could not acquire lock on status");
			throw new InterruptedException("Could not acquire lock on status...");
		}
	}

	@Override
	public void startReporting() throws InterruptedException {
		boolean lockAcquired = lock.tryLock(1, TimeUnit.SECONDS);
		if(lockAcquired) {
			try {
				if (recorder == null) {
					log.error("Sensor reporter can not be null");
					throw new IllegalArgumentException("Sensor reporter can not be null");
				}
				if(status != SensorStatus.INITIALIZED || status == SensorStatus.STOPPED) {
					log.error("Sensor not initialized");
					throw new IllegalStateException("Sensor not initialized or stopped");
				}
				this.status = SensorStatus.RUNNING;
				log.debug("start reporting");
			} finally {
				lock.unlock();
			}
		} else {
			log.error("Could not acquire lock on status");
			throw new InterruptedException("Could not acquire lock on status...");
		}
	}

	@Override
	public void stopReporting() throws InterruptedException {
		boolean lockAcquired = lock.tryLock(1, TimeUnit.SECONDS);
		if(lockAcquired) {
			try {
				if(status != SensorStatus.RUNNING) {
					log.error("Sensor not running");
					throw new IllegalStateException("Sensor not running");
				}
				status = SensorStatus.STOPPED;
				log.debug("stop reporting");
			} finally {
				lock.unlock();
			}
		} else {
			log.error("Could not acquire lock on status");
			throw new InterruptedException("Could not acquire lock on status...");
		}
	}

	@Override
	public void shutdown() throws InterruptedException {
		boolean lockAcquired = lock.tryLock(1, TimeUnit.SECONDS);
		if(lockAcquired) {
			try {
				if(status != SensorStatus.STOPPED || status == SensorStatus.SHUTDOWN) {
					log.error("Sensor not stopped or already shutdown");
					throw new IllegalStateException("Sensor not stopped or already shutdown");
				}
				status = SensorStatus.SHUTDOWN;
				log.debug("Sensor is shutdown");
			} finally {
				lock.unlock();
			}
		} else {
			log.error("Could not acquire lock on status");
			throw new InterruptedException("Could not acquire lock on status...");
		}
	}

}
