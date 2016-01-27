package org.openlmis.vaccine.domain.VaccineOrderRequisition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VaccineOrderRequisitionStatusChange extends BaseModel {

    private Long orderId;

    private VaccineOrderStatus status;
    private String userName;

    private String firstName;

    private String lastName;

    private Date date;

    public VaccineOrderRequisitionStatusChange(VaccineOrderRequisition orderRequisition, VaccineOrderStatus statusToSave, Long userId) {
        orderId = orderRequisition.getId();
        status = statusToSave;
        createdBy  = userId;
        modifiedBy = userId;
    }


}
