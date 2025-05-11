package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of OutputStrategy that writes patient data to files.
 * 
 * This class saves patient health data to text files in a specified directory.
 * Each type of data (identified by its label) is written to a separate file,
 * with the filename derived from the data label. The data is appended to the
 * file in a formatted text format.
 * 
 * This implementation uses a ConcurrentHashMap to cache file paths for improved
 * performance when writing multiple records to the same file.
 */
public class FileOutputStrategy implements OutputStrategy {

    /** The base directory where output files will be stored */
    private String baseDirectory;

    /** 
     * Maps data labels to their corresponding file paths.
     * This cache improves performance by avoiding repeated path construction.
     */
    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * Constructs a new FileOutputStrategy with the specified base directory.
     * 
     * The specified directory will be created if it doesn't exist when data is
     * first written. All output files will be created within this directory,
     * with filenames based on the data labels.
     *
     * @param baseDirectory the absolute or relative path to the directory where output files will be stored
     */
    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * Outputs patient data to a file named after the label in the specified base directory.
     * 
     * This method creates the base directory if it doesn't exist, then writes the
     * patient data to a file named after the label (e.g., "ECG.txt"). Each data point
     * is written as a new line in the file, with a formatted string containing the
     * patient ID, timestamp, label, and data value.
     * 
     * The method uses a ConcurrentHashMap to cache file paths for improved performance
     * when writing multiple records with the same label.
     *
     * @param patientId the unique identifier of the patient
     * @param timestamp the time when the data was generated, in milliseconds since epoch
     * @param label the type of data being recorded (e.g., "ECG", "BloodPressure")
     * @param data the actual health data as a formatted string
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the base directory if it doesn't exist
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        
        // Get or create the file path for this label
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file, appending if the file already exists
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}