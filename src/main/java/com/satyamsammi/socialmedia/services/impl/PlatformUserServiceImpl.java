package com.satyamsammi.socialmedia.services.impl;

import com.satyamsammi.socialmedia.constants.Constants;
import com.satyamsammi.socialmedia.dtos.genericresponse.SuccessResponse;
import com.satyamsammi.socialmedia.dtos.platformuser.request.CreatePlatformUserRequest;
import com.satyamsammi.socialmedia.dtos.platformuser.response.GetActiveFriendsResponse;
import com.satyamsammi.socialmedia.dtos.platformuser.response.GetPendingFriendRequestResponse;
import com.satyamsammi.socialmedia.exceptions.APIRuntimeException;
import com.satyamsammi.socialmedia.models.PlatformUser;
import com.satyamsammi.socialmedia.models.PlatformUserRelationship;
import com.satyamsammi.socialmedia.repositories.PlatformUserRelationshipRepository;
import com.satyamsammi.socialmedia.repositories.PlatformUserRepository;
import com.satyamsammi.socialmedia.services.PlatformUserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class PlatformUserServiceImpl implements PlatformUserService {
    private final PlatformUserRepository platformUserRepository;
    private final PlatformUserRelationshipRepository platformUserRelationshipRepository;

    @Override
    public SuccessResponse createPlatformUser(CreatePlatformUserRequest createPlatformUserRequest) {
        PlatformUser platformUser = platformUserRepository.findByUsernameAndAccountActive(createPlatformUserRequest.getUsername(), true);
        if (platformUser != null) {
            throw new APIRuntimeException("Username has been already taken. Please try something else.",
                    HttpStatus.BAD_REQUEST);
        }
        platformUser = platformUserRepository.save(PlatformUser.builder()
                .username(createPlatformUserRequest.getUsername())
                .firstName(createPlatformUserRequest.getFirstName())
                .lastName(createPlatformUserRequest.getLastName())
                .email(createPlatformUserRequest.getEmail())
                .accountActive(true)
                .build());

        return SuccessResponse.builder()
                .success(true)
                .message("Platform user has been created with id - " + platformUser.getId())
                .build();
    }

    @Override
    public SuccessResponse sendFriendRequest(String requesterUsername, String requesteeUsername) {
        if (requesterUsername.equals(requesteeUsername)) {
            throw new APIRuntimeException("you can't send friend request to yourself", HttpStatus.BAD_REQUEST);
        }
        PlatformUser requesterUser = platformUserRepository.findByUsernameAndAccountActive(requesterUsername, true);
        if (requesterUser == null) {
            throw new APIRuntimeException("Requester username doesn't exist", HttpStatus.BAD_REQUEST);
        }
        PlatformUser requesteeUser = platformUserRepository.findByUsernameAndAccountActive(requesteeUsername, true);
        if (requesteeUser == null) {
            throw new APIRuntimeException("Requestee username doesn't exist", HttpStatus.BAD_REQUEST);
        }
        if (isSendingFriendRequestValid(requesterUser, requesteeUser)) {
            platformUserRelationshipRepository.save(PlatformUserRelationship.builder()
                    .requesterUser(requesterUser)
                    .requesteeUser(requesteeUser)
                    .status(Constants.PlatformUserRelationshipStatus.PENDING)
                    .build());
            return SuccessResponse.builder()
                    .success(true)
                    .message("Request successfully sent.")
                    .build();
        }


        return null;
    }

    @Override
    public GetPendingFriendRequestResponse fetchPendingFriendRequests(String username) {
        PlatformUser platformUser = platformUserRepository.findByUsernameAndAccountActive(username, true);
        if (platformUser == null) {
            throw new APIRuntimeException("Username doesn't exist",
                    HttpStatus.BAD_REQUEST);
        }
        List<PlatformUserRelationship> pendingRequestList = platformUserRelationshipRepository.
                findByRequesteeUserAndStatus(platformUser, Constants.PlatformUserRelationshipStatus.PENDING);
        if (pendingRequestList.isEmpty()) {
            throw new APIRuntimeException("No pending friend requests", HttpStatus.NOT_FOUND);
        }
        List<String> usernameList = new ArrayList<>();
        pendingRequestList
                .stream()
                .forEach((pendingRequest) -> usernameList.add(pendingRequest.getRequesterUser().getUsername()));
        return GetPendingFriendRequestResponse.builder()
                .friendRequests(usernameList)
                .build();
    }

    @Override
    public SuccessResponse acceptFriendRequest(String requesterUsername, String requesteeUsername) {
        if (requesterUsername.equals(requesteeUsername)) {
            throw new APIRuntimeException("Invalid action Can't accept your own request", HttpStatus.BAD_REQUEST);
        }
        PlatformUser requesterUser = platformUserRepository.findByUsernameAndAccountActive(requesterUsername, true);
        if (requesterUser == null) {
            throw new APIRuntimeException("Requester username doesn't exist", HttpStatus.BAD_REQUEST);
        }
        PlatformUser requesteeUser = platformUserRepository.findByUsernameAndAccountActive(requesteeUsername, true);
        if (requesteeUser == null) {
            throw new APIRuntimeException("Requestee username doesn't exist", HttpStatus.BAD_REQUEST);
        }
        PlatformUserRelationship pendingRequest = getPendingRequest(requesterUser, requesteeUser);
        // update the status and create reverse relationship as well
        // for the faster retrieval of friends
        pendingRequest = pendingRequest.toBuilder()
                .status(Constants.PlatformUserRelationshipStatus.FRIENDS)
                .build();
        platformUserRelationshipRepository.save(pendingRequest);
        // reverse relationship
        platformUserRelationshipRepository.save(PlatformUserRelationship.builder()
                .requesterUser(requesteeUser)
                .requesteeUser(requesterUser)
                .status(Constants.PlatformUserRelationshipStatus.FRIENDS)
                .build());
        return SuccessResponse.builder()
                .success(true)
                .message("Request successfully accepted.")
                .build();

    }

    @Override
    public GetActiveFriendsResponse fetchingActiveFriends(String username) {
        PlatformUser platformUser = platformUserRepository.findByUsernameAndAccountActive(username, true);
        if (platformUser == null) {
            throw new APIRuntimeException("Username doesn't exist",
                    HttpStatus.BAD_REQUEST);
        }
        List<PlatformUserRelationship> pendingRequestList = platformUserRelationshipRepository.
                findByRequesterUserAndStatus(platformUser, Constants.PlatformUserRelationshipStatus.FRIENDS);
        if (pendingRequestList.isEmpty()) {
            throw new APIRuntimeException("No active friends found", HttpStatus.NOT_FOUND);
        }
        List<String> usernameList = new ArrayList<>();
        pendingRequestList
                .stream()
                .forEach((pendingRequest) -> usernameList.add(pendingRequest.getRequesteeUser().getUsername()));
        return GetActiveFriendsResponse.builder()
                .friends(usernameList)
                .build();
    }

    private PlatformUserRelationship getPendingRequest(PlatformUser requesterUser, PlatformUser requesteeUser) {
        try {
            // check if requestee has blocked the requester
            List<PlatformUserRelationship> userRelationshipList = platformUserRelationshipRepository.
                    findByRequesterUserAndRequesteeUserOrderByCreatedOnDesc(requesteeUser, requesterUser);
            if (!userRelationshipList.isEmpty()) {
                if (Constants.PlatformUserRelationshipStatus.BLOCKS.equals(userRelationshipList.get(0).getStatus())) {
                    log.error("getPendingRequest:: {} has blocked {} )", requesteeUser.getUsername(),
                            requesterUser.getUsername());
                    throw new APIRuntimeException("Requestee doesn't exist/blocking you" + userRelationshipList.get(0).getStatus(),
                            HttpStatus.NOT_FOUND);
                }
                if (Constants.PlatformUserRelationshipStatus.FRIENDS.equals(userRelationshipList.get(0).getStatus())) {
                    log.error("getPendingRequest:: {} is already friend with {} )", requesteeUser.getUsername(),
                            requesterUser.getUsername());
                    throw new APIRuntimeException("Already friend", HttpStatus.NOT_FOUND);
                }
            }
            // check if pending friend request exists
            userRelationshipList = platformUserRelationshipRepository.
                    findByRequesterUserAndRequesteeUserOrderByCreatedOnDesc(requesterUser, requesteeUser);
            if (!userRelationshipList.isEmpty() &&
                    Constants.PlatformUserRelationshipStatus.PENDING.equals(userRelationshipList.get(0).getStatus())) {
                log.error("getPendingRequest:: Relationship already exists in between {}<>{} as {}",
                        requesteeUser.getUsername(), requesterUser.getUsername(), userRelationshipList.get(0).getStatus());
                return userRelationshipList.get(0);
            } else {
                log.error("getPendingRequest:: Pending Friend request doesn't exist from {} to {}",
                        requesterUser.getUsername(), requesteeUser.getUsername());
                throw new APIRuntimeException("Friend request doesn't exist", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("isFriendRequestValid:: Exception - {}", e.toString());
            throw e;
        }
    }

    private boolean isSendingFriendRequestValid(PlatformUser requesterUser, PlatformUser requesteeUser) {
        try {
            // The reverse logic is verified in case requestee
            // has already sent the request or blocking the requester
            // or already friend with the user.
            List<PlatformUserRelationship> userRelationshipList = platformUserRelationshipRepository.
                    findByRequesterUserAndRequesteeUserOrderByCreatedOnDesc(requesteeUser, requesterUser);
            if (!userRelationshipList.isEmpty() &&
                    !Constants.PlatformUserRelationshipStatus.UNFRIEND.equals(userRelationshipList.get(0).getStatus())) {
                log.error("isSendingFriendRequestValid:: Relationship already exists in between {}<>{} as {}",
                        requesteeUser.getUsername(), requesterUser.getUsername(), userRelationshipList.get(0).getStatus());
                throw new APIRuntimeException("Relationship already exists requesteeUser<>requesterUser - " + userRelationshipList.get(0).getStatus(),
                        HttpStatus.BAD_REQUEST);
            }
            userRelationshipList = platformUserRelationshipRepository.
                    findByRequesterUserAndRequesteeUserOrderByCreatedOnDesc(requesterUser, requesteeUser);
            if (!userRelationshipList.isEmpty() &&
                    !Constants.PlatformUserRelationshipStatus.UNFRIEND.equals(userRelationshipList.get(0).getStatus())) {
                log.error("isSendingFriendRequestValid:: Relationship already exists in between {}<>{} as {}",
                        requesterUser.getUsername(), requesteeUser.getUsername(), userRelationshipList.get(0).getStatus());
                throw new APIRuntimeException("Relationship already exists in requesterUser<>requesteeUser - " + userRelationshipList.get(0).getStatus(),
                        HttpStatus.BAD_REQUEST);
            }
            return true;
        } catch (Exception e) {
            log.error("isFriendRequestValid:: Exception - {}", e.toString());
            throw e;
        }
    }
}
