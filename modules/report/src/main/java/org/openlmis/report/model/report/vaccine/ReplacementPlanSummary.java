
package org.openlmis.report.model.report.vaccine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplacementPlanSummary implements ReportData {


    private String serialNumber;
    private String sourceOfEnergy;
    private String equipment;
    private Integer ReplacementYear;
    private Integer referenceYear;
    private Integer replacementCost;
    private Integer totalEquipment;

    private Integer total;
    private Integer breakDown;
    private String workingStatus;
    private String status;
    private float purchasePrice;

    private String district;
    private String region;
    private String brand;
    private Integer regionId;
    private String model;
    private Integer Capacity;

    private String facilityName;
    private String facilityTypeName;
    private Integer facilityId;
    private Integer total_year1;
    private Integer total_year2;
    private Integer total_year3;
    private Integer total_year4;
    private Integer total_year5;
    private float this_year_cost;
    private Integer replacementYearOne;
    private Integer replacementYearTwo;
    private Integer replacementYearThree;
    private Integer replacementYearFour;
    private Integer replacementYearFive;




}
