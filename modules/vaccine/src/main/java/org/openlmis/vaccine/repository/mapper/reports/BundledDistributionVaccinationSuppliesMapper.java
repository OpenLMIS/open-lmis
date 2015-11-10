/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
package org.openlmis.vaccine.repository.mapper.reports;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.vaccine.domain.reports.BundledDistributionVaccinationSupplies;
import org.openlmis.vaccine.domain.reports.ColdChainLineItem;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface BundledDistributionVaccinationSuppliesMapper {
    @Select(" select  \n" +
            "            zone_name,  \n" +
            "            region_name, \n" +
            "             district_name, \n" +
            "            COALESCE(population, 0) population,  \n" +
            "            COALESCE(jan_rec, 0) jan_rec,  \n" +
            "            COALESCE(feb_rec, 0) feb_rec,  \n" +
            "             COALESCE(mar_rec, 0) mar_rec,  \n" +
            "            COALESCE(apr_rec, 0) apr_rec, \n" +
            "            COALESCE(may_rec, 0)  may_rec, \n" +
            "            COALESCE(jun_rec, 0)  jun_rec,  \n" +
            "            COALESCE(jul_rec, 0) jul_rec, \n" +
            "             COALESCE(aug_rec, 0)  aug_rec,  \n" +
            "              COALESCE(sep_rec, 0) sep_rec, \n" +
            "            COALESCE(oct_rec, 0) oct_rec,  \n" +
            "              COALESCE(nov_rec, 0) nov_rec,  \n" +
            "             COALESCE(dec_rec, 0) dec_rec, \n" +
            "            COALESCE(jan_issued, 0) jan_issued, \n" +
            "             COALESCE(feb_issued, 0) feb_issued,  \n" +
            "            COALESCE(mar_issued, 0) mar_issued, \n" +
            "            COALESCE(apr_issued, 0) apr_issued,  \n" +
            "            COALESCE(may_issued, 0) may_issued, \n" +
            "             COALESCE(jun_issued, 0) jun_issued,  \n" +
            "             COALESCE(jul_issued, 0) jul_issued, \n" +
            "             COALESCE(aug_issued, 0) aug_issued, \n" +
            "              COALESCE(sep_issued, 0) sep_issued, \n" +
            "            COALESCE(oct_issued, 0) oct_issued,  \n" +
            "             COALESCE(nov_issued, 0) nov_issued, \n" +
            "             COALESCE(dec_issued, 0) dec_issued \n" +
            "             FROM crosstab( \n" +
            "              'select geographiczoneid, extract(month from startdate) period_month, sum(quantityreceived) quantityreceived \n" +
            "             from vaccine_report_logistics_line_items li \n" +
            "             join vaccine_reports vr on vr.id = li.reportid \n" +
            "             join facilities f on f.id = vr.facilityid \n" +
            "             join processing_periods pp on pp.id = vr.periodid   \n" +
            "               join products p on p.id = li.productid   \n" +
            "               join program_products pgp on pgp.productid = p.id and pgp.programid =  (select id from programs where code = ''Vaccine'')   \n" +
            "               join product_categories pc on pc.id = pgp.productcategoryid   \n" +
            "               and pc.code = ''Syringes and safety boxes''   \n" +
            "               where p.id = '|| #{productId} || ' and extract(year from startdate) = '|| #{year} || '   \n" +
            "               group by 1,2   \n" +
            "               order by 1,2',   \n" +
            "              ' SELECT m FROM generate_series(1,12) m '   \n" +
            "            )  AS (   \n" +
            "              gzid int, jan_rec int, feb_rec int, mar_rec int, apr_rec int, may_rec int, jun_rec int, jul_rec int, aug_rec int,  \n" +
            "             sep_rec int, oct_rec int, nov_rec int, dec_rec int   \n" +
            "            )    \n" +
            "             join vw_districts vd on vd.district_id = gzid   \n" +
            "             left join (   \n" +
            "             SELECT geographic_zone_id,  population   \n" +
            "             FROM crosstab(   \n" +
            "              'SELECT   \n" +
            "                geographic_zone_id,   \n" +
            "                category_id,    \n" +
            "                target_value_annual target_population   \n" +
            "               FROM vw_vaccine_district_target_population tp   \n" +
            "                 where year = '|| #{year} || '   \n" +
            "                 and category_id = 1    \n" +
            "               order by 1, 2')    \n" +
            "             AS (   \n" +
            "              geographic_zone_id int, population int   \n" +
            "             )   \n" +
            "             ) tp on tp.geographic_zone_id = gzid    \n" +
            "             left join (   \n" +
            "             select gzid geographiczoneid, jan_issued, feb_issued, mar_issued, apr_issued, may_issued, jun_issued, jul_issued, \n" +
            "             aug_issued, sep_issued, oct_issued, nov_issued, dec_issued   \n" +
            "             FROM crosstab(   \n" +
            "              'select geographiczoneid, extract(month from startdate) period_month, sum(quantityissued) quantityissued   \n" +
            "             from vaccine_report_logistics_line_items li   \n" +
            "             join vaccine_reports vr on vr.id = li.reportid   \n" +
            "             join facilities f on f.id = vr.facilityid   \n" +
            "             join processing_periods pp on pp.id = vr.periodid   \n" +
            "             join products p on p.id = li.productid   \n" +
            "             join program_products pgp on pgp.productid = p.id and pgp.programid = (select id from programs where code = ''Vaccine'')   \n" +
            "             join product_categories pc on pc.id = pgp.productcategoryid   \n" +
            "             and pc.code = ''Syringes and safety boxes''   \n" +
            "             where p.id = '|| #{productId} || ' and extract(year from startdate) = '|| #{year} || '   \n" +
            "             group by 1,2   \n" +
            "             order by 1,2',   \n" +
            "              ' SELECT m FROM generate_series(1,12) m '   \n" +
            "             )  AS (   \n" +
            "              gzid int, jan_issued int, feb_issued int, mar_issued int, apr_issued int, may_issued int, jun_issued int, jul_issued int, \n" +
            "             aug_issued int, sep_issued int, oct_issued int, nov_issued int, dec_issued int   \n" +
            "             )    \n" +
            "             ) a   \n" +
            "             on a.geographiczoneid = gzid  ")
    List<BundledDistributionVaccinationSupplies> getBundledDistributionVaccinationSupplies(@Param("year")Long yearVal, @Param("productId") Long productId);
}
