package com.cardio_generator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.cardio_generator.generators.AlertGenerator;

import com.cardio_generator.generators.BloodPressureDataGenerator;
import com.cardio_generator.generators.BloodSaturationDataGenerator;
import com.cardio_generator.generators.BloodLevelsDataGenerator;
import com.cardio_generator.generators.ECGDataGenerator;
import com.cardio_generator.outputs.ConsoleOutputStrategy;
import com.cardio_generator.outputs.FileOutputStrategy;
import com.cardio_generator.outputs.OutputStrategy;
import com.cardio_generator.outputs.TcpOutputStrategy;
import com.cardio_generator.outputs.WebSocketOutputStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * A simulator for generating and streaming health data for multiple patients.
 * 
 * This class serves as the main entry point for the cardio generator application.
 * It simulates various health metrics (ECG, blood pressure, blood saturation, etc.)
 * for a configurable number of patients and outputs the data using different
 * strategies (console, file, WebSocket, TCP).
 * 
 * The simulator uses a scheduled thread pool to periodically generate different
 * types of health data at various intervals, simulating real-time monitoring of
 * multiple patients simultaneously.
 */
public class HealthDataSimulator {

    /** Default number of patients to simulate data for */
    private static int patientCount = 50;
    
    /** Scheduler for executing periodic data generation tasks */
    private static ScheduledExecutorService scheduler;
    
    /** Strategy used for outputting generated data (default: console) */
    private static OutputStrategy outputStrategy = new ConsoleOutputStrategy();
    
    /** Random number generator for adding variability to scheduling */
    private static final Random random = new Random();

    /**
     * The main entry point for the health data simulator.
     * 
     * Parses command line arguments, initializes the scheduler, creates patient IDs,
     * and schedules periodic tasks to generate different types of health data.
     *
     * @param args command line arguments for configuring the simulator
     * @throws IOException if there is an error creating output directories
     */
    public static void main(String[] args) throws IOException {
        parseArguments(args);

        scheduler = Executors.newScheduledThreadPool(patientCount * 4);

        List<Integer> patientIds = initializePatientIds(patientCount);
        Collections.shuffle(patientIds); // Randomize the order of patient IDs

        scheduleTasksForPatients(patientIds);
    }

