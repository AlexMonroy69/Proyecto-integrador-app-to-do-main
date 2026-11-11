package co.Proyecto_Backendll.domain;

import co.Proyecto_Backendll.domain.Exceptions.BusinessException;

public enum EnrollmentStatus {
    ACTIVE,
    CANCELLED,
    COMPLETE;

    public static EnrollmentStatus from(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException("Enrollment status is required");
        }
        try {
            return EnrollmentStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid enrollment status: " + value + ". Allowed values: ACTIVE, CANCELLED, COMPLETE");
        }
    }
}
