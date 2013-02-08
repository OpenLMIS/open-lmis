package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.ProductCategory;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductCategoryMapper {


  @Insert("INSERT INTO product_categories (code, name, modifiedBy) VALUES (#{code}, #{name}, #{modifiedBy})")
  @Options(useGeneratedKeys = true)
  public void insert(ProductCategory productCategory);

  @Select("SELECT * FROM product_categories WHERE id = #{id}")
  public ProductCategory getProductCategoryById(Integer id);

  @Select("SELECT * FROM product_categories WHERE code = #{code}")
  public ProductCategory getProductCategoryByCode(String code);

  @Update("UPDATE product_categories SET name = #{name}, modifiedBy = #{modifiedBy}, modifiedDate = DEFAULT where id = #{id}")
  public void update(ProductCategory category);
}
