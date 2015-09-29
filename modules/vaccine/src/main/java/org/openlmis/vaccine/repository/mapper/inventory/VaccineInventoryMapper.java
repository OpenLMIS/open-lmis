package org.openlmis.vaccine.repository.mapper.inventory;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;

import org.openlmis.vaccine.domain.inventory.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineInventoryMapper {

    @Select("SELECT *" +
            " FROM stock_cards" +
            " WHERE facilityid = #{facilityId}" +
            "   AND productid = #{productId}")
    StockCard getStockCardByFacilityAndProduct(@Param("facilityId") Long facilityId, @Param("productId") Long productId);

    @Select("SELECT *" +
            " FROM stock_cards" +
            " WHERE id = #{id}")
    StockCard getStockCardById(@Param("id") Long id);

    @Select("SELECT *" +
            " FROM stock_cards" +
            " WHERE facilityid = #{facilityId}")
    List<StockCard> getAllStockCardsByFacility(@Param("facilityId") Long facilityId);


    @Select("SELECT *" +
            " FROM lots_on_hand" +
            " WHERE stockcardid = #{stockCardId} AND lotid= #{lotId} ")
    LotOnHand getLotOnHand(@Param("stockCardId") Long stockCardId, @Param("lotId") Long lotId);

    @Update("update stock_cards " +
            " set " +
            " totalquantityonhand = #{totalQuantityOnHand}," +
            " effectivedate= NOW()," +
            " modifiedBy = #{modifiedBy}," +
            " modifiedDate = NOW()" +
            " where id = #{id} ")
    Integer updateStockCard(StockCard stockCard);

    @Update("update lots_on_hand " +
            " set " +
            " quantityonhand = #{quantityOnHand}," +
            " modifiedBy = #{modifiedBy}," +
            " modifiedDate = NOW()," +
            " effectivedate =NOW()" +
            " where id = #{id}")
    Integer updateLotOnHand(LotOnHand lotOnHand);

    @Select("SELECT *" +
            " FROM lots" +
            " WHERE lotnumber = #{lotCode} ")
    @Results({
            @Result(property = "lotCode", column = "lotnumber"),
    })
    Lot getLotByCode(@Param("lotCode") String lotCode);

    @Insert("insert into lots " +
            " (productid, lotnumber, manufacturername, manufacturedate, expirationdate, createdby, createddate, modifiedby,modifieddate )" +
            " values " +
            " (#{productId}, #{lotCode}, #{manufacturerName}, #{manufactureDate}, #{expirationDate},#{createdBy},NOW(),#{modifiedBy},NOW()) ")
    @Options(useGeneratedKeys = true)
    Integer insertLot(Lot lot);

    @Insert("insert into stock_cards " +
            " (facilityid, productid, totalquantityonhand, effectivedate, notes, createdby, createddate, modifiedby,modifieddate )" +
            " values " +
            " (#{facilityId}, #{productId}, #{totalQuantityOnHand}, NOW(), #{notes},#{createdBy},NOW(),#{modifiedBy},NOW()) ")
    @Options(useGeneratedKeys = true)
    Integer insertStockCard(StockCard stockCard);

    @Insert("insert into lots_on_hand " +
            " (stockcardid, lotid, quantityonhand, effectivedate, createdby, createddate, modifiedby,modifieddate )" +
            " values " +
            " (#{stockCardId}, #{lotId}, #{quantityOnHand}, NOW(),#{createdBy},NOW(),#{modifiedBy},NOW()) ")
    @Options(useGeneratedKeys = true)
    Integer insertLotsOnHand(LotOnHand lotOnHand);

    @Insert("insert into stock_card_entries " +
            " (stockcardid, lotonhandid, type, quantity,  createdby, createddate, modifiedby,modifieddate )" +
            " values " +
            " (#{stockCardId}, #{lotOnHandId}, #{type}, #{quantity},#{createdBy},NOW(),#{modifiedBy},NOW()) ")
    @Options(useGeneratedKeys = true)
    Integer insertStockCardEntry(StockCardEntry stockCardEntry);

    @Insert("insert into vaccine_lots_on_hand_vvm " +
            " (lotonhandid, vvmstatus,effectivedate) " +
            " values " +
            " (#{lotOnHandId},#{vvmStatus},NOW()) ")
    Integer insertVVM(@Param("lotOnHandId") Long lotOnHandId, @Param("vvmStatus") Integer vvmStatus);

    @Update("update vaccine_lots_on_hand_vvm " +
            " set" +
            " vvmstatus=#{vvmStatus}," +
            " effectivedate=NOW() " +
            " where lotonhandid=#{lotOnHandId}"
    )
    Integer updateVVM(@Param("lotOnHandId") Long lotOnHandId, @Param("vvmStatus") Integer vvmStatus);

    @Insert("insert into vaccine_lots_on_hand_adjustments " +
            " (lotonhandid, adjustmentreason,quantity,effectivedate, createdby, createddate, modifiedby, modifieddate) " +
            " values " +
            " (#{lotOnHandId},#{name},#{quantity},NOW(),#{createdBy},NOW(),#{modifiedBy},NOW())")
    Integer insertAdjustmentReasons(AdjustmentReason adjustmentReason);

    @Select("SELECT *" +
            " FROM lots" +
            " WHERE productid = #{productId} ")
    @Results({
            @Result(property = "lotCode", column = "lotnumber"),
    })
    List<Lot> getLotsByProductId(@Param("productId") Long productId);

}
