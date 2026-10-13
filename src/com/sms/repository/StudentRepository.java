package com.sms.repository;

import com.sms.model.Student;

import java.io.IOException;
import java.util.List;

/**
 * Storage abstraction. The service depends on this interface,
 * so the storage format can change without touching business logic.
 */
public interface StudentRepository {

    List<Student> loadAll() throws IOException;

    void saveAll(List<Student> students) throws IOException;
}
