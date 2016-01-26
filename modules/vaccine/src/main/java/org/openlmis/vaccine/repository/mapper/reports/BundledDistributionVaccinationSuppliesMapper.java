/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.vaccine.repository.mapper.reports;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.vaccine.domain.reports.*;
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
    List<BundledDistributionVaccinationSupplies> getBundledDistributionVaccinationSupplies(@Param("year") Long yearVal, @Param("productId") Long productId);

    @Select("" +
            "select \n" +

            "count(case when b.jan_rec<.5 then 't' else null end) jan_rec_district_less_lower_limit ,\n" +
            "count(case when b.feb_rec<.5 then 't' else null end) feb_rec_district_less_lower_limit,\n" +
            "count(case when b.mar_rec<.5 then 't' else null end) mar_rec_district_less_lower_limit ,\n" +
            "count(case when b.apr_rec<.5 then 't' else null end) apr_rec_district_less_lower_limit,\n" +
            "count(case when b.may_rec<.5 then 't' else null end) may_rec_district_less_lower_limit ,\n" +
            "count(case when b.jun_rec<.5 then 't' else null end) jun_rec_district_less_lower_limit,\n" +
            "count(case when b.jul_rec<.5 then 't' else null end) jul_rec_district_less_lower_limit ,\n" +
            "count(case when b.aug_rec<.5 then 't' else null end) aug_rec_district_less_lower_limit,\n" +
            "count(case when b.sep_rec<.5 then 't' else null end) sep_rec_district_less_lower_limit ,\n" +
            "count(case when b.oct_rec<.5 then 't' else null end) oct_rec_district_less_lower_limit,\n" +
            "count(case when b.nov_rec<.5 then 't' else null end) nov_rec_district_less_lower_limit ,\n" +
            "count(case when b.dec_rec<.5 then 't' else null end) dec_rec_district_less_lower_limit,\n" +

            "count(case when b.jan_rec>3 then 't' else null end) jan_rec_district_greater_upper_limit ,\n" +
            "count(case when b.feb_rec>3 then 't' else null end) feb_rec_district_greater_upper_limit ,\n" +
            "count(case when b.mar_rec>3 then 't' else null end) mar_rec_district_greater_upper_limit ,\n" +
            "count(case when b.apr_rec>3 then 't' else null end) apr_rec_district_greater_upper_limit ,\n" +
            "count(case when b.may_rec>3 then 't' else null end) may_rec_district_greater_upper_limit ,\n" +
            "count(case when b.jun_rec>3 then 't' else null end) jun_rec_district_greater_upper_limit ,\n" +
            "count(case when b.jul_rec>3 then 't' else null end) jul_rec_district_greater_upper_limit ,\n" +
            "count(case when b.aug_rec>3 then 't' else null end) aug_rec_district_greater_upper_limit ,\n" +
            "count(case when b.sep_rec>3 then 't' else null end) sep_rec_district_greater_upper_limit ,\n" +
            "count(case when b.oct_rec>3 then 't' else null end) oct_rec_district_greater_upper_limit ,\n" +
            "count(case when b.nov_rec>3then 't' else null end) nov_rec_district_greater_upper_limit ,\n" +
            "count(case when b.dec_rec>3 then 't' else null end) dec_rec_district_greater_upper_limit ,\n" +

            "count(case when b.jan_issued<.5 then 't' else null end) jan_issued_district_less_lower_limit ,\n" +
            "count(case when b.feb_issued<.5 then 't' else null end) feb_issued_district_less_lower_limit,\n" +
            "count(case when b.mar_issued<.5 then 't' else null end) mar_issued_district_less_lower_limit ,\n" +
            "count(case when b.apr_issued<.5 then 't' else null end) apr_issued_district_less_lower_limit,\n" +
            "count(case when b.may_issued<.5 then 't' else null end) may_issued_district_less_lower_limit,\n" +
            "count(case when b.jun_issued<.5 then 't' else null end) jun_issued_district_less_lower_limit ,\n" +
            "count(case when b.jul_issued<.5 then 't' else null end) jul_issued_district_less_lower_limit,\n" +
            "count(case when b.aug_issued<.5 then 't' else null end) aug_issued_district_less_lower_limit ,\n" +
            "count(case when b.sep_issued<.5 then 't' else null end) sep_issued_district_less_lower_limit,\n" +
            "count(case when b.oct_issued<.5 then 't' else null end) oct_issued_district_less_lower_limit ,\n" +
            "count(case when b.nov_issued<.5 then 't' else null end) nov_issued_district_less_lower_limit,\n" +
            "count(case when b.dec_issued<.5 then 't' else null end) dec_issued_district_less_lower_limit ,\n" +

            "count(case when b.jan_issued>3 then 't' else null end) jan_issued_district_greater_upper_limit ,\n" +
            "count(case when b.feb_issued>3 then 't' else null end) feb_issued_district_greater_upper_limit ,\n" +
            "count(case when b.mar_issued>3 then 't' else null end) mar_issued_district_greater_upper_limit ,\n" +
            "count(case when b.apr_issued>3 then 't' else null end) apr_issued_district_greater_upper_limit ,\n" +
            "count(case when b.may_issued>3 then 't' else null end) may_issued_district_greater_upper_limit ,\n" +
            "count(case when b.jun_issued>3 then 't' else null end) jun_issued_district_greater_upper_limit ,\n" +
            "count(case when b.jul_issued>3 then 't' else null end) jul_issued_district_greater_upper_limit ,\n" +
            "count(case when b.aug_issued>3 then 't' else null end) aug_issued_district_greater_upper_limit ,\n" +
            "count(case when b.sep_issued>3 then 't' else null end) sep_issued_district_greater_upper_limit ,\n" +
            "count(case when b.oct_issued>3 then 't' else null end) oct_issued_district_greater_upper_limit ,\n" +
            "count(case when b.nov_issued>3 then 't' else null end) nov_issued_district_greater_upper_limit ,\n" +
            "count(case when b.dec_issued>3 then 't' else null end) dec_issued_district_greater_upper_limit \n" +
            "from \n" +
            " ( select  \n" +
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
            "             on a.geographiczoneid = gzid  ) b")
    BundledDistributionVaccinationSupplyDistrict getBundledDistributionVaccinationSuppliesDistrictSummary(@Param("year") Long yearVal, @Param("productId") Long productId);

    @Select("" +
            "select \n" +

            "count(case when b.jan_rec<.5 then 't' else null end) jan_rec_district_less_lower_limit ,\n" +
            "count(case when b.feb_rec<.5 then 't' else null end) feb_rec_district_less_lower_limit,\n" +
            "count(case when b.mar_rec<.5 then 't' else null end) mar_rec_district_less_lower_limit ,\n" +
            "count(case when b.apr_rec<.5 then 't' else null end) apr_rec_district_less_lower_limit,\n" +
            "count(case when b.may_rec<.5 then 't' else null end) may_rec_district_less_lower_limit ,\n" +
            "count(case when b.jun_rec<.5 then 't' else null end) jun_rec_district_less_lower_limit,\n" +
            "count(case when b.jul_rec<.5 then 't' else null end) jul_rec_district_less_lower_limit ,\n" +
            "count(case when b.aug_rec<.5 then 't' else null end) aug_rec_district_less_lower_limit,\n" +
            "count(case when b.sep_rec<.5 then 't' else null end) sep_rec_district_less_lower_limit ,\n" +
            "count(case when b.oct_rec<.5 then 't' else null end) oct_rec_district_less_lower_limit,\n" +
            "count(case when b.nov_rec<.5 then 't' else null end) nov_rec_district_less_lower_limit ,\n" +
            "count(case when b.dec_rec<.5 then 't' else null end) dec_rec_district_less_lower_limit,\n" +

            "count(case when b.jan_rec>3 then 't' else null end) jan_rec_district_greater_upper_limit ,\n" +
            "count(case when b.feb_rec>3 then 't' else null end) feb_rec_district_greater_upper_limit ,\n" +
            "count(case when b.mar_rec>3 then 't' else null end) mar_rec_district_greater_upper_limit ,\n" +
            "count(case when b.apr_rec>3 then 't' else null end) apr_rec_district_greater_upper_limit ,\n" +
            "count(case when b.may_rec>3 then 't' else null end) may_rec_district_greater_upper_limit ,\n" +
            "count(case when b.jun_rec>3 then 't' else null end) jun_rec_district_greater_upper_limit ,\n" +
            "count(case when b.jul_rec>3 then 't' else null end) jul_rec_district_greater_upper_limit ,\n" +
            "count(case when b.aug_rec>3 then 't' else null end) aug_rec_district_greater_upper_limit ,\n" +
            "count(case when b.sep_rec>3 then 't' else null end) sep_rec_district_greater_upper_limit ,\n" +
            "count(case when b.oct_rec>3 then 't' else null end) oct_rec_district_greater_upper_limit ,\n" +
            "count(case when b.nov_rec>3then 't' else null end) nov_rec_district_greater_upper_limit ,\n" +
            "count(case when b.dec_rec>3 then 't' else null end) dec_rec_district_greater_upper_limit ,\n" +

            "count(case when b.jan_issued<.5 then 't' else null end) jan_issued_district_less_lower_limit ,\n" +
            "count(case when b.feb_issued<.5 then 't' else null end) feb_issued_district_less_lower_limit,\n" +
            "count(case when b.mar_issued<.5 then 't' else null end) mar_issued_district_less_lower_limit ,\n" +
            "count(case when b.apr_issued<.5 then 't' else null end) apr_issued_district_less_lower_limit,\n" +
            "count(case when b.may_issued<.5 then 't' else null end) may_issued_district_less_lower_limit,\n" +
            "count(case when b.jun_issued<.5 then 't' else null end) jun_issued_district_less_lower_limit ,\n" +
            "count(case when b.jul_issued<.5 then 't' else null end) jul_issued_district_less_lower_limit,\n" +
            "count(case when b.aug_issued<.5 then 't' else null end) aug_issued_district_less_lower_limit ,\n" +
            "count(case when b.sep_issued<.5 then 't' else null end) sep_issued_district_less_lower_limit,\n" +
            "count(case when b.oct_issued<.5 then 't' else null end) oct_issued_district_less_lower_limit ,\n" +
            "count(case when b.nov_issued<.5 then 't' else null end) nov_issued_district_less_lower_limit,\n" +
            "count(case when b.dec_issued<.5 then 't' else null end) dec_issued_district_less_lower_limit ,\n" +

            "count(case when b.jan_issued>3 then 't' else null end) jan_issued_district_greater_upper_limit ,\n" +
            "count(case when b.feb_issued>3 then 't' else null end) feb_issued_district_greater_upper_limit ,\n" +
            "count(case when b.mar_issued>3 then 't' else null end) mar_issued_district_greater_upper_limit ,\n" +
            "count(case when b.apr_issued>3 then 't' else null end) apr_issued_district_greater_upper_limit ,\n" +
            "count(case when b.may_issued>3 then 't' else null end) may_issued_district_greater_upper_limit ,\n" +
            "count(case when b.jun_issued>3 then 't' else null end) jun_issued_district_greater_upper_limit ,\n" +
            "count(case when b.jul_issued>3 then 't' else null end) jul_issued_district_greater_upper_limit ,\n" +
            "count(case when b.aug_issued>3 then 't' else null end) aug_issued_district_greater_upper_limit ,\n" +
            "count(case when b.sep_issued>3 then 't' else null end) sep_issued_district_greater_upper_limit ,\n" +
            "count(case when b.oct_issued>3 then 't' else null end) oct_issued_district_greater_upper_limit ,\n" +
            "count(case when b.nov_issued>3 then 't' else null end) nov_issued_district_greater_upper_limit ,\n" +
            "count(case when b.dec_issued>3 then 't' else null end) dec_issued_district_greater_upper_limit \n" +
            "from \n" +
            "(" +
            "  select  \n" +
            " zone_name,   \n" +
            "                        region_name,  \n" +

            "                      COALESCE(sum(jan_rec),0) jan_rec,    \n" +
            "                                        COALESCE(sum(feb_rec),0) feb_rec,    \n" +
            "                                        COALESCE( sum(mar_rec),0) mar_rec,    \n" +
            "                                       COALESCE( sum(apr_rec),0) apr_rec,   \n" +
            "                                       COALESCE( sum(may_rec),0)  may_rec,   \n" +
            "                                       COALESCE( sum(jun_rec),0)  jun_rec,    \n" +
            "                                       COALESCE( sum(jul_rec),0) jul_rec,   \n" +
            "                                        COALESCE( sum(aug_rec),0)  aug_rec,    \n" +
            "                                        COALESCE(  sum(sep_rec),0) sep_rec,   \n" +
            "                                       COALESCE( sum(oct_rec),0) oct_rec,    \n" +
            "                                        COALESCE(  sum(nov_rec),0) nov_rec,    \n" +
            "                                        COALESCE( sum(dec_rec),0) dec_rec,   \n" +
            "                                      COALESCE(  sum(jan_issued),0) jan_issued,   \n" +
            "                                       COALESCE(  sum(feb_issued),0) feb_issued,    \n" +
            "                                       COALESCE( sum(mar_issued),0) mar_issued,   \n" +
            "                                       COALESCE( sum(apr_issued),0) apr_issued,    \n" +
            "                                       COALESCE( sum(may_issued),0) may_issued,   \n" +
            "                                       COALESCE(  sum(jun_issued),0) jun_issued,    \n" +
            "                                        COALESCE( sum(jul_issued),0) jul_issued,   \n" +
            "                                        COALESCE( sum(aug_issued),0) aug_issued,   \n" +
            "                                        COALESCE(  sum(sep_issued),0) sep_issued,   \n" +
            "                                       COALESCE( sum(oct_issued),0) oct_issued,    \n" +
            "                                        COALESCE( sum(nov_issued),0) nov_issued,   \n" +
            "                                        COALESCE( sum(dec_issued),0) dec_issued  " +

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
            "             on a.geographiczoneid = gzid group by vd.region_name, vd.zone_name ) b")
    BundledDistributionVaccinationSupplyRegion getBundledDistributionVaccinationSuppliesRegionSummary(@Param("year") Long yearVal, @Param("productId") Long productId);
}
