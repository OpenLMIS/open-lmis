/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.serializer.DateDeserializer;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * ProgramSupported represents the Program supported by a Facility. Defines the contract for upload of such mapping like program code,
 * facility code and if program is active for facility are mandatory for such mapping. It also provides methods to calculate
 * whoRatio, packSize etc for a given ProgramSupported.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProgramSupported extends BaseModel implements Importable {

    private Long facilityId;

    @ImportField(mandatory = true, name = "Program Code", nested = "code")
    private Program program;

    @ImportField(mandatory = true, name = "Facility Code")
    private String facilityCode;

    @ImportField(mandatory = true, name = "Program Is Active", type = "boolean")
    private Boolean active = false;

    @ImportField(name = "Program Start Date", type = "Date")
    @JsonDeserialize(using = DateDeserializer.class)
    private Date startDate;

    @ImportField(name = "Report Type Code", nested = "code")
    private ReportType reportType;

    @ImportField(name = "Report Start Date", type = "Date")
    @JsonDeserialize(using = DateDeserializer.class)
    private Date reportStartDate;

    @ImportField(name = "Report Active", type = "boolean")
    private boolean reportActive;

    private List<FacilityProgramProduct> programProducts;

    public void isValid(List<ReportType> reportTypes) {
        if (this.active && this.startDate == null) {
            throw new DataException("supported.programs.invalid");
        }

        if (reportType != null) {

            if (!isMatch(reportTypes)) {
                throw new DataException("supported.programs.report.type.match");
            }

            if (!this.active && this.reportActive) {
                throw new DataException("supported.programs.report.type.active");
            }

            if (this.active && this.reportActive && this.reportStartDate == null) {
                throw new DataException("supported.programs.report.type.invalid ");
            }

            if (this.active && this.reportActive && this.startDate.after(this.reportStartDate)) {
                throw new DataException("supported.programs.report.type.start.time.invalid");
            }


        }

    }

    private boolean isMatch(List<ReportType> reportTypes) {
        for (ReportType reportType : reportTypes) {
            if (reportType.getId() == this.reportType.id &&
                    reportType.getProgram().getId() == this.program.getId()) {
                return true;
            }

        }

        return false;
    }

    public ProgramSupported(Long programId, Boolean active, Date startDate) {

        this.program = new Program(programId);
        this.active = active;
        this.startDate = startDate;
    }

    @SuppressWarnings("unused")
    public String getStringStartDate() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return this.startDate == null ? null : simpleDateFormat.format(this.startDate);
    }

    @SuppressWarnings("unused")
    public String getStringReportTypeDate() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return this.reportStartDate == null ? null : simpleDateFormat.format(this.reportStartDate);
    }

    @JsonIgnore
    public Double getWhoRatioFor(final String productCode) {
        FacilityProgramProduct facilityProgramProduct = (FacilityProgramProduct) CollectionUtils.find(this.getProgramProducts(), new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                return ((FacilityProgramProduct) o).getProduct().getCode().equals(productCode);
            }
        });
        return (facilityProgramProduct == null) ? null : facilityProgramProduct.getWhoRatio();
    }

    @JsonIgnore
    public Integer getPackSizeFor(final String productCode) {
        FacilityProgramProduct facilityProgramProduct = (FacilityProgramProduct) CollectionUtils.find(this.getProgramProducts(), new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                return ((FacilityProgramProduct) o).getProduct().getCode().equals(productCode);
            }
        });
        return (facilityProgramProduct == null) ? null : facilityProgramProduct.getProduct().getPackSize();
    }
}
