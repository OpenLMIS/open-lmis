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

package org.openlmis.vaccine.repository.reports;/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

import org.apache.ibatis.annotations.Param;
import org.openlmis.core.domain.Product;
import org.openlmis.vaccine.domain.reports.DropoutProduct;
import org.openlmis.vaccine.domain.reports.PerformanceByDropoutRateByDistrict;
import org.openlmis.vaccine.domain.reports.params.PerformanceByDropoutRateParam;
import org.openlmis.vaccine.repository.mapper.reports.PerformanceByDropoutRateByDistrictMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Component
public class PerformanceByDropoutRateByDistrictRepository {
    @Autowired
    PerformanceByDropoutRateByDistrictMapper mapper;

    public List<PerformanceByDropoutRateByDistrict> loadPerformanceByDropoutRateDistrictReports(PerformanceByDropoutRateParam filterCriteria) {
        List<PerformanceByDropoutRateByDistrict> performanceByDropoutRateByDistrictList = null;
        performanceByDropoutRateByDistrictList = mapper.loadPerformanceByDropoutRateDistrictReports(filterCriteria);
        return performanceByDropoutRateByDistrictList;
    }
    public List<PerformanceByDropoutRateByDistrict> loadPerformanceByDropoutRateFacillityReports(PerformanceByDropoutRateParam filterCriteria) {
        List<PerformanceByDropoutRateByDistrict> performanceByDropoutRateByDistrictList = null;
        performanceByDropoutRateByDistrictList = mapper.loadPerformanceByDropoutRateFacilityReports(filterCriteria);
        return performanceByDropoutRateByDistrictList;
    }
    public List<PerformanceByDropoutRateByDistrict> loadPerformanceByDropoutRateRegionReports(PerformanceByDropoutRateParam filterCriteria) {
        List<PerformanceByDropoutRateByDistrict> performanceByDropoutRateByDistrictList = null;
        performanceByDropoutRateByDistrictList = mapper.loadPerformanceByDropoutRateRegionReports(filterCriteria);
        return performanceByDropoutRateByDistrictList;
    }
    public boolean isDistrictLevel(Long goegraphicZoneId){

        return goegraphicZoneId!=0 &&mapper.isDistrictLevel(goegraphicZoneId)>0;
}
    public List<DropoutProduct> loadDropoutProductList(){
        List<DropoutProduct> productList=null;
        productList= this.mapper.loadDropoutProductList();
        return  productList;
    }
}
