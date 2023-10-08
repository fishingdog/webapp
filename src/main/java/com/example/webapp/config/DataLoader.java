package com.example.webapp.config;

import com.example.webapp.csvmodel.UserCSV;
import com.example.webapp.model.User;
import com.example.webapp.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {


//        File file = new ClassPathResource("/opt/users.csv").getFile(); // delete this line in demo
//        FileReader reader = new FileReader(file); //change to "/opt/users.csv" in demo

        FileReader reader = new FileReader("/opt/users.csv");

        CsvToBean<UserCSV> csvToBean = new CsvToBeanBuilder<UserCSV>(reader)
                .withType(UserCSV.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        for (UserCSV userCSV : csvToBean) {
            String email = userCSV.getEmail();
            String firstName = userCSV.getFirstName();
            String lastName = userCSV.getLastName();
            String password =  userCSV.getPassword();

            Optional<User> existingUser = userRepository.findByEmail(email);
            if (!existingUser.isPresent()) {
                User user = new User();
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode(password));
                user.setAccountCreated(LocalDateTime.now());
                user.setAccountUpdated(LocalDateTime.now());
                userRepository.save(user);
            }
        }
        reader.close();
    }
}
