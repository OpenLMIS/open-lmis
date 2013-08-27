package org.openlmis.shipment.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentFileColumnMapper {

  @Insert({"INSERT INTO shipment_file_columns (dataFieldLabel, position, includedInShipmentFile, mandatory) values",
    "(#{dataFieldLabel}, #{position}, #{includedInShipmentFile}, #{mandatory})"})
  public void insert(ShipmentFileColumn shipmentFileColumn);

  @Select("SELECT * FROM shipment_file_columns")
  public List<ShipmentFileColumn> getAll();

  @Delete("DELETE FROM shipment_file_columns")
  void deleteAll();
}
