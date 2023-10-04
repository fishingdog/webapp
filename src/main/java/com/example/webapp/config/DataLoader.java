package com.example.webapp.config;

import com.example.webapp.model.User;
import com.example.webapp.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.util.Optional;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        FileReader reader = new FileReader("/opt/user.csv");
        CsvToBean<User> csvToBean = new CsvToBeanBuilder<User>(reader)
                .withType(User.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        for (User user : csvToBean) {
            String email = user.getEmail();
            String firstName = user.getFirstName();
            String lastName = user.getLastName();
            String password =  user.getPassword();

            Optional<User> 
        }
    }

}
