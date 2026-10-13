package com.sms.repository;

import com.sms.model.Student;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores students in a CSV file, one record per line.
 */
public class FileStudentRepository implements StudentRepository {

    private static final String HEADER = "id,name,age,email,course,marks";

    private final Path filePath;

    public FileStudentRepository(String fileName) {
        this.filePath = Paths.get(fileName);
    }

    @Override
    public List<Student> loadAll() throws IOException {
        List<Student> students = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return students; // first run: nothing saved yet
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty() || line.equals(HEADER)) {
                    continue;
                }
                try {
                    students.add(Student.fromCsv(line));
                } catch (IllegalArgumentException e) {
                    System.out.println("Skipping corrupt line " + lineNumber + ": " + e.getMessage());
                }
            }
        }
        return students;
    }

    @Override
    public void saveAll(List<Student> students) throws IOException {
        Path parent = filePath.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        // Write to a temp file first, then replace, so a crash mid-write
        // never leaves a half-written data file behind.
        Path tempFile = Paths.get(filePath + ".tmp");
        try (BufferedWriter writer = Files.newBufferedWriter(tempFile, StandardCharsets.UTF_8)) {
            writer.write(HEADER);
            writer.newLine();
            for (Student student : students) {
                writer.write(student.toCsv());
                writer.newLine();
            }
        }
        Files.move(tempFile, filePath, StandardCopyOption.REPLACE_EXISTING);
    }
}
