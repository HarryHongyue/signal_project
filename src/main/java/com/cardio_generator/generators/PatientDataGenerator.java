package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Interface for generating patient health data.
 * 
 * This interface defines the contract for all patient data generators in the system.
 * Implementations of this interface are responsible for generating specific types of
 * health data (such as ECG, blood pressure, etc.) for patients and sending the data
 * to the specified output strategy.
 */
public interface PatientDataGenerator {
    
    /**
     * Generates health data for a specific patient and sends it to the output strategy.
     * 
     * This method is called periodically to simulate real-time health monitoring.
     * Each implementation should generate appropriate data for its specific health metric
     * and format it according to the requirements of the output strategy.
     *
     * @param patientId the unique identifier of the patient to generate data for
     * @param outputStrategy the strategy to use for outputting the generated data
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
