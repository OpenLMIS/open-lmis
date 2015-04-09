/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.vaccine.repository.mapper.smt;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.vaccine.domain.*;
import org.openlmis.vaccine.domain.smt.InventoryBatch;
import org.openlmis.vaccine.domain.smt.InventoryTransaction;
import org.openlmis.vaccine.domain.smt.OnHand;
import org.openlmis.vaccine.domain.smt.TransactionType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Deprecated
public interface VaccineDistributionBatchMapper {
   @Insert("INSERT INTO inventory_transactions(transactiontypeid, fromfacilityid, tofacilityid, productid, \n" +
            "            dispatchreference, dispatchdate, bol, donorid, origincountryid, \n" +
            "            manufacturerid, statusid, purpose, vvmtracked, barcoded, gs1, \n" +
            "            quantity, packsize, unitprice, totalcost, locationid, expecteddate, \n" +
            "            arrivaldate, confirmedby, note,today, receivedAt,distributedTo, createdby, createddate, modifiedby, \n" +
            "            modifieddate) " +
            "VALUES (#{transactionType.id},#{fromFacility.id},#{toFacility.id},#{product.id}," +
            "#{dispatchReference},#{dispatchDate},#{bol},#{donor.id},#{originId}," +
            "#{manufacturer.id},#{status.id},#{purpose},#{vvmTracked},#{barCoded},#{gs1}," +
            "#{quantity},#{packSize},#{unitPrice},#{totalCost},#{storageLocation.id},#{expectedDate}," +
            "#{arrivalDate},#{confirmedBy.id},#{note},#{today},#{receivedAt},#{distributedTo},#{createdBy},COALESCE(#{createdDate}, NOW()),#{modifiedBy},COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP))")
    @Options(useGeneratedKeys = true)
    void insertInventoryTransaction(InventoryTransaction inventoryTransaction);

    @Update("UPDATE inventory_transactions  \n" +
            "SET    productid= #{product.id}, dispatchreference=#{dispatchReference}, dispatchdate=#{dispatchDate}, bol=#{bol}, donorid=#{donor.id}, \n" +
            "       origincountryid=#{originId}, manufacturerid=#{manufacturer.id}, statusid=#{status.id}, purpose=#{purpose}, vvmtracked=#{vvmTracked}, \n" +
            "       barcoded=#{barCoded}, gs1=#{gs1}, quantity=#{quantity}, packsize=#{packSize}, unitprice=#{unitPrice}, totalcost=#{totalCost}, \n" +
            "       locationid=#{storageLocation.id}, expecteddate=#{expectedDate}, arrivaldate=#{arrivalDate}, confirmedby=#{confirmedBy.id}, note=#{note}, \n" +
            "       today=#{today}, receivedAt = #{receivedAt},distributedTo=#{distributedTo}, createdby=#{createdBy}, createddate=COALESCE(#{createdDate}, NOW()), modifiedby=#{modifiedBy}, modifieddate=COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP)\n" +
            " WHERE id = #{id};")
    void updateInventoryTransaction(InventoryTransaction inventoryTransaction);

    @Insert("INSERT INTO inventory_batches(transactionid, batchnumber, manufacturedate, expirydate, \n" +
            "            quantity, vvm1_qty, vvm2_qty, vvm3_qty, vvm4_qty, note, createdby, \n" +
            "            createddate, modifiedby, modifieddate)\n" +
            "    VALUES (#{inventoryTransaction.id},#{batchNumber},#{productionDate},#{expiryDate},#{quantity},#{vvm1},#{vvm2},#{vvm3},#{vvm4},#{note}," +
            "#{createdBy},COALESCE(#{createdDate}, NOW()),#{modifiedBy},COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP))")
    @Options(useGeneratedKeys = true)
    void insertInventoryBatch(InventoryBatch inventoryBatch);

    @Update("UPDATE inventory_batches\n" +
            "   SET batchnumber=#{batchNumber}, manufacturedate=#{productionDate}, expirydate=#{expiryDate}, \n" +
            "       quantity=#{quantity}, vvm1_qty=#{vvm1}, vvm2_qty=#{vvm2}, vvm3_qty=#{vvm3}, vvm4_qty=#{vvm4}, note=#{note}, \n" +
            "       createdby=#{createdBy}, createddate=COALESCE(#{createdDate}, NOW()), modifiedby=#{modifiedBy}, modifieddate=COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP)\n" +
            " WHERE id=#{id}")
    void updateInventoryBatch(InventoryBatch inventoryBatch);

    @Insert("INSERT INTO on_hand(transactionid, transactiontypeid, productid, facilityid, \n" +
            "            batchnumber, quantity, vvm1_qty, vvm2_qty, vvm3_qty, vvm4_qty, \n" +
            "            note, createdby, createddate, modifiedby, modifieddate)\n" +
            "    VALUES (#{inventoryTransaction.id}, #{transactionType.id}, #{product.id},#{facility.id},#{inventoryBatch.id}," +
            "#{quantity},#{vvm1},#{vvm2},#{vvm3},#{vvm4},#{note},#{createdBy},COALESCE(#{createdDate}, NOW()),#{modifiedBy},COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP))")
    @Options(useGeneratedKeys = true)
    void insertOnHand(OnHand onHand);

