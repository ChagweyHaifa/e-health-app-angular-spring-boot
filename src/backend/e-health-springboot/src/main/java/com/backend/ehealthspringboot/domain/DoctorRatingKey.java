package com.backend.ehealthspringboot.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class DoctorRatingKey implements Serializable {

    @Column(name = "user_id")
    Long userId;

    @Column(name = "doctor_id")
    Long doctorId;

    public DoctorRatingKey() {
    }

    public DoctorRatingKey(Long doctorId, Long userId) {
        this.userId = userId;
        this.doctorId = doctorId;

    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        DoctorRatingKey  doctorRatingKey  = ( DoctorRatingKey ) object;
        return userId.equals(doctorRatingKey.userId) &&
                doctorId.equals(doctorRatingKey.doctorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, doctorId);
    }
}
