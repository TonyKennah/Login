package uk.org.kennah.login.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.co.pluckier.mongo.Repo;
import uk.co.pluckier.mongo.UserRepo;

import java.util.Collections;

@Service
public class MongoUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Use a try-with-resources block to ensure the database connection is closed.
        try (Repo userRepo = UserRepo.getDefaultInstance()) {
            uk.co.pluckier.model.User user = userRepo.get(username);

            if (user == null) {
                throw new UsernameNotFoundException("User not found with username: " + username);
            }

            // Convert your application's User object to Spring Security's UserDetails object.
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    Collections.emptyList() // Use an empty list for authorities/roles for now
            );
        }
    }
}