    @Update("UPDATE on_hand\n" +
            "   SET productid=#{product.id}, facilityid=#{facility.id},\n" +
            "   quantity=#{quantity}, vvm1_qty=#{vvm1}, vvm2_qty=#{vvm2}, vvm3_qty=#{vvm3}, \n" +
            "       vvm4_qty=#{vvm4}, note=#{note}, modifiedby=#{modifiedBy}, \n" +
            "       modifieddate=COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP)\n" +
            " WHERE id = #{id}")
    void updateOnHand(OnHand onHand);

    @Delete("DELETE FROM on_hand where batchnumber = #{batchId}")
    void deleteOnHandForBatchId(Long batchId);

    @Select("select * from inventory_transactions where toFacilityId = #{toFacilityId} ")
    @Results({
            @Result(property = "transactionType", javaType = TransactionType.class, column = "transactionTypeId",
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.smt.TransactionTypeMapper.getById")),
            @Result(property = "product", javaType = Product.class, column = "productId",
                    one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById")),
            @Result(property = "toFacility", javaType = Facility.class, column = "toFacilityId",
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
            @Result(property = "fromFacility", javaType = Facility.class, column = "fromFacilityId",
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
            @Result(property = "status", javaType = Status.class, column = "statusId",
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.smt.StatusMapper.getById"))
    })
    List<InventoryTransaction> getInventoryTransactionsByReceivingFacility(Long toFacilityId);

    @Select("select * from inventory_transactions where id = #{id} ")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "transactionType", javaType = TransactionType.class, column = "transactionTypeId",
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.smt.TransactionTypeMapper.getById")),
            @Result(property = "product", javaType = Product.class, column = "productId",
                    one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById")),
            @Result(property = "toFacility", javaType = Facility.class, column = "toFacilityId",
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
            @Result(property = "fromFacility", javaType = Facility.class, column = "fromFacilityId",
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
            @Result(property = "status", javaType = Status.class, column = "statusId",
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.smt.StatusMapper.getById")),
            @Result(property = "manufacturer.id", column = "manufacturerId"),
            @Result(property = "originId", column = "originCountryId" ),
            @Result(property = "donor.id", column = "donorId"),
            @Result(property = "storageLocation.id", column = "locationId"),
            @Result(property = "inventoryBatches", javaType = List.class, column = "id",
                    many = @Many(select = "org.openlmis.vaccine.repository.mapper.smt.VaccineDistributionBatchMapper.getBatchesByTransactionId"))
    })
    InventoryTransaction getInventoryTransactionsById(Long id);

    @Select("WITH tempBatch AS(\n" +
            "            SELECT ib.*, inv.vvmtracked \n" +
            "            FROM inventory_transactions inv\n" +
            "            INNER JOIN Inventory_batches ib on inv.id = ib.transactionId\n" +
            "            WHERE inv.productId =#{productId} AND COALESCE(vvmtracked, false) = true and (COALESCE(ib.vvm1_qty,0) + COALESCE(ib.vvm2_qty,0)) > 0 and statusId in (select id from received_status  where name in ('QA inspected','Put to storage'))\n" +
            "            UNION ALL\n" +
            "            SELECT ib.*, inv.vvmtracked \n" +
            "            FROM inventory_transactions inv\n" +
            "            INNER JOIN Inventory_batches ib on inv.id = ib.transactionId\n" +
            "            WHERE inv.productId =#{productId} AND COALESCE(vvmtracked, false) = false and (COALESCE(ib.vvm1_qty,0) + COALESCE(ib.vvm2_qty,0) + COALESCE(ib.vvm3_qty,0) + COALESCE(ib.vvm4_qty,0)) = 0\n" +
            "            and  statusId in (select id from received_status  where name in ('QA inspected','Put to storage')))\n" +
            "            SELECT * FROM tempBatch order by tempBatch.expirydate")
    @Results({
            @Result(property = "vvm1", column = "vvm1_qty"),
            @Result(property = "vvm2", column = "vvm2_qty"),
            @Result(property = "vvm3", column = "vvm3_qty"),
            @Result(property = "vvm4", column = "vvm4_qty"),
            @Result(property = "productionDate", column = "manufactureDate"),
            @Result(property = "inventoryTransaction.vvmTracked", column = "vvmTracked", javaType = Boolean.class)

    })
    List<InventoryBatch> getUsableBatches(Long productId);

    @Select("SELECT * from inventory_batches where transactionId = #{transactionId}")
    @Results({
            @Result(property = "vvm1", column = "vvm1_qty"),
            @Result(property = "vvm2", column = "vvm2_qty"),
            @Result(property = "vvm3", column = "vvm3_qty"),
            @Result(property = "vvm4", column = "vvm4_qty"),
            @Result(property = "productionDate", column = "manufactureDate")
    })
    List<InventoryBatch> getBatchesByTransactionId(Long transactionId);


}
