package org.openlmis.stockmanagement.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.StockAdjustmentReason;
import org.openlmis.stockmanagement.domain.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StockCardMapper {

  @Select("SELECT *" +
      " FROM stock_cards" +
      " WHERE facilityid = #{facilityId}")
  @Results({
      @Result(property = "id", column = "id"),
      @Result(property = "facility", column = "facilityId", javaType = Facility.class,
          one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
      @Result(property = "product", column = "productId", javaType = Product.class,
          one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById")),
      @Result(property = "lotsOnHand", column = "id", javaType = List.class,
          many = @Many(select = "getLotsOnHand"))
  })
  List<StockCard> queryStockCardBasicInfo(@Param("facilityId") Long facilityId);

  @Select("SELECT *" +
      " FROM stock_cards" +
      " WHERE facilityid = #{facilityId}" +
      "   AND productid = (SELECT id FROM products WHERE code = #{productCode})")
  @Results({
      @Result(property = "id", column = "id"),
      @Result(property = "facility", column = "facilityId", javaType = Facility.class,
          one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
      @Result(property = "product", column = "productId", javaType = Product.class,
          one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById")),
      @Result(property = "entries", column = "id", javaType = List.class,
          many = @Many(select = "getEntries")),
      @Result(property = "lotsOnHand", column = "id", javaType = List.class,
          many = @Many(select = "getLotsOnHand"))
  })
  StockCard getByFacilityAndProduct(@Param("facilityId") Long facilityId, @Param("productCode") String productCode);

  @Select("SELECT *" +
      " FROM stock_cards" +
      " WHERE facilityid = #{facilityId}" +
      "   AND id = #{id}")
  @Results({
      @Result(property = "id", column = "id"),
      @Result(property = "facility", column = "facilityId", javaType = Facility.class,
          one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
      @Result(property = "product", column = "productId", javaType = Product.class,
          one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById")),
      @Result(property = "entries", column = "id", javaType = List.class,
          many = @Many(select = "getEntries")),
      @Result(property = "lotsOnHand", column = "id", javaType = List.class,
          many = @Many(select = "getLotsOnHand"))
  })
  StockCard getByFacilityAndId(@Param("facilityId") Long facilityId, @Param("id") Long id);

  @Select("SELECT *" +
      " FROM stock_cards" +
      " WHERE facilityid = #{facilityId}")
  @Results({
      @Result(property = "id", column = "id"),
      @Result(property = "product", column = "productId", javaType = Product.class,
          one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById")),
      @Result(property = "entries", column = "id", javaType = List.class,
          many = @Many(select = "getEntries")),
      @Result(property = "lotsOnHand", column = "id", javaType = List.class,
          many = @Many(select = "getLotsOnHand"))
  })
  List<StockCard> getAllByFacility(@Param("facilityId") Long facilityId);

  @Select("SELECT scekv.keycolumn AS key" +
      ", scekv.valuecolumn AS value" +
      ", scekv.modifieddate AS synceddate" +
      " FROM stock_card_entries sce" +
      "   JOIN stock_card_entry_key_values scekv ON scekv.stockcardentryid = sce.id" +
      " WHERE stockcardid = #{stockCardId}")
  List<StockCardEntryKV> getStockCardKeyValues(@Param("stockCardId") Long stockCardId);

  @Select("SELECT keycolumn AS key" +
      ", valuecolumn AS value" +
      ", modifieddate AS synceddate" +
      " FROM stock_card_entry_key_values" +
      " WHERE stockcardentryid = #{stockCardEntryId}")
  List<StockCardEntryKV> getStockCardEntryExtensionAttributes(@Param("stockCardEntryId") Long stockCardEntryId);


  @Select("SELECT *" +
      " FROM stock_card_entries" +
      " WHERE stockcardid = #{stockCardId}" +
      " ORDER BY createddate DESC")
  @Results({
      @Result(property = "id", column = "id"),
      @Result(property = "adjustmentReason", column = "adjustmentType", javaType = StockAdjustmentReason.class,
          one = @One(select = "org.openlmis.core.repository.mapper.StockAdjustmentReasonMapper.getByName")),
      @Result(property = "extensions", column = "id", javaType = List.class,
          many = @Many(select = "getStockCardEntryExtensionAttributes"))
  })
  List<StockCardEntry> getEntries(@Param("stockCardId") Long stockCardId);

  @Select("SELECT * FROM stock_card_entries" +
      " WHERE stockcardid = #{stockCardId}" +
      " AND occurred >= #{startDate}" +
      " AND occurred < #{endDate}" +
      " ORDER BY id")
  @Results({
      @Result(property = "id", column = "id"),
      @Result(property = "adjustmentReason", column = "adjustmentType",
          one = @One(select = "org.openlmis.core.repository.mapper.StockAdjustmentReasonMapper.getByName")),
      @Result(property = "extensions", column = "id", javaType = List.class,
          many = @Many(select = "getStockCardEntryExtensionAttributes")),
      @Result(property = "stockCardEntryLotItems", column = "id", javaType = List.class,
          many = @Many(select = "org.openlmis.stockmanagement.repository.mapper.LotMapper.getLotMovementItemsByStockEntry"))
  })
  List<StockCardEntry> queryStockCardEntriesByDateRange(@Param("stockCardId") Long stockCardId,
                                                        @Param("startDate") Date startDate,
                                                        @Param("endDate") Date endDate);

  @Select("SELECT * FROM stock_card_entries" +
      " WHERE stockcardid = #{stockCardId}" +
      " ORDER BY id desc" +
      " limit 6")
  @Results({
      @Result(property = "id", column = "id"),
      @Result(property = "adjustmentReason", column = "adjustmentType",
          one = @One(select = "org.openlmis.core.repository.mapper.StockAdjustmentReasonMapper.getByName")),
      @Result(property = "extensions", column = "id", javaType = List.class,
          many = @Many(select = "getStockCardEntryExtensionAttributes"))
  })
  List<StockCardEntry> queryLatestStockCardEntries(@Param("stockCardId") Long stockCardId);

  @Select("SELECT tmp.expirationdates FROM (" +
      " SELECT DISTINCT" +
      " sen.createddate," +
      " (SELECT valuecolumn FROM stock_card_entry_key_values " +
      "   WHERE stockcardentryid = sen.id" +
      "   AND keycolumn='expirationdates') expirationdates" +
      " FROM stock_card_entries sen" +
      " WHERE sen.stockcardid = #{stockCardEntryId}" +
      " ORDER BY sen.createddate DESC" +
      " LIMIT 1) tmp")
  String getStockCardLatestExpirationDates(@Param("stockCardEntryId") Long stockCardEntryId);

  @Select("SELECT s.*" +
      " FROM stock_cards s" +
      " WHERE s.id = #{id}")
  StockCard getStockCardById(Long id);

  @Select("SELECT *" +
      " FROM lots_on_hand" +
      " WHERE stockcardid = #{stockCardId}")
  @Results({
      @Result(
          property = "lot", column = "lotId", javaType = Lot.class,
          one = @One(select = "org.openlmis.stockmanagement.repository.mapper.LotMapper.getById"))
  })
  List<LotOnHand> getLotsOnHand(@Param("stockCardId") Long stockCardId);

  @Select("SELECT scekv.keycolumn AS key" +
      ", scekv.valuecolumn AS value" +
      ", scekv.modifieddate AS synceddate" +
      " FROM stock_card_entries sce" +
      "   JOIN stock_card_entry_key_values scekv ON scekv.stockcardentryid = sce.id" +
      " WHERE lotonhandid = #{lotOnHandId}")
  List<StockCardEntryKV> getLotOnHandKeyValues(@Param("lotOnHandId") Long lotOnHandId);

  @Select("SELECT p.*" +
      " FROM products p" +
      "   JOIN stock_cards sc ON sc.productid = p.id" +
      " WHERE sc.id = #{stockCardId}")
  Product getProductByStockCardId(Long stockCardId);

  @Insert("INSERT INTO stock_cards (facilityId" +
      ", productId" +
      ", totalQuantityOnHand" +
      ", effectiveDate" +
      ", notes" +
      ", createdBy" +
      ", createdDate" +
      ", modifiedBy" +
      ", modifiedDate" +
      ") VALUES ( #{facility.id}" +
      ", #{product.id}" +
      ", #{totalQuantityOnHand}" +
      ", NOW()" +
      ", #{notes}" +
      ", #{createdBy}" +
      ", NOW()" +
      ", #{modifiedBy}" +
      ", NOW() )")
  @Options(useGeneratedKeys = true)
  int insert(StockCard card);

  //TODO:  add movement id, reference number
  @Insert("INSERT INTO stock_card_entries (stockcardid" +
      ", lotonhandid" +
      ", type" +
      ", quantity" +
      ", notes" +
      ", adjustmentType" +
      ", createdBy" +
      ", createdDate" +
      ", modifiedBy" +
      ", modifiedDate" +
      ", occurred" +
      ", requestedQuantity" +
      ", referenceNumber)" +
      " VALUES ( #{stockCard.id}" +
      ", #{lotOnHand.id}" +
      ", #{type}" +
      ", #{quantity}" +
      ", #{notes}" +
      ", #{adjustmentReason.name}" +
      ", #{createdBy}" +
      ", #{createdDate}" +
      ", #{modifiedBy}" +
      ", NOW()" +
      ", #{occurred}" +
      ", #{requestedQuantity}" +
      ", #{referenceNumber})")
  @Options(useGeneratedKeys = true)
  int insertEntry(StockCardEntry entry);

  @Insert("INSERT INTO stock_card_entry_key_values (stockcardentryid" +
      ", keycolumn" +
      ", valuecolumn" +
      ", createdBy" +
      ", createdDate" +
      ", modifiedBy" +
      ", modifiedDate)" +
      " VALUES (#{entry.id}" +
      ", #{key}" +
      ", #{value}" +
      ", #{entry.createdBy}" +
      ", NOW()" +
      ", #{entry.modifiedBy}" +
      ", NOW())")
  int insertEntryKeyValue(@Param("entry") StockCardEntry entry, @Param("key") String key, @Param("value") String value);

  @Update("UPDATE stock_cards " +
      "SET totalQuantityOnHand = #{totalQuantityOnHand}" +
      ", effectiveDate = NOW()" +
      ", modifiedBy = #{modifiedBy}" +
      ", modifiedDate = NOW()" +
      "WHERE id = #{id}")
  int update(StockCard card);

  @Update("UPDATE stock_cards " +
      "SET modifieddate = NOW() " +
      "WHERE facilityid = #{facilityId}")
  int updateAllStockCardSyncTimeForFacilityToNow(long facilityId);

  @Update("UPDATE stock_cards " +
      "SET modifieddate = NOW() " +
      "WHERE facilityid = #{facilityId} " +
      "AND productid = (SELECT id FROM products WHERE code = (#{stockCardProductCode}))")
  int updateStockCardToSyncTimeToNow(@Param("facilityId") long facilityId, @Param("stockCardProductCode") String stockCardProductCode);
}
