package com.satyamsammi.socialmedia.dtos.genericresponse;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * This generic Success Response Class should be extended to
 * provide extra details in specific use-cases.
 */

@Data
@NoArgsConstructor
@SuperBuilder
public class SuccessResponse {
    private Boolean success;
    private String message;
}
