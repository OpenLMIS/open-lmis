package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.openlmis.core.domain.ProgramProduct;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramProductMapper {

  @Insert("INSERT INTO program_product(program_id, product_id, doses_per_month, active, modified_by, modified_date)" +
      "VALUES ((select id from program where LOWER(code)=  LOWER(#{programCode}))," +
      "(select id from product where LOWER(code)=  LOWER(#{productCode})), " +
      "#{dosesPerMonth}, #{active}, #{modifiedBy}, #{modifiedDate})")
  int insert(ProgramProduct programProduct);

  @Delete("DELETE FROM PROGRAM_PRODUCT")
  void deleteAll();
}