/*
 *
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 *  Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openlmis.vaccine.domain.reports;/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BundledDistributionVaccinationSupplyRegion {

    /*


   recieved
    */
    /*
    jan
     */
    private long jan_rec_district_less_lower_limit;
    private long feb_rec_district_less_lower_limit;
    private long mar_rec_district_less_lower_limit;
    private long apr_rec_district_less_lower_limit;
    private long may_rec_district_less_lower_limit;
    private long jun_rec_district_less_lower_limit;
    private long jul_rec_district_less_lower_limit;
    private long aug_rec_district_less_lower_limit;
    private long sep_rec_district_less_lower_limit;
    private long oct_rec_district_less_lower_limit;
    private long nov_rec_district_less_lower_limit;
    private long dec_rec_district_less_lower_limit;
    /*
    recieve greater upper limit
     */
    private long jan_rec_district_greater_upper_limit;
    private long feb_rec_district_greater_upper_limit;
    private long mar_rec_district_greater_upper_limit;
    private long apr_rec_district_greater_upper_limit;
    private long may_rec_district_greater_upper_limit;
    private long jun_rec_district_greater_upper_limit;
    private long jul_rec_district_greater_upper_limit;
    private long aug_rec_district_greater_upper_limit;
    private long sep_rec_district_greater_upper_limit;
    private long oct_rec_district_greater_upper_limit;
    private long nov_rec_district_greater_upper_limit;
    private long dec_rec_district_greater_upper_limit;
        /*
    issue less
     */

    private long jan_issued_district_less_lower_limit;
    private long feb_issued_district_less_lower_limit;
    private long mar_issued_district_less_lower_limit;
    private long apr_issued_district_less_lower_limit;
    private long may_issued_district_less_lower_limit;
    private long jun_issued_district_less_lower_limit;
    private long jul_issued_district_less_lower_limit;
    private long aug_issued_district_less_lower_limit;
    private long sep_issued_district_less_lower_limit;
    private long oct_issued_district_less_lower_limit;
    private long nov_issued_district_less_lower_limit;
    private long dec_issued_district_less_lower_limit;

    /*
    issue greater
     */
    private long jan_issued_district_greater_upper_limit;
    private long feb_issued_district_greater_upper_limit;
    private long mar_issued_district_greater_upper_limit;
    private long apr_issued_district_greater_upper_limit;
    private long may_issued_district_greater_upper_limit;
    private long jun_issued_district_greater_upper_limit;
    private long jul_issued_district_greater_upper_limit;
    private long aug_issued_district_greater_upper_limit;
    private long sep_issued_district_greater_upper_limit;
    private long oct_issued_district_greater_upper_limit;
    private long nov_issued_district_greater_upper_limit;
    private long dec_issued_district_greater_upper_limit;
}
