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
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.vaccine.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface VaccineDistributionBatchMapper {
    @Select("select * from vaccine_distribution_batches where dispatchId = #{dispatchId}")
    List<DistributionBatch> getByDispatchId(@Param("dispatchId") String dispatchId);

    @Select("select * from vaccine_distribution_batches where id = #{id}")
    @Results({
            @Result(property = "donor.id", column = "donorId"),
            @Result(property = "product.code", column = "productCode"),
            @Result(property = "toFacility.id", column = "toFacilityId"),
            @Result(property = "fromFacility.id", column = "fromFacilityId"),
            @Result(property = "manufacturer.id", column = "manufacturerId")
    })
    DistributionBatch getById(@Param("id") Long id);

    @Select("select * from vaccine_distribution_batches")
    @Results({
            @Result(property = "donor", javaType = Donor.class, column = "donorId",
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.DonorMapper2.getById")),
            @Result(property = "product", javaType = Product.class, column = "productCode",
                    one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getByCode")),
            @Result(property = "toFacility", javaType = Facility.class, column = "toFacilityId",
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
            @Result(property = "fromFacility", javaType = Facility.class, column = "fromFacilityId",
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))
    })
    List<DistributionBatch> getAll();

    @Select("select * from vaccine_distribution_batches where dispatchId like LOWER(#{query})||'%' ")
    @Results({
            @Result(property = "donor", javaType = Donor.class, column = "donorId",
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.DonorMapper2.getById")),
            @Result(property = "product", javaType = Product.class, column = "productCode",
                    one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getByCode")),
            @Result(property = "toFacility", javaType = Facility.class, column = "toFacilityId",
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
            @Result(property = "fromFacility", javaType = Facility.class, column = "fromFacilityId",
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))
    })
    List<DistributionBatch> searchDistributionBatches(@Param("query")String query);


    @Select("select * from vaccine_distribution_batches where dispatchId like LOWER(#{query})||'%' ")
    @Results({
            @Result(property = "donor", javaType = Donor.class, column = "donorId",
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.DonorMapper2.getById")),
            @Result(property = "product", javaType = Product.class, column = "productCode",
                    one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getByCode")),
            @Result(property = "toFacility", javaType = Facility.class, column = "toFacilityId",
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
            @Result(property = "fromFacility", javaType = Facility.class, column = "fromFacilityId",
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))
    })
    List<Map<String, Object>> filterDistributionBatches(@Param("query")String query);

    @Insert("INSERT INTO vaccine_distribution_batches(" +
            " dispatchId, batchid,originid, expirydate, productiondate, manufacturerid, donorid, \n" +
            "            receivedate,recalldate, productcode,voucherNumber, fromfacilityid, tofacilityid, distributiontypeid, \n" +
            "            vialsperbox, boxlength, boxwidth, boxheight, unitcost, totalcost, \n" +
            "            purposeid,freight, createdby, createddate, modifiedby, modifieddate)  " +
            "VALUES (  " +
            "  #{dispatchId},  " +
            "  #{batchId},  " +
            "  #{originId},  " +
            "  #{expiryDate},  " +
            "  #{productionDate},  " +
            "  #{manufacturer.id},  " +
            "  #{donor.id},  " +
            "  #{receiveDate},  " +
            "  #{recallDate},  " +
            "  #{product.code},  " +
            "  #{voucherNumber},  " +
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
            "  #{freight},   " +
            "  #{createdBy},   " +
            "  COALESCE(#{createdDate}, NOW()),   " +
            "  #{modifiedBy},   " +
            "  COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP)  " +
            ")  ")
    @Options(useGeneratedKeys = true)
    void insert(DistributionBatch distributionBatch);

    @Update("UPDATE vaccine_distribution_batches\n" +
            "   SET batchid=#{batchId}, dispatchId=#{dispatchId}, expirydate=#{expiryDate}, productiondate=#{productionDate}, manufacturerid=#{manufacturer.id}, \n" +
            "       donorid=#{donor.id}, receivedate=#{receiveDate},recalldate=#{recallDate}, productcode=#{product.code},voucherNumber=#{voucherNumber}, fromfacilityid=#{fromFacility.id}, tofacilityid=#{toFacility.id}, \n" +
            "       distributiontypeid=#{distributionTypeId}, vialsperbox=#{vialsPerBox}, boxlength=#{boxLength}, boxwidth=#{boxWidth}, \n" +
            "       boxheight=#{boxHeight}, unitcost=#{unitCost}, totalcost=#{totalCost}, purposeid=#{purposeId},freight=#{freight}, createdby=#{createdBy}, \n" +
            "       createddate= COALESCE(#{createdDate}, NOW()), modifiedby=#{modifiedBy}, modifieddate=COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP)\n" +
            " WHERE id=#{id}")
    void update(DistributionBatch distributionBatch);

    @Insert("INSERT INTO inventory_transactions(transactiontypeid, fromfacilityid, tofacilityid, productid, \n" +
            "            dispatchreference, dispatchdate, bol, donorid, origincountryid, \n" +
            "            manufacturerid, statusid, purpose, vvmtracked, barcoded, gs1, \n" +
            "            quantity, packsize, unitprice, totalcost, locationid, expecteddate, \n" +
            "            arrivaldate, confirmedby, note, createdby, createddate, modifiedby, \n" +
            "            modifieddate) " +
            "VALUES (#{transactionType.id},#{fromFacility.id},#{toFacility.id},#{product.id}," +
            "#{dispatchReference},#{dispatchDate},#{bol},#{donor.id},#{originId}," +
            "#{manufacturer.id},#{status.id},#{purpose},#{vvmTracked},#{barCoded},#{gs1}," +
            "#{quantity},#{packSize},#{unitPrice},#{totalCost},#{storageLocation.id},#{expectedDate}," +
            "#{arrivalDate},#{confirmedBy.id},#{note},#{createdBy},COALESCE(#{createdDate}, NOW()),#{modifiedBy},COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP))")
    @Options(useGeneratedKeys = true)
    void insertInventoryTransaction(InventoryTransaction    inventoryTransaction);

    @Insert("INSERT INTO inventory_batches(transactionid, batchnumber, manufacturedate, expirydate, \n" +
            "            quantity, vvm1_qty, vvm2_qty, vvm3_qty, vvm4_qty, note, createdby, \n" +
            "            createddate, modifiedby, modifieddate)\n" +
            "    VALUES (#{inventoryTransaction.id},#{batchNumber},#{productionDate},#{expiryDate},#{quantity},#{vvm1},#{vvm2},#{vvm3},#{vvm4},#{note}," +
            "#{createdBy},COALESCE(#{createdDate}, NOW()),#{modifiedBy},COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP))")
    @Options(useGeneratedKeys = true)
    void insertInventoryBatch(InventoryBatch inventoryBatch);

    @Insert("INSERT INTO on_hand(transactionid, transactiontypeid, productid, facilityid, \n" +
            "            batchnumber, quantity, vvm1_qty, vvm2_qty, vvm3_qty, vvm4_qty, \n" +
            "            note, createdby, createddate, modifiedby, modifieddate)\n" +
            "    VALUES (#{inventoryTransaction.id}, #{transactionType.id}, #{product.id},#{facility.id},#{inventoryBatch.id}," +
            "#{quantity},#{vvm1},#{vvm2},#{vvm3},#{vvm4},#{note},#{createdBy},COALESCE(#{createdDate}, NOW()),#{modifiedBy},COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP))")
    @Options(useGeneratedKeys = true)
    void insertOnHand(OnHand onHand);

    @Select("select * from inventory_transactions where toFacilityId = #{toFacilityId} ")
    @Results({
            @Result(property = "transactionType", javaType = TransactionType.class, column = "transactionTypeId",
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.TransactionTypeMapper.getById")),
            @Result(property = "product", javaType = Product.class, column = "productId",
                    one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById")),
            @Result(property = "toFacility", javaType = Facility.class, column = "toFacilityId",
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
            @Result(property = "fromFacility", javaType = Facility.class, column = "fromFacilityId",
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
            @Result(property = "status", javaType = Status.class, column = "statusId",
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.StatusMapper.getById"))
    })
    List<InventoryTransaction> getInventoryTransactionsByReceivingFacility(Long toFacilityId);


}
