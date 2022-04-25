package com.backend.ehealthspringboot.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
public class DoctorRatingKey implements Serializable {

    @Column(name = "visitor_id")
    Long visitorId;

    @Column(name = "doctor_id")
    Long doctorId;

    public DoctorRatingKey() {
    }

    public DoctorRatingKey(Long doctorId, Long visitorId) {
        this.visitorId = visitorId;
        this.doctorId = doctorId;

    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        DoctorRatingKey  doctorRatingKey  = ( DoctorRatingKey ) object;
        return visitorId.equals(doctorRatingKey.visitorId) &&
                doctorId.equals(doctorRatingKey.doctorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitorId, doctorId);
    }
}
