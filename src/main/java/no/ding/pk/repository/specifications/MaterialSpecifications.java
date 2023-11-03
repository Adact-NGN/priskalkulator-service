package no.ding.pk.repository.specifications;

import no.ding.pk.domain.Discount;
import no.ding.pk.domain.offer.Material;
import org.springframework.data.jpa.domain.Specification;

public class MaterialSpecifications {

    public static Specification<Material> withSalesOrg(String salesOrg) {
        if(salesOrg == null) {
            return null;
        } else {
            return (root, query, cb) -> cb.equal(root.get("salesOrg"), salesOrg);
        }
    }

    public static Specification<Material> withSalesOffice(String salesOffice) {
        if(salesOffice == null) {
            return null;
        } else {
            return (root, query, cb) -> cb.equal(root.get("salesOffice"), salesOffice);
        }
    }

    public static Specification<Material> withMaterialNumber(String materialNumber) {
        if(materialNumber == null) {
            return null;
        } else {
            return (root, query, cb) -> cb.equal(root.get("materialNumber"), materialNumber);
        }
    }

    public static Specification<Material> withDeviceType(String deviceType) {
        if(deviceType == null) {
            return null;
        } else {
            return (root, query, cb) -> cb.equal(root.get("deviceType"), deviceType);
        }
    }

    public static Specification<Material> withZone(String salesZone) {
        if(salesZone == null) {
            return null;
        } else {
            return (root, query, cb) -> cb.equal(root.get("salesZone"), salesZone);
        }
    }
}
