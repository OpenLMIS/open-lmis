package org.openlmis.stockmanagement.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Product;
import org.openlmis.stockmanagement.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LotMapper {

  @Select("SELECT *" +
      " FROM lots" +
      " WHERE id = #{id}")
  @Results({
      @Result(
          property = "product", column = "productId", javaType = Product.class,
          one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById")),
      @Result(property = "lotCode", column = "lotnumber")
  })
  Lot getById(@Param("id")Long id);

  @Select("SELECT *" +
      " FROM lots" +
      " WHERE LOWER(lotnumber) = LOWER(#{lotCode})" +
      "   AND LOWER(manufacturername) = LOWER(#{manufacturerName})" +
      "   AND expirationdate = #{expirationDate}")
  @Results({
      @Result(
          property = "product", column = "productId", javaType = Product.class,
          one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById")),
      @Result(property = "lotCode", column = "lotnumber")
  })
  Lot getByObject(Lot lot);

  @Select("SELECT *" +
      " FROM lots_on_hand" +
      " WHERE stockcardid = #{stockCardId}" +
      "   AND lotid = #{lotId}")
  @Results({
      @Result(
          property = "lot", column = "lotId", javaType = Lot.class,
          one = @One(select = "getById"))
  })
  LotOnHand getLotOnHandByStockCardAndLot(@Param("stockCardId")Long stockCardId, @Param("lotId")Long lotId);

  @Select("SELECT *" +
      " FROM lots_on_hand loh" +
      "   JOIN lots l ON l.id = loh.lotid" +
      " WHERE loh.stockcardid = #{stockCardId}" +
      "   AND LOWER(l.lotnumber) = LOWER(#{lot.lotCode})" +
      "   AND LOWER(l.manufacturername) = LOWER(#{lot.manufacturerName})" +
      "   AND l.expirationdate = #{lot.expirationDate}")
  @Results({
      @Result(
          property = "lot", column = "lotId", javaType = Lot.class,
          one = @One(select = "getById"))
  })
  LotOnHand getLotOnHandByStockCardAndLotObject(@Param("stockCardId")Long stockCardId, @Param("lot")Lot lot);

  @Insert("INSERT into lots " +
      " (productId, lotNumber, manufacturerName, manufactureDate, expirationDate" +
      ", createdBy, createdDate, modifiedBy, modifiedDate) " +
      "values " +
      " (#{product.id}, #{lotCode}, #{manufacturerName}, #{manufactureDate}, #{expirationDate}" +
      ", #{createdBy}, NOW(), #{modifiedBy}, NOW())")
  @Options(useGeneratedKeys = true)
  void insert(Lot lot);

  @Update("UPDATE lots " +
      "SET lotNumber = #{lotCode}" +
      ", manufacturerName = #{manufacturerName}" +
      ", manufactureDate = #{manufactureDate}" +
      ", expirationDate = #{expirationDate}" +
      ", modifiedBy = #{modifiedBy}" +
      ", modifiedDate = NOW()" +
      "WHERE id = #{id}")
  int update(Lot lot);

  @Insert("INSERT into lots_on_hand " +
      " (stockCardId, lotId, quantityOnHand, effectiveDate" +
      ", createdBy, createdDate, modifiedBy, modifiedDate) " +
      "values " +
      " (#{stockCard.id}, #{lot.id}, #{quantityOnHand}, #{effectiveDate}" +
      ", #{createdBy}, NOW(), #{modifiedBy}, NOW())")
  @Options(useGeneratedKeys = true)
  void insertLotOnHand(LotOnHand lotOnHand);

  @Update("UPDATE lots_on_hand " +
      "SET quantityOnHand = #{quantityOnHand}" +
          ", effectiveDate = NOW()" +
          ", modifiedBy = #{modifiedBy}" +
          ", modifiedDate = NOW()" +
      "WHERE id = #{id}")
  int updateLotOnHand(LotOnHand lotOnHand);

  @Select("SELECT loh.* FROM lots_on_hand loh " +
          "JOIN lots l on l.id = loh.lotid " +
          "JOIN stock_cards s on s.id = loh.stockcardid " +
          "JOIN products p on p.id = s.productid " +
          "JOIN facilities f on f.id = s.facilityid " +
          "WHERE f.id = #{facilityId} AND p.code = #{productCode} AND l.lotnumber = #{lotCode}")
  @Results({
          @Result(
                  property = "stockCard", column = "stockcardid", javaType = StockCard.class,
                  one = @One(select = "org.openlmis.stockmanagement.repository.mapper.StockCardMapper.getStockCardById")),
          @Result(
                  property = "lot", column = "lotId", javaType = Lot.class,
                  one = @One(select = "org.openlmis.stockmanagement.repository.mapper.LotMapper.getLotById"))
  })
  LotOnHand getLotOnHandByLotNumberAndProductCodeAndFacilityId(@Param("lotCode") String lotCode,
                                                               @Param("productCode") String productCode, @Param("facilityId") Long facilityId);

  @Select("SELECT l.lotnumber as lotCode," +
          "l.manufacturername as manufacturerName," +
          "l.manufacturedate as manufactureDate, " +
          "l.expirationdate as expirationDate " +
          "FROM lots l " +
          "WHERE id = #{lotId}")
  @Result(property = "product", column = "productid", javaType = Product.class,
          one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById"))
  Lot getLotById(Long lotId);

  @Insert("INSERT INTO stock_card_entry_lot_items " +
          " (stockCardEntryId, lotId, quantity, effectiveDate, createdBy, createdDate, modifiedBy, modifiedDate) " +
          "VALUES " +
          " (#{stockCardEntryId}, #{lot.id}, #{quantity}, #{effectiveDate}, #{createdBy}, NOW(), #{modifiedBy}, NOW())")
  @Options(useGeneratedKeys = true)
  void insertLotMovementItem(StockCardEntryLotItem stockCardEntryLotItem);

  @Select("SELECT * " +
          "FROM stock_card_entry_lot_items s " +
          "WHERE stockCardEntryId = #{stockCardEntryId}")
  @Results({
      @Result(property = "lot", column = "lotId", javaType = Lot.class,
              one = @One(select = "org.openlmis.stockmanagement.repository.mapper.LotMapper.getLotById")),
      @Result(property = "extensions", column = "id", javaType = List.class,
          many = @Many(select = "getStockCardEntryLotItemExtensions"))
  })
  List<StockCardEntryLotItem> getLotMovementItemsByStockEntry(Long stockCardEntryId);

  @Select("SELECT s.keycolumn as key, " +
          "s.valuecolumn as value " +
          "FROM stock_card_entry_lot_items_key_values s " +
          "WHERE stockCardEntryLotItemId = #{stockCardEntryLotItemId}")
  List<StockCardEntryLotItemKV> getStockCardEntryLotItemExtensions(Long stockCardEntryLotItemId);
}
