package com.bitjawsbill;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123"; // Esta será la contraseña en texto plano
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("Contraseña en texto plano: " + rawPassword);
        System.out.println("Contraseña hasheada: " + encodedPassword);
    }
} 