    /**
     * Parses command line arguments to configure the simulator.
     * 
     * Supported arguments:
     * <ul>
     *   <li>-h: Display help information and exit</li>
     *   <li>--patient-count &lt;count&gt;: Set the number of patients to simulate</li>
     *   <li>--output &lt;type&gt;: Set the output strategy (console, file, websocket, tcp)</li>
     * </ul>
     *
     * @param args array of command line arguments to parse
     * @throws IOException if there is an error creating output directories for file output
     */
    private static void parseArguments(String[] args) throws IOException {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h":
                    printHelp();
                    System.exit(0);
                    break;
                case "--patient-count":
                    if (i + 1 < args.length) {
                        try {
                            patientCount = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            System.err
                                    .println("Error: Invalid number of patients. Using default value: " + patientCount);
                        }
                    }
                    break;
                case "--output":
                    if (i + 1 < args.length) {
                        String outputArg = args[++i];
                        if (outputArg.equals("console")) {
                            outputStrategy = new ConsoleOutputStrategy();
                        } else if (outputArg.startsWith("file:")) {
                            String baseDirectory = outputArg.substring(5);
                            Path outputPath = Paths.get(baseDirectory);
                            if (!Files.exists(outputPath)) {
                                Files.createDirectories(outputPath);
                            }
                            outputStrategy = new FileOutputStrategy(baseDirectory);
                        } else if (outputArg.startsWith("websocket:")) {
                            try {
                                int port = Integer.parseInt(outputArg.substring(10));
                                // Initialize your WebSocket output strategy here
                                outputStrategy = new WebSocketOutputStrategy(port);
                                System.out.println("WebSocket output will be on port: " + port);
                            } catch (NumberFormatException e) {
                                System.err.println(
                                        "Invalid port for WebSocket output. Please specify a valid port number.");
                            }
                        } else if (outputArg.startsWith("tcp:")) {
                            try {
                                int port = Integer.parseInt(outputArg.substring(4));
                                // Initialize your TCP socket output strategy here
                                outputStrategy = new TcpOutputStrategy(port);
                                System.out.println("TCP socket output will be on port: " + port);
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid port for TCP output. Please specify a valid port number.");
                            }
                        } else {
                            System.err.println("Unknown output type. Using default (console).");
                        }
                    }
                    break;
                default:
                    System.err.println("Unknown option '" + args[i] + "'");
                    printHelp();
                    System.exit(1);
            }
        }
    }

    /**
     * Displays usage information and available command line options for the simulator.
     * 
     * Prints a formatted help message to standard output that explains the
     * available command line options, their syntax, and provides usage examples.
     */
    private static void printHelp() {
        System.out.println("Usage: java HealthDataSimulator [options]");
        System.out.println("Options:");
        System.out.println("  -h                       Show help and exit.");
        System.out.println(
                "  --patient-count <count>  Specify the number of patients to simulate data for (default: 50).");
        System.out.println("  --output <type>          Define the output method. Options are:");
        System.out.println("                             'console' for console output,");
        System.out.println("                             'file:<directory>' for file output,");
        System.out.println("                             'websocket:<port>' for WebSocket output,");
        System.out.println("                             'tcp:<port>' for TCP socket output.");
        System.out.println("Example:");
        System.out.println("  java HealthDataSimulator --patient-count 100 --output websocket:8080");
        System.out.println(
                "  This command simulates data for 100 patients and sends the output to WebSocket clients connected to port 8080.");
    }

    /**
     * Creates a list of sequential patient IDs based on the specified count.
     * 
     * Generates a list of integers from 1 to patientCount (inclusive) to be used
     * as unique identifiers for simulated patients.
     *
     * @param patientCount the number of patient IDs to generate
     * @return a list containing sequential patient IDs from 1 to patientCount
     */
    private static List<Integer> initializePatientIds(int patientCount) {
        List<Integer> patientIds = new ArrayList<>();
        for (int i = 1; i <= patientCount; i++) {
            patientIds.add(i);
        }
        return patientIds;
    }

    /**
     * Initializes data generators and schedules periodic tasks for each patient.
     * 
     * Creates instances of various health data generators and schedules them to run
     * periodically at different intervals for each patient ID. Each generator produces
     * different types of health data (ECG, blood pressure, etc.) that are sent to the
     * configured output strategy.
     *
     * @param patientIds list of patient IDs to schedule tasks for
     */
    private static void scheduleTasksForPatients(List<Integer> patientIds) {
        ECGDataGenerator ecgDataGenerator = new ECGDataGenerator(patientCount);
        BloodSaturationDataGenerator bloodSaturationDataGenerator = new BloodSaturationDataGenerator(patientCount);
        BloodPressureDataGenerator bloodPressureDataGenerator = new BloodPressureDataGenerator(patientCount);
        BloodLevelsDataGenerator bloodLevelsDataGenerator = new BloodLevelsDataGenerator(patientCount);
        AlertGenerator alertGenerator = new AlertGenerator(patientCount);

        for (int patientId : patientIds) {
            scheduleTask(() -> ecgDataGenerator.generate(patientId, outputStrategy), 1, TimeUnit.SECONDS);
            scheduleTask(() -> bloodSaturationDataGenerator.generate(patientId, outputStrategy), 1, TimeUnit.SECONDS);
            scheduleTask(() -> bloodPressureDataGenerator.generate(patientId, outputStrategy), 1, TimeUnit.MINUTES);
            scheduleTask(() -> bloodLevelsDataGenerator.generate(patientId, outputStrategy), 2, TimeUnit.MINUTES);
            scheduleTask(() -> alertGenerator.generate(patientId, outputStrategy), 20, TimeUnit.SECONDS);
        }
    }

    /**
     * Schedules a task to run periodically with a random initial delay.
     * 
     * Uses the scheduler to execute the specified task at fixed intervals, with
     * a random initial delay between 0 and 4 seconds to prevent all tasks from
     * executing simultaneously.
     *
     * @param task the task to schedule for periodic execution
     * @param period the time interval between successive executions
     * @param timeUnit the time unit for the period parameter
     */
    private static void scheduleTask(Runnable task, long period, TimeUnit timeUnit) {
        scheduler.scheduleAtFixedRate(task, random.nextInt(5), period, timeUnit);
    }
}
