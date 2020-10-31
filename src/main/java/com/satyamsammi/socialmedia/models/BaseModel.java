package com.satyamsammi.socialmedia.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.Instant;

/**
 * THis is the base model class which can be extended by other models. It contains (and should contain) only the boiler plate code
 * which must are on high re-usability scenarios and mostly kept as part of best practices.
 */
@MappedSuperclass
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseModel {
    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    protected Instant createdOn;

    @UpdateTimestamp
    @Column(name = "updated_on")
    protected Instant updatedOn;
}