package org.openlmis.vaccine.repository.mapper.demographics;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.demographics.DemographicEstimateCategory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemographicEstimateCategoryMapper {

  @Select("select * from demographic_estimate_categories order by id")
  List<DemographicEstimateCategory> getAll();

  @Select("select * from demographic_estimate_categories where id = #{id}")
  DemographicEstimateCategory getById( @Param("id") Long id);

  @Insert("insert into demographic_estimate_categories " +
    "(name, description, isPrimaryEstimate, defaultConversionFactor, createdBy, createdDate)" +
    "values " +
    "(#{name}, #{description}, #{isPrimaryEstimate}, #{defaultConversionFactor}, #{createdBy}, NOW())")
  @Options(flushCache = true, useGeneratedKeys = true)
  Integer insert(DemographicEstimateCategory category);

  @Update("update demographic_estimate_categories " +
    " set " +
    " name = #{name} " +
    ", description = #{description} " +
    ", isPrimaryEstimate = #{isPrimaryEstimate} " +
    ", defaultConversionFactor = #{defaultConversionFactor} " +
    ", modifiedBy = #{modifiedBy} " +
    ", modifiedDate = NOW() " +
    " where id = #{id} ")
  Integer update(DemographicEstimateCategory category);
}
