package com.sms.exception;

/** Thrown when a student with the given ID does not exist. */
public class StudentNotFoundException extends Exception {

    public StudentNotFoundException(String id) {
        super("No student found with ID: " + id);
    }
}
