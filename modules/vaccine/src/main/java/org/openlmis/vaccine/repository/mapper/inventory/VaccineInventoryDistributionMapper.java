package org.openlmis.vaccine.repository.mapper.inventory;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.vaccine.domain.inventory.VaccineDistributionLineItem;
import org.openlmis.vaccine.domain.inventory.VaccineDistributionLineItemLot;
import org.openlmis.vaccine.domain.inventory.VaccineDistribution;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface VaccineInventoryDistributionMapper {

    @Insert("insert into vaccine_distributions " +
            " (tofacilityid, fromfacilityid, vouchernumber, distributiondate, periodid,orderid,status, distributiontype, createdby, createddate, modifiedby,modifieddate )" +
            " values " +
            " (#{toFacilityId}, #{fromFacilityId}, #{voucherNumber}, #{distributionDate}, #{periodId}, #{orderId}, #{status},#{distributionType}, #{createdBy},NOW(),#{modifiedBy},NOW()) ")
    @Options(useGeneratedKeys = true)
    Integer saveDistribution(VaccineDistribution vaccineDistribution);

    @Insert("insert into vaccine_distribution_line_items " +
            " (distributionid, productid, quantity, vvmstatus, createdby, createddate, modifiedby,modifieddate )" +
            " values " +
            " (#{distributionId}, #{productId}, #{quantity}, #{vvmStatus}, #{createdBy},NOW(),#{modifiedBy},NOW()) ")
    @Options(useGeneratedKeys = true)
    Integer saveDistributionLineItem(VaccineDistributionLineItem vaccineDistributionLineItem);

    @Insert("insert into vaccine_distribution_line_item_lots " +
            " (distributionlineitemid, lotid, quantity, vvmstatus, createdby, createddate, modifiedby,modifieddate )" +
            " values " +
            " (#{distributionLineItemId}, #{lotId}, #{quantity}, #{vvmStatus}, #{createdBy},NOW(),#{modifiedBy},NOW()) ")
    @Options(useGeneratedKeys = true)
    Integer saveDistributionLineItemLot(VaccineDistributionLineItemLot lot);


    @Select("Select  pp.id, pp.name, pp.startdate::DATE, pp.enddate::DATE from requisition_groups rg " +
            " JOIN supervisory_nodes sn on rg.supervisorynodeId = sn.id " +
            " JOIN requisition_group_program_schedules RGS ON rg.id = rgs.requisitiongroupid " +
            " JOIN processing_schedules ps ON rgs.scheduleid = ps.id " +
            " JOIN processing_periods pp ON ps.id = pp.scheduleid " +
            " WHERE sn.facilityid = #{facilityId} and RGS.programid=#{programId} " +
            " AND   #{distributionDate}::DATE >= pp.startdate::DATE  AND #{distributionDate}::DATE <=pp.enddate::DATE LIMIT 1;")
    @Results(value = {
            @Result(property = "name", column = "name"),
            @Result(property = "startDate", column = "startdate"),
            @Result(property = "endDate", column = "enddate")
    })
    ProcessingPeriod getCurrentPeriod(@Param("facilityId") Long facilityId, @Param("programId") Long programId, @Param("distributionDate") Date distributionDate);

    @Select("SELECT *" +
            " FROM vaccine_distributions " +
            " WHERE EXTRACT(MONTH FROM distributionDate) = #{month} "
    )
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "lineItems", column = "id", javaType = List.class,
                    many = @Many(select = "getLineItems"))})
    List<VaccineDistribution> getDistributedFacilitiesByMonth(@Param("month") int month);

    @Select("SELECT *" +
            " FROM vaccine_distributions " +
            "WHERE periodId=#{periodId}")
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "lineItems", column = "id", javaType = List.class,
                    many = @Many(select = "getLineItems"))})
    List<VaccineDistribution> getDistributedFacilitiesByPeriod(@Param("periodId") Long periodId);

    @Select("SELECT *" +
            " FROM vaccine_distribution_line_items" +
            " WHERE distributionid = #{distributionId}"
    )
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "lots", column = "id", javaType = List.class,
                    many = @Many(select = "getLineItemsLots"))})
    List<VaccineDistributionLineItem> getLineItems(@Param("distributionId") Long distributionId);

    @Select("SELECT *" +
            " FROM vaccine_distribution_line_item_lots" +
            " WHERE distributionlineitemid = #{distributionLineItemId}"
    )
    List<VaccineDistributionLineItemLot> getLineItemsLots(@Param("distributionLineItemId") Long distributionLineItemId);
}
