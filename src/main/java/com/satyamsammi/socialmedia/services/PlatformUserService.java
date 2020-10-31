package com.satyamsammi.socialmedia.services;

import com.satyamsammi.socialmedia.dtos.genericresponse.SuccessResponse;
import com.satyamsammi.socialmedia.dtos.platformuser.request.CreatePlatformUserRequest;
import com.satyamsammi.socialmedia.dtos.platformuser.response.GetActiveFriendsResponse;
import com.satyamsammi.socialmedia.dtos.platformuser.response.GetPendingFriendRequestResponse;

public interface PlatformUserService {
    SuccessResponse createPlatformUser(CreatePlatformUserRequest createPlatformUserRequest);

    SuccessResponse sendFriendRequest(String requesterUsername, String requesteeUsername);

    GetPendingFriendRequestResponse fetchPendingFriendRequests(String username);

    SuccessResponse acceptFriendRequest(String requesterUsername, String requesteeUsername);

    GetActiveFriendsResponse fetchingActiveFriends(String username);
}
