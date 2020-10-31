package com.satyamsammi.socialmedia.dtos.platformuser.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@SuperBuilder()
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GetActiveFriendsResponse {
    List<String> friends;
}
