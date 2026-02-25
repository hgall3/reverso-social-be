package com.reverso.config;

import com.reverso.model.User;
import com.reverso.model.enums.Role;
import com.reverso.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Value("${app.security.admin-password}")
    private String adminPassword;

    @org.springframework.beans.factory.annotation.Value("${app.security.editor-password}")
    private String editorPassword;

    @Override
    public void run(String... args) {
        log.info("Inicializando usuarios por defecto...");
        createDefaultUsers();
        log.info("Usuarios por defecto inicializados correctamente");
    }

    private void createDefaultUsers() {
        // Configuramos tu email personal para el ADMIN
        String adminEmail = "gagallegosanda@gmail.com";

        userRepository.findByEmail(adminEmail).ifPresentOrElse(
                user -> {
                    user.setPassword(passwordEncoder.encode(adminPassword));
                    userRepository.save(user);
                    log.info("Usuario ADMIN ({}) actualizado con contraseña del entorno", adminEmail);
                },
                () -> {
                    User admin = User.builder()
                            .fullName("Gabi Gallegos - Admin")
                            .email(adminEmail)
                            .password(passwordEncoder.encode(adminPassword))
                            .phone("+34 600 000 000")
                            .companyName("Reverso Social")
                            .role(Role.ADMIN)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    userRepository.save(admin);
                    log.info("Usuario ADMIN ({}) creado exitosamente", adminEmail);
                });

        // Mantenemos el editor genérico o cámbialo si lo prefieres
        userRepository.findByEmail("editor@reversosocial.com").ifPresentOrElse(
                user -> {
                    user.setPassword(passwordEncoder.encode(editorPassword));
                    userRepository.save(user);
                    log.info("Usuario EDITOR actualizado con contraseña del entorno");
                },
                () -> {
                    User editor = User.builder()
                            .fullName("Colaboradora Externa")
                            .email("editor@reversosocial.com")
                            .password(passwordEncoder.encode(editorPassword))
                            .phone("+34 900 000 001")
                            .companyName("Reverso Social")
                            .role(Role.EDITOR)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    userRepository.save(editor);
                    log.info("Usuario EDITOR creado");
                });
    }
}