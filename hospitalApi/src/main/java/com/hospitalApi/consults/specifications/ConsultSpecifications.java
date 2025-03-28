package com.hospitalApi.consults.specifications;

import com.hospitalApi.consults.models.Consult;
import org.springframework.data.jpa.domain.Specification;

public class ConsultSpecifications {

    public static Specification<Consult> hasId(String id) {
        return (root, query, cb) ->
                id == null ? null : cb.equal(root.get("id"), id);
    }

    public static Specification<Consult> isPaid(Boolean isPaid) {
        return (root, query, cb) -> {
            if (isPaid == null) return null;
            return Boolean.TRUE.equals(isPaid) ? cb.isTrue(root.get("isPaid")) : cb.isFalse(root.get("isPaid"));
        };
    }

    public static Specification<Consult> isInternado(Boolean isInternado) {
        return (root, query, cb) -> {
            if (isInternado == null) return null;
            return Boolean.TRUE.equals(isInternado) ? cb.isTrue(root.get("isInternado")) : cb.isFalse(root.get("isInternado"));
        };
    }
    
    public static Specification<Consult> hasPatientDpi(String dpi) {
        return (root, query, cb) ->
            dpi == null ? null : cb.like(cb.lower(root.get("patient").get("dpi")), "%" + dpi.toLowerCase() + "%");
    }    

    public static Specification<Consult> hasPatientFirstnames(String firstnames) {
        return (root, query, cb) ->
                firstnames == null ? null : cb.like(cb.lower(root.get("patient").get("firstnames")), "%" + firstnames.toLowerCase() + "%");
    }

    public static Specification<Consult> hasPatientLastnames(String lastnames) {
        return (root, query, cb) ->
                lastnames == null ? null : cb.like(cb.lower(root.get("patient").get("lastnames")), "%" + lastnames.toLowerCase() + "%");
    }

    public static Specification<Consult> hasEmployeeId(String employeeId) {
        return (root, query, cb) -> {
            if (employeeId == null) return null;
            var join = root.join("employeeConsults");
            return cb.equal(join.get("employee").get("id"), employeeId);
        };
    }

    public static Specification<Consult> hasEmployeeFirstName(String firstName) {
        return (root, query, cb) -> {
            if (firstName == null) return null;
            var join = root.join("employeeConsults");
            return cb.like(cb.lower(join.get("employee").get("firstName")), "%" + firstName.toLowerCase() + "%");
        };
    }

    public static Specification<Consult> hasEmployeeLastName(String lastName) {
        return (root, query, cb) -> {
            if (lastName == null) return null;
            var join = root.join("employeeConsults");
            return cb.like(cb.lower(join.get("employee").get("lastName")), "%" + lastName.toLowerCase() + "%");
        };
    }
}
