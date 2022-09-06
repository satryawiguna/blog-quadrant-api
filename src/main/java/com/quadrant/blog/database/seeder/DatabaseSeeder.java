package com.quadrant.blog.database.seeder;

import com.quadrant.blog.entity.RoleEntity;
import com.quadrant.blog.entity.UserEntity;
import com.quadrant.blog.repository.RoleRepository;
import com.quadrant.blog.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final Log logger = LogFactory.getLog(getClass());
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public DatabaseSeeder(RoleRepository roleRepository,
                          UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedRoleTable();
        seedUserTable();
    }

    private void seedRoleTable() {
        if (roleRepository.count() == 0) {
            RoleEntity roleAdmin = new RoleEntity();
            roleAdmin.setName("ADMIN");
            roleRepository.save(roleAdmin);

            RoleEntity roleUser = new RoleEntity();
            roleUser.setName("USER");

            roleRepository.save(roleUser);

            logger.info("Role table seeded");
        } else {
            logger.trace("Role seeding not required");
        }
    }

    private void seedUserTable() {
        if (userRepository.count() == 0) {
            RoleEntity roleAdmin = roleRepository.findByName("ADMIN");

            UserEntity userAdmin = new UserEntity();
            userAdmin.setNickName("Admin");
            userAdmin.setFullName("Administrator");
            userAdmin.setEmail("admin@quadrant-blog.com");
            userAdmin.setPassword(new BCryptPasswordEncoder().encode("12345"));
            userAdmin.setRole(roleAdmin);

            userRepository.save(userAdmin);

            logger.info("User table seeded");
        } else {
            logger.trace("User seeding not required");
        }
    }
}
