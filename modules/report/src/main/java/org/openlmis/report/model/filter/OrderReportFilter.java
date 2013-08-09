package org.openlmis.report.model.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

import java.text.DateFormat;
import java.util.Date;

/**
 * User: Wolde
 * Date: 8/5/13
 * Time: 6:32 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderReportFilter implements ReportData {

    private String periodType;
    private int yearFrom;
    private int yearTo;
    private int monthFrom;
    private int monthTo;
    private Date startDate;
    private Date endDate;
    private int quarterFrom;
    private int quarterTo;
    private int semiAnnualFrom;
    private int semiAnnualTo;

    private int facilityTypeId;
    private String facilityType;
    private String facility;
    private int facilityId;
    private int productId;
    private int productCategoryId;
    private int rgroupId;
    private String rgroup;
    private int programId;
    private String orderType;
    private int zoneId;

    @Override
    public String toString(){
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT);

        StringBuilder filtersValue = new StringBuilder("");
        filtersValue.append("Period : ").append(dateFormatter.format(this.getStartDate())).append("-").append(dateFormatter.format(this.getEndDate())).append("\n").
                append("Facility Types : ").append(this.getFacilityType()).append("\n").
                append("Reporting Groups : ").append(this.getRgroup());

        return filtersValue.toString();
    }



}
