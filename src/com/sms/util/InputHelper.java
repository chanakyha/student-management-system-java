package com.sms.util;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Reads and validates console input. Keeps prompting until the input is valid.
 */
public class InputHelper {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private final Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    /** Reads a non-empty line. Commas are rejected because records are stored as CSV. */
    public String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = nextLine();
            if (value.isEmpty()) {
                System.out.println("  Value cannot be empty.");
            } else if (value.contains(",")) {
                System.out.println("  Commas are not allowed.");
            } else {
                return value;
            }
        }
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            String value = readText(prompt);
            Integer number = parseInt(value, min, max);
            if (number != null) {
                return number;
            }
        }
    }

    public double readDouble(String prompt, double min, double max) {
        while (true) {
            String value = readText(prompt);
            Double number = parseDouble(value, min, max);
            if (number != null) {
                return number;
            }
        }
    }

    public String readEmail(String prompt) {
        while (true) {
            String value = readText(prompt);
            if (isValidEmail(value)) {
                return value;
            }
        }
    }

    // ---- "Press Enter to keep current value" variants, used when updating ----

    public String readTextOrKeep(String prompt, String current) {
        while (true) {
            System.out.print(prompt + " [" + current + "]: ");
            String value = nextLine();
            if (value.isEmpty()) return current;
            if (!value.contains(",")) return value;
            System.out.println("  Commas are not allowed.");
        }
    }

    public int readIntOrKeep(String prompt, int current, int min, int max) {
        while (true) {
            System.out.print(prompt + " [" + current + "]: ");
            String value = nextLine();
            if (value.isEmpty()) return current;
            Integer number = parseInt(value, min, max);
            if (number != null) return number;
        }
    }

    public double readDoubleOrKeep(String prompt, double current, double min, double max) {
        while (true) {
            System.out.print(prompt + " [" + current + "]: ");
            String value = nextLine();
            if (value.isEmpty()) return current;
            Double number = parseDouble(value, min, max);
            if (number != null) return number;
        }
    }

    public String readEmailOrKeep(String prompt, String current) {
        while (true) {
            System.out.print(prompt + " [" + current + "]: ");
            String value = nextLine();
            if (value.isEmpty()) return current;
            if (isValidEmail(value)) return value;
        }
    }

    public boolean confirm(String prompt) {
        System.out.print(prompt + " (y/n): ");
        String value = nextLine();
        return value.equalsIgnoreCase("y") || value.equalsIgnoreCase("yes");
    }

    public void pause() {
        System.out.print("\nPress Enter to continue...");
        nextLine();
    }

    // ---- helpers ----

    private String nextLine() {
        if (!scanner.hasNextLine()) {
            // Input stream closed (e.g. Ctrl+D) - exit cleanly instead of looping forever.
            System.out.println("\nInput closed. Exiting.");
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }

    private Integer parseInt(String value, int min, int max) {
        try {
            int number = Integer.parseInt(value);
            if (number >= min && number <= max) return number;
            System.out.println("  Enter a number between " + min + " and " + max + ".");
        } catch (NumberFormatException e) {
            System.out.println("  Please enter a whole number.");
        }
        return null;
    }

    private Double parseDouble(String value, double min, double max) {
        try {
            double number = Double.parseDouble(value);
            if (number >= min && number <= max) return number;
            System.out.println("  Enter a number between " + min + " and " + max + ".");
        } catch (NumberFormatException e) {
            System.out.println("  Please enter a valid number.");
        }
        return null;
    }

    private boolean isValidEmail(String value) {
        if (EMAIL.matcher(value).matches()) return true;
        System.out.println("  Invalid email format (example: name@mail.com).");
        return false;
    }
}
