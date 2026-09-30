package com.prince.materialportal.config;

import com.prince.materialportal.entity.Material;
import com.prince.materialportal.entity.Role;
import com.prince.materialportal.entity.User;
import com.prince.materialportal.repository.MaterialRepository;
import com.prince.materialportal.repository.RoleRepository;
import com.prince.materialportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final MaterialRepository materialRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initRolesAndUsers();
        initSampleMaterials();
    }

    private void initRolesAndUsers() {
        Role studentRole = roleRepository.findByName("ROLE_STUDENT")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_STUDENT")));

        Role facultyRole = roleRepository.findByName("ROLE_FACULTY")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_FACULTY")));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        // Default Student 1
        if (userRepository.findByUsernameIgnoreCase("psvpec.2025.students@gmail.com").isEmpty()) {
            userRepository.save(User.builder()
                    .username("psvpec.2025.students@gmail.com")
                    .password(passwordEncoder.encode("password"))
                    .fullName("PSVPEC Student")
                    .roles(Set.of(studentRole))
                    .build());
            log.info("Initialized default user: psvpec.2025.students@gmail.com");
        }

        // Default Faculty 1
        if (userRepository.findByUsernameIgnoreCase("psvpec.2025.staffs@gmail.com").isEmpty()) {
            userRepository.save(User.builder()
                    .username("psvpec.2025.staffs@gmail.com")
                    .password(passwordEncoder.encode("password"))
                    .fullName("PSVPEC Faculty Staff")
                    .roles(Set.of(facultyRole))
                    .build());
            log.info("Initialized default user: psvpec.2025.staffs@gmail.com");
        }

        // Additional convenient demo users
        if (userRepository.findByUsernameIgnoreCase("student@prince.edu").isEmpty()) {
            userRepository.save(User.builder()
                    .username("student@prince.edu")
                    .password(passwordEncoder.encode("password"))
                    .fullName("Prince Student")
                    .roles(Set.of(studentRole))
                    .build());
        }

        if (userRepository.findByUsernameIgnoreCase("faculty@prince.edu").isEmpty()) {
            userRepository.save(User.builder()
                    .username("faculty@prince.edu")
                    .password(passwordEncoder.encode("password"))
                    .fullName("Prince Faculty Head")
                    .roles(Set.of(facultyRole, adminRole))
                    .build());
        }
    }

    private void initSampleMaterials() {
        if (materialRepository.count() == 0) {
            List<Material> sampleMaterials = List.of(
                Material.builder()
                        .subjectName("Java Programming")
                        .department("Information Technology")
                        .courseYear("1st Year")
                        .sem1("https://drive.google.com/drive/folders/17fUS7YzfL001mcFGnRAUtTWihcz7F_qW?usp=drive_link")
                        .sem2("https://drive.google.com/drive/folders/1IVVzJ_SJ7XwF8mNQWitE9ALsOFn_Sted?usp=drive_link")
                        .activeStatus(true)
                        .build(),
                Material.builder()
                        .subjectName("Data Structures & Algorithms")
                        .department("Computer Science & Engineering")
                        .courseYear("2nd Year")
                        .sem1("https://drive.google.com/drive/folders/17fUS7YzfL001mcFGnRAUtTWihcz7F_qW?usp=drive_link")
                        .sem2("https://drive.google.com/drive/folders/1IVVzJ_SJ7XwF8mNQWitE9ALsOFn_Sted?usp=drive_link")
                        .activeStatus(true)
                        .build(),
                Material.builder()
                        .subjectName("Database Management Systems")
                        .department("Computer Science & Engineering")
                        .courseYear("2nd Year")
                        .sem1("https://drive.google.com/drive/folders/17fUS7YzfL001mcFGnRAUtTWihcz7F_qW?usp=drive_link")
                        .sem2("https://drive.google.com/drive/folders/1IVVzJ_SJ7XwF8mNQWitE9ALsOFn_Sted?usp=drive_link")
                        .activeStatus(true)
                        .build(),
                Material.builder()
                        .subjectName("Cloud Computing & DevOps")
                        .department("Information Technology")
                        .courseYear("3rd Year")
                        .sem1("https://drive.google.com/drive/folders/17fUS7YzfL001mcFGnRAUtTWihcz7F_qW?usp=drive_link")
                        .sem2("https://drive.google.com/drive/folders/1IVVzJ_SJ7XwF8mNQWitE9ALsOFn_Sted?usp=drive_link")
                        .activeStatus(true)
                        .build(),
                Material.builder()
                        .subjectName("Artificial Intelligence & Machine Learning")
                        .department("Artificial Intelligence & Data Science")
                        .courseYear("3rd Year")
                        .sem1("https://drive.google.com/drive/folders/17fUS7YzfL001mcFGnRAUtTWihcz7F_qW?usp=drive_link")
                        .sem2("https://drive.google.com/drive/folders/1IVVzJ_SJ7XwF8mNQWitE9ALsOFn_Sted?usp=drive_link")
                        .activeStatus(true)
                        .build(),
                Material.builder()
                        .subjectName("Digital Signal Processing")
                        .department("Electronics & Communication Engineering")
                        .courseYear("3rd Year")
                        .sem1("https://drive.google.com/drive/folders/17fUS7YzfL001mcFGnRAUtTWihcz7F_qW?usp=drive_link")
                        .sem2("https://drive.google.com/drive/folders/1IVVzJ_SJ7XwF8mNQWitE9ALsOFn_Sted?usp=drive_link")
                        .activeStatus(true)
                        .build()
            );

            materialRepository.saveAll(sampleMaterials);
            log.info("Initialized {} sample study materials", sampleMaterials.size());
        }
    }
}
