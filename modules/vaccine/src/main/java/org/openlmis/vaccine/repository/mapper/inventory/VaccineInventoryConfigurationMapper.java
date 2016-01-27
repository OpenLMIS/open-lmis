package org.openlmis.vaccine.repository.mapper.inventory;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Product;
import org.openlmis.vaccine.domain.inventory.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineInventoryConfigurationMapper {

    @Select("SELECT *" +
            " FROM vaccine_inventory_product_configurations ")
    @Results({
            @Result(property = "product", column = "productId", javaType = Product.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById"))
    })
    List<VaccineInventoryProductConfiguration> getAll();

    @Select("SELECT *" +
            " FROM vaccine_inventory_product_configurations " +
            " WHERE id=#{id}")
    VaccineInventoryProductConfiguration getById(Long id);

    @Update("update vaccine_inventory_product_configurations " +
            " set " +
            " batchtracked = #{batchTracked}," +
            " vvmtracked= #{vvmTracked}, " +
            " survivingInfants = #{survivingInfants}," +
            " denominatorEstimateCategoryId = #{denominatorEstimateCategoryId} " +
            "WHERE id=#{id} "
    )
    Integer update(VaccineInventoryProductConfiguration configuration);


    @Insert("insert into vaccine_inventory_product_configurations  " +
            " (type, productid, batchtracked, vvmtracked,survivingInfants,denominatorEstimateCategoryId) " +
            " values " +
            " (#{type}, #{productId}, #{batchTracked}, #{vvmTracked},#{survivingInfants},#{denominatorEstimateCategoryId}) ")
    @Options(useGeneratedKeys = true)
    Integer insert(VaccineInventoryProductConfiguration configuration);

}
