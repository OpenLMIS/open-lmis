package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;

import java.util.Date;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL;


/**
 * Created with IntelliJ IDEA.
 * User: mahmed
 * Date: 6/19/13
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates..
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize


public class ProgramProductPriceList {

    private Integer id;
    private Integer programid;
    private Integer productid;
    private Integer programproductid;
    private String programname;
    private Integer priceperpack;
    private Integer priceperdosage;
    private Date  startdate;
    private Date  enddate;
    private String  source;
}
