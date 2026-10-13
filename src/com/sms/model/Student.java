package com.sms.model;

import java.util.Objects;

/**
 * A student record. Inherits common fields from {@link Person}.
 */
public class Student extends Person {

    private static final String DELIMITER = ",";

    private final String id;
    private String course;
    private double marks;

    public Student(String id, String name, int age, String email, String course, double marks) {
        super(name, age, email);
        this.id = id;
        this.course = course;
        this.marks = marks;
    }

    public String getId() {
        return id;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public double getMarks() {
        return marks;
    }

    public void setMarks(double marks) {
        this.marks = marks;
    }

    /** Derives a letter grade from the marks (0-100). */
    public String getGrade() {
        if (marks >= 90) return "A";
        if (marks >= 75) return "B";
        if (marks >= 60) return "C";
        if (marks >= 40) return "D";
        return "F";
    }

    @Override
    public String getRole() {
        return "Student";
    }

    /** Converts this student into one CSV line for file storage. */
    public String toCsv() {
        return String.join(DELIMITER,
                id, getName(), String.valueOf(getAge()), getEmail(), course, String.valueOf(marks));
    }

    /** Builds a student from one CSV line. Throws IllegalArgumentException on bad data. */
    public static Student fromCsv(String line) {
        String[] parts = line.split(DELIMITER, -1);
        if (parts.length != 6) {
            throw new IllegalArgumentException("Expected 6 fields but found " + parts.length);
        }
        try {
            return new Student(
                    parts[0].trim(),
                    parts[1].trim(),
                    Integer.parseInt(parts[2].trim()),
                    parts[3].trim(),
                    parts[4].trim(),
                    Double.parseDouble(parts[5].trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number in record: " + line, e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student other = (Student) o;
        return id.equalsIgnoreCase(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id.toUpperCase());
    }

    @Override
    public String toString() {
        return String.format("| %-8s | %-20s | %3d | %-25s | %-12s | %6.2f | %-5s |",
                id, getName(), getAge(), getEmail(), course, marks, getGrade());
    }
}
