package com.ra2.users.logging;

import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CustomLogging {

    private static final String LOG_DIR = "logs"; // Carpeta de logs
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Método para log INFO
    public void logInfo(String className, String methodName, String infoMsg) {
        String timestamp = LocalDateTime.now().format(DATETIME_FORMATTER);
        String logEntry = String.format("[%s] INFO - %s - %s - %s", timestamp, className, methodName, infoMsg);
        writeToFile(logEntry);
        System.out.println(logEntry); // Opcional: para ver en consola
    }

    // Método para log ERROR
    public void logError(String className, String methodName, String errorMsg, Exception e) {
        String timestamp = LocalDateTime.now().format(DATETIME_FORMATTER);
        String logEntry = String.format("[%s] ERROR - %s - %s - %s", timestamp, className, methodName, errorMsg);

        if (e != null) {
            logEntry += " - Exception: " + e.getMessage();
        }

        writeToFile(logEntry);
        System.out.println(logEntry); // Opcional: para ver en consola
    }

    // Método privado para escribir en el archivo del día
    private void writeToFile(String message) {
        String fileName = "aplicacio-" + LocalDate.now().format(DATE_FORMATTER) + ".log";
        Path logPath = Paths.get(LOG_DIR, fileName);

        try {
            // Crear carpeta logs si no existe
            if (Files.notExists(logPath.getParent())) {
                Files.createDirectories(logPath.getParent());
            }

            // Escribir mensaje al final del archivo
            try (BufferedWriter writer = Files.newBufferedWriter(logPath,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND)) {
                writer.write(message);
                writer.newLine();
            }

        } catch (IOException ioException) {
            System.err.println("ERROR escrivint al fitxer de log: " + ioException.getMessage());
        }
    }
}
