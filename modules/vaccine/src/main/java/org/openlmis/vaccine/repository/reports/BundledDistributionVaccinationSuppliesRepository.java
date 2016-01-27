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

package org.openlmis.vaccine.repository.reports;
import org.openlmis.vaccine.domain.reports.BundledDistributionVaccinationSupplies;
import org.openlmis.vaccine.domain.reports.BundledDistributionVaccinationSupplyDistrict;
import org.openlmis.vaccine.domain.reports.BundledDistributionVaccinationSupplyRegion;
import org.openlmis.vaccine.repository.mapper.reports.BundledDistributionVaccinationSuppliesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class BundledDistributionVaccinationSuppliesRepository {
    @Autowired
    private BundledDistributionVaccinationSuppliesMapper vaccinationSuppliesMapper;
  public  List<BundledDistributionVaccinationSupplies> getBundledDistributionVaccinationSupplies(Long year, Long productId){
      List<BundledDistributionVaccinationSupplies> vaccinationSuppliesList=null;
      System.out.println(" year and product id is "+ year + " "+ productId);
      vaccinationSuppliesList=vaccinationSuppliesMapper.getBundledDistributionVaccinationSupplies(year,productId);
      System.out.println(" year and product id is "+ year + " "+ productId + "  "+vaccinationSuppliesList.size());
      return vaccinationSuppliesList;
    }
    public  BundledDistributionVaccinationSupplyDistrict getBundledDistributionVaccinationSuppliesDistrictSummary(Long year, Long productId){
        BundledDistributionVaccinationSupplyDistrict vaccinationSupplyDistrict=null;
        System.out.println(" year and product id is "+ year + " "+ productId);
        vaccinationSupplyDistrict=vaccinationSuppliesMapper.getBundledDistributionVaccinationSuppliesDistrictSummary(year,productId);

        return vaccinationSupplyDistrict;
    }
    public BundledDistributionVaccinationSupplyRegion getBundledDistributionVaccinationSuppliesRegionSummary(Long year, Long productId){
        BundledDistributionVaccinationSupplyRegion vaccinationSupplyRegion=null;
        System.out.println(" year and product id is "+ year + " "+ productId);
        vaccinationSupplyRegion=vaccinationSuppliesMapper.getBundledDistributionVaccinationSuppliesRegionSummary(year,productId);

        return vaccinationSupplyRegion;
    }

}
