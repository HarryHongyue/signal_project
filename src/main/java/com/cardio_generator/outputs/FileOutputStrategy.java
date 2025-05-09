package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

//Change variable class name to upperCamelCase
public class FileOutputStrategy implements OutputStrategy {

    //Changed variable name to lowerCamelCase
    private String baseDirectory;

    
    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * Constructor for FileOutputStrategy.
     * @param baseDirectory the base directory where output files will be stored
     */
    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * Outputs patient data to a file named after the label in the specified base directory.
     * @param patientId the ID of the patient
     * @param timestamp the timestamp of the data
     * @param label the label/type of the data
     * @param data the actual data to write
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        //Changed variable name to lowerCamelCase
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}