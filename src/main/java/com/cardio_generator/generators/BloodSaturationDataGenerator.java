package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generator for simulating blood oxygen saturation levels for patients.
 * 
 * This class generates realistic blood oxygen saturation values (SpO2) for patients,
 * typically measured as a percentage. Normal SpO2 values range from 95% to 100%,
 * with values below 90% potentially indicating hypoxemia (low blood oxygen).
 * 
 * The generator maintains the last saturation value for each patient and introduces
 * small random variations to simulate realistic changes over time while keeping
 * the values within physiologically plausible ranges.
 */
public class BloodSaturationDataGenerator implements PatientDataGenerator {
    /** Random number generator for creating variations in saturation values */
    private static final Random random = new Random();
    
    /** Array to store the last saturation value for each patient, indexed by patient ID */
    private int[] lastSaturationValues;

    /**
     * Constructs a new BloodSaturationDataGenerator for the specified number of patients.
     * 
     * Initializes the last saturation values for each patient with random baseline
     * values in the normal range (95-100%).
     *
     * @param patientCount the number of patients to generate data for
     */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }

    /**
     * Generates a blood oxygen saturation value for the specified patient.
     * 
     * Creates a new saturation value by applying a small random variation to the
     * patient's previous value, ensuring the result stays within realistic bounds (90-100%).
     * The generated value is then sent to the provided output strategy along with
     * the patient ID, current timestamp, and appropriate label.
     *
     * @param patientId the unique identifier of the patient to generate data for
     * @param outputStrategy the strategy to use for outputting the generated data
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
