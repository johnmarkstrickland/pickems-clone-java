package com.pickems.backend;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;
    private static final Pattern SPECIAL_CHARS = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");
    private static final Pattern UPPER_CASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWER_CASE = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("[0-9]");

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (password == null || password.trim()
                                        .isEmpty()) {
            addConstraintViolation(context, "Password cannot be empty");
            return false;
        }

        List<String> violations = new ArrayList<>();

        // Length check
        if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            violations.add(String.format("Password must be between %d and %d characters",
            MIN_LENGTH, MAX_LENGTH));
        }

        // Character type checks
        if (!UPPER_CASE.matcher(password)
                       .find()) {
            violations.add("Password must contain at least one uppercase letter");
        }
        if (!LOWER_CASE.matcher(password)
                       .find()) {
            violations.add("Password must contain at least one lowercase letter");
        }
        if (!DIGIT.matcher(password)
                  .find()) {
            violations.add("Password must contain at least one digit");
        }
        if (!SPECIAL_CHARS.matcher(password)
                          .find()) {
            violations.add("Password must contain at least one special character");
        }

        // Check for repeating characters
        if (hasRepeatingCharacters(password)) {
            violations.add("Password cannot contain repeating characters more than 3 times");
        }

        // Check for sequential characters
        if (hasSequentialCharacters(password)) {
            violations.add("Password cannot contain sequential characters");
        }

        if (!violations.isEmpty()) {
            addConstraintViolation(context, String.join(", ", violations));
            return false;
        }

        return true;
    }

    private boolean hasRepeatingCharacters(String password) {
        for (int i = 0; i < password.length() - 3; i++) {
            if (password.charAt(i) == password.charAt(i + 1) &&
                password.charAt(i) == password.charAt(i + 2) &&
                password.charAt(i) == password.charAt(i + 3)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSequentialCharacters(String password) {
        String lowerPass = password.toLowerCase();
        String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < alphabet.length() - 3; i++) {
            String forward = alphabet.substring(i, i + 4);
            String backward = new StringBuilder(forward).reverse()
                                                        .toString();

            if (lowerPass.contains(forward) || lowerPass.contains(backward)) {
                return true;
            }
        }
        return false;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message)
               .addConstraintViolation();
    }
}
