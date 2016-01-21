/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
package org.openlmis.vaccine.domain.reports;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BundledDistributionVaccinationSupplies   {
    private String zone_name;
    private  String region_name;
    private String district_name;
    private  Long population;
    private Long jan_rec;
    private Long feb_rec;
    private Long mar_rec;
    private Long apr_rec;
    private Long may_rec;
    private Long jun_rec;
    private Long jul_rec;
    private Long aug_rec;
    private Long sep_rec;
    private Long oct_rec;
    private Long nov_rec;
    private Long dec_rec;
    private Long jan_issued;
    private Long feb_issued;
    private Long mar_issued;
    private Long apr_issued;
    private Long may_issued;
    private Long jun_issued;
    private Long jul_issued;
    private Long aug_issued;
    private Long sep_issued;
    private Long oct_issued;
    private Long nov_issued;
    private Long dec_issued;
}
