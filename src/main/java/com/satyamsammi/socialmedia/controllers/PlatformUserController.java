package com.satyamsammi.socialmedia.controllers;

import com.satyamsammi.socialmedia.dtos.genericresponse.FailureResponse;
import com.satyamsammi.socialmedia.dtos.genericresponse.SuccessResponse;
import com.satyamsammi.socialmedia.dtos.platformuser.request.CreatePlatformUserRequest;
import com.satyamsammi.socialmedia.dtos.platformuser.response.GetActiveFriendsResponse;
import com.satyamsammi.socialmedia.dtos.platformuser.response.GetPendingFriendRequestResponse;
import com.satyamsammi.socialmedia.services.PlatformUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/platform-user/")
@Api(value = "Platform User Controller")
@Log4j2
public class PlatformUserController {
    private final PlatformUserService platformUserService;

    @ApiOperation(code = 201, value = "create/update assessment for the claim", response = SuccessResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created new platform user", response = SuccessResponse.class),
            @ApiResponse(code = 400, message = "Bad request, username not unique/invalid param", response = FailureResponse.class),
            @ApiResponse(code = 500, message = "logic error in API", response = FailureResponse.class)
    })
    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse> createPlatformUser(@Valid @RequestBody CreatePlatformUserRequest createPlatformUserRequest) {
        log.info("createPlatformUser:: Request - {}", createPlatformUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(platformUserService.createPlatformUser(createPlatformUserRequest));
    }


    @ApiOperation(value = "send friend request to user", response = SuccessResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully sent the friend request"),
            @ApiResponse(code = 400, message = "Bad request, invalid params - username doesn't exist", response = FailureResponse.class),
            @ApiResponse(code = 500, message = "logic error in API", response = FailureResponse.class)
    })
    @RequestMapping(value = "/add/{requesterUsername}/{requesteeUsername}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse> sendFriendRequest(@PathVariable("requesterUsername") String requesterUsername,
                                                      @PathVariable("requesteeUsername") String requesteeUsername) {
        log.info("sendFriendRequest:: {} wants to add {}", requesterUsername, requesteeUsername);
        return ResponseEntity.ok(platformUserService.sendFriendRequest(requesterUsername, requesteeUsername));
    }


    @ApiOperation(value = "accept friend request", response = SuccessResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully accepted the friend request"),
            @ApiResponse(code = 400, message = "Bad request, invalid params - username doesn't exist", response = FailureResponse.class),
            @ApiResponse(code = 404, message = "Friend request doesn't exist/ requestee blocking", response = FailureResponse.class),
            @ApiResponse(code = 500, message = "logic error in API", response = FailureResponse.class)
    })
    @RequestMapping(value = "/acceptRequest/{requesterUsername}/{requesteeUsername}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse> acceptFriendRequest(@PathVariable("requesterUsername") String requesterUsername,
                                                        @PathVariable("requesteeUsername") String requesteeUsername) {
        log.info("acceptFriendRequest:: {} trying to accept {} friend request.", requesteeUsername, requesterUsername);
        return ResponseEntity.ok(platformUserService.acceptFriendRequest(requesterUsername, requesteeUsername));
    }


    @ApiOperation(value = "get pending friend request list", response = GetPendingFriendRequestResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully fetched pending request usernames list"),
            @ApiResponse(code = 400, message = "Bad request, invalid params", response = FailureResponse.class),
            @ApiResponse(code = 404, message = "No pending request for the requested username", response = FailureResponse.class),
            @ApiResponse(code = 500, message = "logic error in API", response = FailureResponse.class)
    })
    @RequestMapping(value = "/friendRequests/{username}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GetPendingFriendRequestResponse> getPendingFriendRequests(@PathVariable("username") String username) {
        log.info("getPendingFriendRequests:: Trying to fetch pending friend requests for {}", username);
        return ResponseEntity.ok(platformUserService.fetchPendingFriendRequests(username));
    }


    @ApiOperation(value = "get active friends list", response = GetActiveFriendsResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully fetched active friends"),
            @ApiResponse(code = 400, message = "Bad request, invalid params", response = FailureResponse.class),
            @ApiResponse(code = 404, message = "No active friends for the requested username", response = FailureResponse.class),
            @ApiResponse(code = 500, message = "logic error in API", response = FailureResponse.class)
    })
    @RequestMapping(value = "/friends/{username}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GetActiveFriendsResponse> getActiveFriends(@PathVariable("username") String username) {
        log.info("getActiveFriends:: Trying to fetch active friends for {}", username);
        return ResponseEntity.ok(platformUserService.fetchingActiveFriends(username));
    }
}
