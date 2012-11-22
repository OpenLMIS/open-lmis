package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.openlmis.rnr.domain.RnrLineItem;
import org.springframework.stereotype.Repository;

@Repository
public interface RnrLineItemMapper {

    @Insert("insert into requisition_line_item(rnr_id, product_code, modified_by, modified_date) " +
            "values (#{rnrId}, #{productCode}, #{modifiedBy}, #{modifiedDate})")
    public int insert(RnrLineItem rnrLineItem);

    @Delete("delete from requisition_line_item")
    public void deleteAll();

}
