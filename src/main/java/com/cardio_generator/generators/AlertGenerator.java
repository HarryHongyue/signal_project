package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generator for simulating patient alert events in a healthcare monitoring system.
 * 
 * This class generates alert events that might occur in a clinical setting, such as
 * when a patient presses a call button or when an automatic alert is triggered by
 * abnormal vital signs. The generator simulates both the triggering of new alerts
 * and the resolution of existing alerts based on probability distributions.
 * 
 * The class maintains the current alert state for each patient and generates
 * state transitions (triggered or resolved) according to configurable probabilities.
 */
public class AlertGenerator implements PatientDataGenerator {

    /** Random number generator for simulating alert probabilities */
    public static final Random randomGenerator = new Random();
    
    /** 
     * Array to track the current alert state for each patient, indexed by patient ID.
     * false = resolved (no active alert), true = triggered (active alert)
     */
    private boolean[] alertStates; // Changed variable name to follow camelCase convention

    /**
     * Constructs a new AlertGenerator for the specified number of patients.
     * 
     * Initializes the alert states array with all patients having no active alerts.
     *
     * @param patientCount the number of patients to generate alerts for
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1]; // Changed variable name to follow camelCase convention
    }

    /**
     * Generates alert events for the specified patient based on probabilistic models.
     * 
     * This method simulates two scenarios:
     * 1. If the patient already has an active alert, there's a 90% chance it will be resolved.
     * 2. If the patient has no active alert, a new alert may be triggered based on a
     *    Poisson process model with a configurable rate parameter (lambda).
     * 
     * The method sends the appropriate alert status ("triggered" or "resolved") to the
     * output strategy along with the patient ID and current timestamp.
     *
     * @param patientId the unique identifier of the patient to generate data for
     * @param outputStrategy the strategy to use for outputting the generated alert events
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) { // Changed variable name to follow camelCase convention
                if (randomGenerator.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false; // Changed variable name to follow camelCase convention
                    // Output the alert resolution event
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = randomGenerator.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true; // Changed variable name to follow camelCase convention
                    // Output the alert triggering event
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
