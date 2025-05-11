package com.cardio_generator.outputs;

/**
 * Interface defining a strategy for outputting patient health data.
 * 
 * This interface follows the Strategy design pattern and provides a common contract
 * for different output mechanisms (console, file, network, etc.). Implementations
 * of this interface determine how and where the generated health data is sent or stored.
 */
public interface OutputStrategy {
    
    /**
     * Outputs patient health data using the specific strategy implementation.
     * 
     * This method is called by data generators when new health data is available.
     * Implementations should handle the data according to their specific output mechanism,
     * such as printing to console, writing to a file, or sending over a network connection.
     *
     * @param patientId the unique identifier of the patient the data belongs to
     * @param timestamp the time when the data was generated, in milliseconds since epoch
     * @param label a descriptive label for the type of data (e.g., "ECG", "BloodPressure")
     * @param data the actual health data as a formatted string
     */
    void output(int patientId, long timestamp, String label, String data);
}
