package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Implementation of OutputStrategy that sends patient data over a TCP socket connection.
 * 
 * This class sets up a TCP server on a specified port and sends patient health data
 * to any connected client. The server accepts client connections in a separate thread
 * to avoid blocking the main application thread. Data is sent as formatted text lines
 * over the socket connection.
 * 
 * Note: This implementation handles only a single client connection at a time.
 */
public class TcpOutputStrategy implements OutputStrategy {

    /** Server socket that listens for client connections */
    private ServerSocket serverSocket;
    
    /** Socket for the currently connected client */
    private Socket clientSocket;
    
    /** Writer for sending data to the connected client */
    private PrintWriter out;

    /**
     * Constructs a new TcpOutputStrategy that listens on the specified port.
     * 
     * Initializes a TCP server socket on the given port and starts a background thread
     * to accept a client connection. When a client connects, a PrintWriter is created
     * for sending data to that client.
     *
     * @param port the TCP port number to listen on (1-65535)
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends patient data to the connected TCP client.
     * 
     * Formats the patient data as a comma-separated string and sends it as a line
     * of text to the connected client. If no client is currently connected (out is null),
     * the data is silently discarded.
     *
     * @param patientId the unique identifier of the patient
     * @param timestamp the time when the data was generated, in milliseconds since epoch
     * @param label the type of data being recorded (e.g., "ECG", "BloodPressure")
     * @param data the actual health data as a formatted string
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
