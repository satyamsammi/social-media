package com.satyamsammi.socialmedia.models;

import com.satyamsammi.socialmedia.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = PlatformUserRelationship.TABLE_NAME,
        indexes = {@Index(name = "requester_user_index", columnList = "requester_id"),
                @Index(name = "requestee_user_index", columnList = "requestee_id")})
public class PlatformUserRelationship extends BaseModel {
    public static final String TABLE_NAME = "platform_user_relationship";

    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "sequence",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "platform_user_relationship_sequence")
            }
    )
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private PlatformUser requesterUser;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "requestee_id", nullable = false)
    private PlatformUser requesteeUser;

    private Constants.PlatformUserRelationshipStatus status;

}
