/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.vaccine.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.DistributionBatch;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineDistributionBatchMapper {
    @Select("select * from vaccine_distribution_batches where batchnumber = #{batchId}")
    List<DistributionBatch> getByBatchId(@Param("batchNumber") String batchId);

    @Select("select * from vaccine_distribution_batches")
    List<DistributionBatch> getAll();

    @Insert("INSERT INTO vaccine_distribution_batches(" +
            " batchid, expirydate, productiondate, manufacturerid, donorid, \n" +
            "            receivedate, productcode, fromfacilityid, tofacilityid, distributiontypeid, \n" +
            "            vialsperbox, boxlength, boxwidth, boxheight, unitcost, totalcost, \n" +
            "            purposeid, createdby, createddate, modifiedby, modifieddate)  " +
            "VALUES (  " +
            "  #{batchId},  " +
            "  #{expiryDate},  " +
            "  #{productionDate},  " +
            "  #{manufacturer.id},  " +
            "  #{donor.id},  " +
            "  #{receiveDate},  " +
            "  #{product.code},  " +
            "  #{fromFacility.id},  " +
            "  #{toFacility.id},  " +
            "  #{distributionTypeId},  " +
            "  #{vialsPerBox},   " +
            "  #{boxLength},   " +
            "  #{boxWidth},   " +
            "  #{boxHeight},   " +
            "  #{unitCost},   " +
            "  #{totalCost},   " +
            "  #{purposeId},   " +
            "  #{createdBy},   " +
            "  COALESCE(#{createdDate}, NOW()),   " +
            "  #{modifiedBy},   " +
            "  COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP)  " +
            ")  ")
    @Options(useGeneratedKeys = true)
    void insert(DistributionBatch distributionBatch);

    @Update("UPDATE vaccine_distribution_batches\n" +
            "   SET batchid=#{batchId}, expirydate=#{expiryDate}, productiondate=#{productionDate}, manufacturerid=#{manufacturer.id}, \n" +
            "       donorid=#{donor.id}, receivedate=#{receiveDate}, productcode=#{product.code}, fromfacilityid=#{fromFacility.id}, tofacilityid=#{toFacility.id}, \n" +
            "       distributiontypeid=#{distributiontypeId}, vialsperbox=#{vialsPerBox}, boxlength=#{boxLength}, boxwidth=#{boxWidth}, \n" +
            "       boxheight=#{boxHeight}, unitcost=#{unitCost}, totalcost=#{totalCost}, purposeid=#{purposeId}, createdby=#{createdBy}, \n" +
            "       createddate= COALESCE(#{createdDate}, NOW()), modifiedby=#{modifiedBy}, modifieddate=COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP)\n" +
            " WHERE id=#{id}")
    void update(DistributionBatch distributionBatch);
}
