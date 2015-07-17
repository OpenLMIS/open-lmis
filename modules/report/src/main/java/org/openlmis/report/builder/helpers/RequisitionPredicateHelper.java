package org.openlmis.report.builder.helpers;

import org.apache.commons.lang.StringUtils;

import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

public class RequisitionPredicateHelper {

  public static void whereMultiProductFilter(String products){
    if(products.equals("-1")){
      WHERE("p.tracer = true");
    } else if(!products.equals("0") && !products.isEmpty()){
      WHERE(" p.id = ANY(array[" + products + " ]::INT[])");
    }
  }

  public static void whereProductCategory(Long productCategory){
    if(productCategory > 0){
      WHERE( "ppc.productCategoryId = #{filterCriteria.productCategory}");
    }
  }

  public static void whereFacilityPermission(){
    WHERE("f.id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.program}) " );
  }

  public static void whereGeographicZoneFilter(Long geographicZone){
    if(geographicZone != 0){
      WHERE( "(d.zone_id = #{filterCriteria.zone} or d.parent = #{filterCriteria.zone} or d.region_id = #{filterCriteria.zone} or d.district_id = #{filterCriteria.zone}) ") ;
    }
  }

  public static void whereProgramFilter(){
    WHERE("r.programId = #{filterCriteria.program}");
  }

  public static void wherePeriodFilter(){
    WHERE("r.periodId = #{filterCriteria.period}");
  }

}
