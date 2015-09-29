package org.openlmis.vaccine.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper = false)
public class OrderRequisitionDTO {

    Long id;
    Long periodId;
    Long programId;
    Long facilityId;
    String status;
    String periodName;
    String programName;
    String facilityType;
    String districtName;

    private Date submittedDate;
    private Date modifiedDate;
    private Date periodStartDate;
    private Date periodEndDate;

    private String stringModifiedDate;
    private String stringPeriodStartDate;
    private String stringPeriodEndDate;

    private String requisitionStatus;


    //Used to view pending requisition

    String facilityName;
    Date orderDate;



    //Used to join order requisition and stock Card


    @JsonIgnore
    Facility facility;

    Long productId;

    Long quantityRequested ;

    String productCategory;

    Boolean emergency;




    public static List<OrderRequisitionDTO> prepareForView(List<VaccineOrderRequisition> requisitions) {
        List<OrderRequisitionDTO> result = new ArrayList<>();
        for (VaccineOrderRequisition requisition : requisitions) {
            OrderRequisitionDTO requisitionDTO = populateDTOWithRequisition(requisition);
            requisitionDTO.requisitionStatus = requisition.getStatus().name();
            result.add(requisitionDTO);
        }
        return result;
    }


    private static OrderRequisitionDTO populateDTOWithRequisition(VaccineOrderRequisition requisition) {

        OrderRequisitionDTO rnrDTO = new OrderRequisitionDTO();

        rnrDTO.id = requisition.getId();
        rnrDTO.programId = requisition.getProgram().getId();
        rnrDTO.facilityId = requisition.getFacility().getId();
        rnrDTO.programName = requisition.getProgram().getName();
        rnrDTO.facilityName = requisition.getFacility().getName();

        rnrDTO.facilityType = requisition.getFacility().getFacilityType().getName();
        rnrDTO.districtName = requisition.getFacility().getGeographicZone().getName();

        rnrDTO.modifiedDate = requisition.getModifiedDate();
        rnrDTO.periodStartDate = requisition.getPeriod().getStartDate();
        rnrDTO.periodEndDate = requisition.getPeriod().getEndDate();

        rnrDTO.stringModifiedDate = formatDate(requisition.getModifiedDate());
        rnrDTO.stringPeriodStartDate = formatDate(requisition.getPeriod().getStartDate());
        rnrDTO.stringPeriodEndDate = formatDate(requisition.getPeriod().getEndDate());

        rnrDTO.emergency = requisition.isEmergency();
        return rnrDTO;
    }

    private void formatDates(){
        stringModifiedDate    = formatDate(modifiedDate);
        stringPeriodStartDate = formatDate(periodStartDate);
        stringPeriodEndDate   = formatDate(periodEndDate);
    }

    private static String formatDate(Date date) {
        return DateUtil.getFormattedDate(date, "dd/MM/yyyy");
    }

}
