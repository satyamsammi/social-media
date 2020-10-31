package com.satyamsammi.socialmedia.repositories;

import com.satyamsammi.socialmedia.models.PlatformUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformUserRepository extends JpaRepository<PlatformUser, Long> {
    PlatformUser findById(long id);

    PlatformUser findByUsernameAndAccountActive(String username, Boolean active);
}
