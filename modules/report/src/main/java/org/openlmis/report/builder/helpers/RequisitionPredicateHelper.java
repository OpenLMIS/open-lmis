package org.openlmis.report.builder.helpers;

public class RequisitionPredicateHelper {

  public static String multiProductFilterBy(String products, String productIdField, String tracerField){
    if(products.equals("-1")){
      return String.format("%s = true", tracerField);
    } else if(!products.equals("0") && !products.isEmpty()){
      return String.format(" %2$s = ANY(array[ %1$s ]::INT[])", products, productIdField);
    }
    return null;
  }

  public static String productCategoryIsFilteredBy(String field){
      return String.format("%s = #{filterCriteria.productCategory} ", field);
  }

  public static String userHasPermissionOnFacilityBy(String field){
    return String.format("%s in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.program}) " , field);
  }

  public static String geoZoneIsFilteredBy( String viewAlias){
      return String.format( "(%1$s.zone_id = #{filterCriteria.zone} or %1$s.parent = #{filterCriteria.zone} or %1$s.region_id = #{filterCriteria.zone} or %1$s.district_id = #{filterCriteria.zone})" , viewAlias) ;
  }

  public static String programIsFilteredBy(String field){
    return String.format("%s = #{filterCriteria.program}", field);
  }

  public static String periodIsFilteredBy(String field){
    return String.format("%s = #{filterCriteria.period}", field);
  }

  public static String rnrStatusFilteredBy(String field, String acceptedRnrStatuses){
    return  field +" in ( " + acceptedRnrStatuses +" )";
  }

  public static String productFilteredBy(String field){
    return String.format("%s = #{filterCriteria.product}::INT", field);
  }

  public static String facilityIsFilteredBy(String field){
    return String.format("%s = #{filterCriteria.facility}::INT", field);
  }

  public static String facilityTypeIsFilteredBy(String field){
    return String.format("%s= #{filterCriteria.facilityTypeId}", field);
  }

}
