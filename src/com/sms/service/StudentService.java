package com.sms.service;

import com.sms.exception.DuplicateStudentException;
import com.sms.exception.StudentNotFoundException;
import com.sms.model.Student;
import com.sms.repository.StudentRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Business logic for managing students.
 * Records are kept in a LinkedHashMap (fast lookup by ID, keeps insertion order)
 * and written to the repository after every change.
 */
public class StudentService {

    private final StudentRepository repository;
    private final Map<String, Student> students = new LinkedHashMap<>();

    public StudentService(StudentRepository repository) throws IOException {
        this.repository = repository;
        for (Student s : repository.loadAll()) {
            students.put(key(s.getId()), s);
        }
    }

    public void addStudent(Student student) throws DuplicateStudentException, IOException {
        String key = key(student.getId());
        if (students.containsKey(key)) {
            throw new DuplicateStudentException(student.getId());
        }
        students.put(key, student);
        persist();
    }

    public boolean exists(String id) {
        return students.containsKey(key(id));
    }

    public Student findById(String id) throws StudentNotFoundException {
        Student student = students.get(key(id));
        if (student == null) {
            throw new StudentNotFoundException(id);
        }
        return student;
    }

    /** Saves changes made to a student obtained from {@link #findById}. */
    public void updateStudent(Student student) throws StudentNotFoundException, IOException {
        if (!students.containsKey(key(student.getId()))) {
            throw new StudentNotFoundException(student.getId());
        }
        students.put(key(student.getId()), student);
        persist();
    }

    public Student deleteStudent(String id) throws StudentNotFoundException, IOException {
        Student removed = students.remove(key(id));
        if (removed == null) {
            throw new StudentNotFoundException(id);
        }
        persist();
        return removed;
    }

    public List<Student> getAllStudents() {
        return Collections.unmodifiableList(new ArrayList<>(students.values()));
    }

    /** Case-insensitive partial match on name. */
    public List<Student> searchByName(String text) {
        String query = text.toLowerCase();
        List<Student> result = new ArrayList<>();
        for (Student s : students.values()) {
            if (s.getName().toLowerCase().contains(query)) {
                result.add(s);
            }
        }
        return result;
    }

    public List<Student> getSorted(Comparator<Student> comparator) {
        List<Student> sorted = new ArrayList<>(students.values());
        sorted.sort(comparator);
        return sorted;
    }

    public int count() {
        return students.size();
    }

    public double averageMarks() {
        if (students.isEmpty()) return 0;
        double total = 0;
        for (Student s : students.values()) {
            total += s.getMarks();
        }
        return total / students.size();
    }

    public Student topStudent() {
        return students.isEmpty() ? null
                : Collections.max(students.values(), Comparator.comparingDouble(Student::getMarks));
    }

    private void persist() throws IOException {
        repository.saveAll(new ArrayList<>(students.values()));
    }

    /** IDs are case-insensitive: "s101" and "S101" are the same student. */
    private static String key(String id) {
        return id.trim().toUpperCase();
    }
}
