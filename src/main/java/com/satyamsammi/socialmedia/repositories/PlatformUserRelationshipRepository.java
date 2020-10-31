package com.satyamsammi.socialmedia.repositories;

import com.satyamsammi.socialmedia.constants.Constants;
import com.satyamsammi.socialmedia.models.PlatformUser;
import com.satyamsammi.socialmedia.models.PlatformUserRelationship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlatformUserRelationshipRepository extends JpaRepository<PlatformUserRelationship, Long> {
    List<PlatformUserRelationship> findByRequesterUserAndStatus(PlatformUser requesterUser, Constants.PlatformUserRelationshipStatus status);

    List<PlatformUserRelationship> findByRequesteeUserAndStatus(PlatformUser requesteeUser, Constants.PlatformUserRelationshipStatus status);

    List<PlatformUserRelationship> findByRequesterUserAndRequesteeUserAndStatusOrderByCreatedOnDesc(PlatformUser requesterUser,
                                                                                                    PlatformUser requesteeUser,
                                                                                                    Constants.PlatformUserRelationshipStatus status);

    List<PlatformUserRelationship> findByRequesterUserAndRequesteeUserOrderByCreatedOnDesc(PlatformUser requesterUser,
                                                                                           PlatformUser requesteeUser);
}
