package no.ding.pk.domain.offer;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class CustomerTermsPK implements Serializable {
    private static final long serialVersionUID = 4943289309822399321L;
    private long salesOfficeId;
    private long customerId;
    private long serialNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CustomerTermsPK customerTermsPK = (CustomerTermsPK) o;

        return new EqualsBuilder().append(salesOfficeId, customerTermsPK.salesOfficeId).append(customerId, customerTermsPK.customerId).append(serialNumber, customerTermsPK.serialNumber).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(salesOfficeId).append(customerId).append(serialNumber).toHashCode();
    }
}
