/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
