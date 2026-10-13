package com.sms;

import com.sms.exception.DuplicateStudentException;
import com.sms.exception.StudentNotFoundException;
import com.sms.model.Student;
import com.sms.repository.FileStudentRepository;
import com.sms.service.StudentService;
import com.sms.util.InputHelper;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Console entry point: shows the menu and routes choices to the service.
 */
public class Main {

    private static final String DATA_FILE = "data/students.csv";
    private static final String LINE =
            "+----------+----------------------+-----+---------------------------+--------------+--------+-------+";

    private final StudentService service;
    private final InputHelper input;

    public Main(StudentService service, InputHelper input) {
        this.service = service;
        this.input = input;
    }

    public static void main(String[] args) {
        StudentService service;
        try {
            service = new StudentService(new FileStudentRepository(DATA_FILE));
        } catch (IOException e) {
            System.out.println("Could not read data file " + DATA_FILE + ": " + e.getMessage());
            return;
        }

        try (Scanner scanner = new Scanner(System.in)) {
            new Main(service, new InputHelper(scanner)).run();
        }
    }

    private void run() {
        System.out.println("==============================================");
        System.out.println("        STUDENT MANAGEMENT SYSTEM");
        System.out.println("==============================================");
        System.out.println("Loaded " + service.count() + " student(s) from " + DATA_FILE);

        while (true) {
            printMenu();
            int choice = input.readInt("Enter your choice: ", 0, 8);
            System.out.println();

            try {
                switch (choice) {
                    case 1: addStudent(); break;
                    case 2: viewAllStudents(); break;
                    case 3: searchById(); break;
                    case 4: searchByName(); break;
                    case 5: updateStudent(); break;
                    case 6: deleteStudent(); break;
                    case 7: sortStudents(); break;
                    case 8: showStatistics(); break;
                    case 0:
                        System.out.println("All changes are saved. Goodbye!");
                        return;
                    default:
                        break;
                }
            } catch (StudentNotFoundException | DuplicateStudentException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error saving to file: " + e.getMessage());
            }
            input.pause();
        }
    }

    private void printMenu() {
        System.out.println("\n---------------- MAIN MENU ----------------");
        System.out.println(" 1. Add Student");
        System.out.println(" 2. View All Students");
        System.out.println(" 3. Search Student by ID");
        System.out.println(" 4. Search Students by Name");
        System.out.println(" 5. Update Student");
        System.out.println(" 6. Delete Student");
        System.out.println(" 7. Sort Students");
        System.out.println(" 8. Statistics");
        System.out.println(" 0. Exit");
        System.out.println("-------------------------------------------");
    }

    private void addStudent() throws DuplicateStudentException, IOException {
        System.out.println("--- Add New Student ---");
        String id = input.readText("Student ID (e.g. S101): ").toUpperCase();
        if (service.exists(id)) {
            throw new DuplicateStudentException(id);
        }
        String name = input.readText("Name: ");
        int age = input.readInt("Age: ", 3, 100);
        String email = input.readEmail("Email: ");
        String course = input.readText("Course: ");
        double marks = input.readDouble("Marks (0-100): ", 0, 100);

        service.addStudent(new Student(id, name, age, email, course, marks));
        System.out.println("Student added successfully.");
    }

    private void viewAllStudents() {
        System.out.println("--- All Students ---");
        printTable(service.getAllStudents());
    }

    private void searchById() throws StudentNotFoundException {
        String id = input.readText("Enter Student ID: ");
        Student student = service.findById(id);
        printTable(Collections.singletonList(student));
    }

    private void searchByName() {
        String name = input.readText("Enter name (or part of it): ");
        List<Student> results = service.searchByName(name);
        System.out.println(results.size() + " match(es) found.");
        printTable(results);
    }

    private void updateStudent() throws StudentNotFoundException, IOException {
        String id = input.readText("Enter ID of student to update: ");
        Student student = service.findById(id);

        System.out.println("Press Enter to keep the current value.");
        student.setName(input.readTextOrKeep("Name", student.getName()));
        student.setAge(input.readIntOrKeep("Age", student.getAge(), 3, 100));
        student.setEmail(input.readEmailOrKeep("Email", student.getEmail()));
        student.setCourse(input.readTextOrKeep("Course", student.getCourse()));
        student.setMarks(input.readDoubleOrKeep("Marks", student.getMarks(), 0, 100));

        service.updateStudent(student);
        System.out.println("Student updated successfully.");
    }

    private void deleteStudent() throws StudentNotFoundException, IOException {
        String id = input.readText("Enter ID of student to delete: ");
        Student student = service.findById(id);
        printTable(Collections.singletonList(student));

        if (input.confirm("Are you sure you want to delete this student?")) {
            service.deleteStudent(id);
            System.out.println("Student deleted successfully.");
        } else {
            System.out.println("Delete cancelled.");
        }
    }

    private void sortStudents() {
        System.out.println("Sort by:");
        System.out.println(" 1. Name (A-Z)");
        System.out.println(" 2. Marks (highest first)");
        System.out.println(" 3. Age (youngest first)");
        System.out.println(" 4. ID");
        int option = input.readInt("Choice: ", 1, 4);

        Comparator<Student> comparator;
        switch (option) {
            case 1: comparator = Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER); break;
            case 2: comparator = Comparator.comparingDouble(Student::getMarks).reversed(); break;
            case 3: comparator = Comparator.comparingInt(Student::getAge); break;
            default: comparator = Comparator.comparing(Student::getId); break;
        }
        printTable(service.getSorted(comparator));
    }

    private void showStatistics() {
        System.out.println("--- Statistics ---");
        System.out.println("Total students : " + service.count());
        if (service.count() == 0) {
            return;
        }
        System.out.printf("Average marks  : %.2f%n", service.averageMarks());
        Student top = service.topStudent();
        System.out.printf("Top student    : %s (%s) - %.2f%n", top.getName(), top.getId(), top.getMarks());
    }

    private void printTable(List<Student> list) {
        if (list.isEmpty()) {
            System.out.println("No students to display.");
            return;
        }
        System.out.println(LINE);
        System.out.printf("| %-8s | %-20s | %3s | %-25s | %-12s | %6s | %-5s |%n",
                "ID", "Name", "Age", "Email", "Course", "Marks", "Grade");
        System.out.println(LINE);
        for (Student s : list) {
            System.out.println(s);
        }
        System.out.println(LINE);
    }
}
