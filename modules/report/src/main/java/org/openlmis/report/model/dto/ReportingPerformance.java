package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User: Issa
 * Date: 5/22/14
 * Time: 5:50 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportingPerformance {

    private String name;

    private String mainPhone;

    private String status;

    private String district;

    private Boolean hasContacts;
}
