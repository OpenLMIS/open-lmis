/*
 *
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 *  Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openlmis.vaccine.service.reports;/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

import org.apache.commons.lang.StringUtils;
import org.openlmis.core.domain.Product;
import org.openlmis.vaccine.domain.reports.*;
import org.openlmis.vaccine.domain.reports.params.PerformanceByDropoutRateParam;
import org.openlmis.vaccine.repository.reports.PerformanceByDropoutRateByDistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class PerformanceByDropoutRateByDistrictService {
    @Autowired
    private PerformanceByDropoutRateByDistrictRepository repository;
    public static final String BELOW_MIN = "1_dropoutGreaterThanHigh";
    public static final String MIN = "2_dropOutBetweenMidAndMin";
    public static final String AVERAGE = "3_droOputBetweenMidAndHigh";
    public static final String HIGHER = "4_dropoutGreaterThanHigh";
    public static final String DISTRICT_LEVEL = "";
    public static final int REGION_REPORT = 1;
    public static final int DISTRICT_REPORT = 2;
    public static final int FACILLITY_REPORT = 3;

    public boolean isDistrictGeographicLevel(int geopgraphicId) {
        boolean geographicZone = false;
        return geographicZone;
    }

    public PerformanceByDisrictReport loadPerformanceByDropoutRateDistrictReports(Map<String, String[]> filterCriteria) {
        boolean isFailityReport = false;
        boolean isRegionReport = false;
        PerformanceByDisrictReport performanceByDisrictReport = null;
        Map<String, List<PerformanceByDropoutRateByDistrict>> stringPerformanceByDropoutRateByDistrictMap = null;
        Map<String, List<PerformanceByDropoutRateByDistrict>> stringPerformanceByDropoutRateByRegionMap = null;
        Map<String, Map<Date, Long>> columnRangeValues = null;
        Map<String, Map<Date, Long>> regionColumnRangeValues = null;
        List<Date> columnNames = null;
        List<PerformanceByDropoutRateByDistrict> performanceByDropoutRateByDistrictList = null;
        List<PerformanceByDropoutRateByDistrict> performanceByDropoutRateByRegionList = null;

        PerformanceByDropoutRateParam filterParam = null;
        filterParam = prepareParam(filterCriteria);
        isRegionReport = filterParam.getGeographic_zone_id() == 0 ? true : false;

        isFailityReport = repository.isDistrictLevel(filterParam.getGeographic_zone_id());
        if (!isFailityReport) {
            performanceByDropoutRateByDistrictList = repository.loadPerformanceByDropoutRateDistrictReports(filterParam);
            stringPerformanceByDropoutRateByDistrictMap = this.prepareReportForGeographicLevel(performanceByDropoutRateByDistrictList, DISTRICT_REPORT);
            if (isRegionReport) {
                performanceByDropoutRateByRegionList = repository.loadPerformanceByDropoutRateRegionReports(filterParam);
                stringPerformanceByDropoutRateByRegionMap = this.prepareReportForGeographicLevel(performanceByDropoutRateByRegionList, REGION_REPORT);


            }

        } else {

            performanceByDropoutRateByDistrictList = repository.loadPerformanceByDropoutRateFacillityReports(filterParam);
            stringPerformanceByDropoutRateByDistrictMap = this.prepareReportForGeographicLevel(performanceByDropoutRateByDistrictList, FACILLITY_REPORT);
        }


        performanceByDisrictReport = this.aggregateReport(performanceByDropoutRateByDistrictList);
        performanceByDisrictReport.setRegionReport(isRegionReport);
        performanceByDisrictReport.setFacillityReport(isFailityReport);
        columnNames = this.extractColumnValues(filterParam);
        columnRangeValues = this.prepareColumnRangesForSummary(columnNames, performanceByDropoutRateByDistrictList);
        if (isRegionReport) {
            regionColumnRangeValues = this.prepareColumnRangesForSummary(columnNames, performanceByDropoutRateByRegionList);
            performanceByDisrictReport.setRegionColumnsValueList(this.prepareColumn(regionColumnRangeValues));
        }
        performanceByDisrictReport.setColumnsValueList(this.prepareColumn(columnRangeValues));
        performanceByDisrictReport.setColumnNames(columnNames);
        performanceByDisrictReport.setDetailPerformanceByDropoutRateByDistrictList(performanceByDropoutRateByDistrictList);
        performanceByDisrictReport.setPerformanceByDropOutDistrictsList(this.prepareDistrict(stringPerformanceByDropoutRateByDistrictMap));
        performanceByDisrictReport.setPerformanceByDropOutRegionsList(this.prepareDistrict(stringPerformanceByDropoutRateByRegionMap));
        performanceByDisrictReport.setFacillityReport(isFailityReport);
        return performanceByDisrictReport;
    }

    public Map<String, Map<Date, Long>> prepareColumnRangesForSummary(List<Date> columnNames, List<PerformanceByDropoutRateByDistrict> performanceByDropoutRateByDistrictList) {

        Map<String, Map<Date, Long>> columnRangeValues = null;
        columnRangeValues = this.intializeColRangeValues(columnNames);
        for (PerformanceByDropoutRateByDistrict performanceByDropoutRateByDistrict : performanceByDropoutRateByDistrictList) {
            Date dateString = performanceByDropoutRateByDistrict.getPeriod_name();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy");
            Date columngName = null;
            try {
                columngName = dateFormat.parse(dateFormat.format(dateString));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            float value = performanceByDropoutRateByDistrict.getBcg_mr_dropout();

            if (value > 20) {

                Long highVal = columnRangeValues.get(HIGHER).get(columngName) + 1l;
                columnRangeValues.get(HIGHER).put(columngName, highVal);
            } else if (value > 10) {

                Long highVal = columnRangeValues.get(AVERAGE).get(columngName) + 1l;
                columnRangeValues.get(AVERAGE).put(columngName, highVal);

            } else if (value > 5) {

                Long highVal = columnRangeValues.get(MIN).get(columngName) + 1l;
                columnRangeValues.get(MIN).put(columngName, highVal);
            } else {

                Long highVal = columnRangeValues.get(BELOW_MIN).get(columngName) + 1l;
                columnRangeValues.get(BELOW_MIN).put(columngName, highVal);
            }

        }
        return columnRangeValues;

    }

    public Map<String, List<PerformanceByDropoutRateByDistrict>> prepareReportForGeographicLevel(List<PerformanceByDropoutRateByDistrict> performanceByDropoutRateByDistrictList, int reportType) {
        Map<String, List<PerformanceByDropoutRateByDistrict>> stringPerformanceByDropoutRateByDistrictMap = new HashMap<>();

        for (PerformanceByDropoutRateByDistrict performanceByDropoutRateByDistrict : performanceByDropoutRateByDistrictList) {
            // String dateString = performanceByDropoutRateByDistrict.getPeriod_name();


            String districtName = performanceByDropoutRateByDistrict.getRegion_name();
            if (reportType == DISTRICT_REPORT) {
                districtName = districtName + "_" + performanceByDropoutRateByDistrict.getDistrict_name();
            }
            if (reportType == FACILLITY_REPORT) {
                districtName = districtName + "_" + performanceByDropoutRateByDistrict.getFacility_name();
            }
            if (!stringPerformanceByDropoutRateByDistrictMap.containsKey(districtName)) {
                stringPerformanceByDropoutRateByDistrictMap.put(districtName, new ArrayList<PerformanceByDropoutRateByDistrict>());
            }
            stringPerformanceByDropoutRateByDistrictMap.get(districtName).add(performanceByDropoutRateByDistrict);

        }
        return stringPerformanceByDropoutRateByDistrictMap;
    }

    public PerformanceByDisrictReport aggregateReport(List<PerformanceByDropoutRateByDistrict> performanceByDropoutRateByDistrictList) {
        PerformanceByDisrictReport performanceByDisrictReport = new PerformanceByDisrictReport();
        Long total_target = 0l;
        Long total_bcg_vaccinated = 0l;
        Long total_dtp1_vaccinated = 0l;
        Long total_mr_vaccinated = 0l;
        Long total_dtp3_vaccinated = 0l;
        Long total_bcg_mr_dropout = 0l;
        for (PerformanceByDropoutRateByDistrict performanceByDropoutRateByDistrict : performanceByDropoutRateByDistrictList) {

            total_target = total_target + performanceByDropoutRateByDistrict.getTarget();
            total_bcg_vaccinated = total_bcg_vaccinated + performanceByDropoutRateByDistrict.getBcg_vaccinated();
            total_dtp1_vaccinated = total_dtp1_vaccinated + performanceByDropoutRateByDistrict.getDtp1_vaccinated();
            total_mr_vaccinated = total_mr_vaccinated + performanceByDropoutRateByDistrict.getMr_vaccinated();
            total_dtp3_vaccinated = total_dtp3_vaccinated + performanceByDropoutRateByDistrict.getDtp3_vaccinated();
            total_bcg_mr_dropout = total_bcg_mr_dropout + performanceByDropoutRateByDistrict.getBcg_vaccinated();
        }
        performanceByDisrictReport.setTotal_target(total_target);
        performanceByDisrictReport.setTotal_bcg_vaccinated(total_bcg_vaccinated);
        performanceByDisrictReport.setTotal_dtp1_vaccinated(total_dtp1_vaccinated);
        performanceByDisrictReport.setTotal_mr_vaccinated(total_mr_vaccinated);
        performanceByDisrictReport.setTotal_dtp3_vaccinated(total_dtp3_vaccinated);
        performanceByDisrictReport.setTotal_bcg_mr_dropout(total_bcg_mr_dropout);
        return performanceByDisrictReport;
    }

    public List<Date> extractColumnValues(PerformanceByDropoutRateParam filterParam) {
        String periodStart = filterParam.getPeriod_start_date();
        String periodEnd = filterParam.getPeriod_end_date();
        Date staDate = null;
        Date enDate = null;
        List<Date> columnNames = new ArrayList<>();
        SimpleDateFormat dateFormatStart = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormatEnd = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat monthName = new SimpleDateFormat("MMM yyyy");
        try {
            staDate = dateFormatStart.parse(periodStart);
            enDate = dateFormatEnd.parse(periodEnd);
            while (!staDate.after(enDate)) {
                String colName = monthName.format(staDate);
                columnNames.add(monthName.parse(colName));
                Calendar calendar = Calendar.getInstance();

                calendar.setTime(staDate);
                calendar.add(Calendar.MONTH, 1);
                staDate = calendar.getTime();

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return columnNames;
    }

    public Map<String, Map<Date, Long>> intializeColRangeValues(List<Date> columnNameList) {
        Map<String, Map<Date, Long>> columnRangeValues = new HashMap<>();
        columnRangeValues.put(HIGHER, new HashMap<Date, Long>());
        columnRangeValues.put(AVERAGE, new HashMap<Date, Long>());
        columnRangeValues.put(MIN, new HashMap<Date, Long>());
        columnRangeValues.put(BELOW_MIN, new HashMap<Date, Long>());
        for (int i = 0; i < columnNameList.size(); i++) {
            columnRangeValues.get(HIGHER).put(columnNameList.get(i), 0l);
            columnRangeValues.get(AVERAGE).put(columnNameList.get(i), 0l);
            columnRangeValues.get(MIN).put(columnNameList.get(i), 0l);
            columnRangeValues.get(BELOW_MIN).put(columnNameList.get(i), 0l);
        }
        return columnRangeValues;
    }

    public List<PerformanceByDropOutDistricts> prepareDistrict(Map<String, List<PerformanceByDropoutRateByDistrict>> stringPerformanceByDropoutRateByDistrictMap) {
        List<PerformanceByDropOutDistricts> performanceByDropOutDistrictsList = new ArrayList<>();
        List<String> regionDestrictFacilityNameList = new ArrayList<>();
        if (stringPerformanceByDropoutRateByDistrictMap == null) {
            return performanceByDropOutDistrictsList;
        }

        Set<String> districtKey = stringPerformanceByDropoutRateByDistrictMap.keySet();
        Iterator<String> districtKeyIterator = districtKey.iterator();
        Long totalPopulation = 0l;
        while (districtKeyIterator.hasNext()) {
            String keyValue = districtKeyIterator.next();
            String regionName = stringPerformanceByDropoutRateByDistrictMap.get(keyValue).get(0).getRegion_name();
            String districtName = stringPerformanceByDropoutRateByDistrictMap.get(keyValue).get(0).getDistrict_name();
            String facilityName = stringPerformanceByDropoutRateByDistrictMap.get(keyValue).get(0).getFacility_name();
            PerformanceByDropOutDistricts performanceByDropOutDistricts = new PerformanceByDropOutDistricts();
            Long population = stringPerformanceByDropoutRateByDistrictMap.get(keyValue).get(0).getTarget();
            if (!regionDestrictFacilityNameList.contains(regionName)) {
                performanceByDropOutDistricts.setRegionName(regionName);
                regionDestrictFacilityNameList.add(regionName);
            }
            if (!regionDestrictFacilityNameList.contains(districtName)) {
                performanceByDropOutDistricts.setDistrictName(districtName);
                regionDestrictFacilityNameList.add(districtName);
            }
            if (!regionDestrictFacilityNameList.contains(facilityName)) {
                performanceByDropOutDistricts.setFacilityName(facilityName);
                regionDestrictFacilityNameList.add(facilityName);
            }
            performanceByDropOutDistricts.setPopulation(population);
            totalPopulation = totalPopulation + population;
            performanceByDropOutDistricts.setPerformanceByDropoutRateByDistrictList(stringPerformanceByDropoutRateByDistrictMap.get(keyValue));
            performanceByDropOutDistrictsList.add(performanceByDropOutDistricts);
        }
        return performanceByDropOutDistrictsList;

    }

    public List<PerformanceByDropoutRange> prepareColumn(Map<String, Map<Date, Long>> columnRangeValues) {
        List<PerformanceByDropoutRange> performanceByDropoutColumnList = new ArrayList<>();
        List<String> columnKeySet = new ArrayList<>(columnRangeValues.keySet());
        Collections.sort(columnKeySet);
        Iterator<String> iterator = columnKeySet.iterator();

        while (iterator.hasNext()) {
            String keyVal = iterator.next();
            Map<Date, Long> colRangeVal = columnRangeValues.get(keyVal);
            PerformanceByDropoutRange performanceByDropoutRange = new PerformanceByDropoutRange();
            performanceByDropoutRange.setRangeName(keyVal);
            List<Date> columnsKey = new ArrayList<>(colRangeVal.keySet());
            Collections.sort(columnsKey);
            Iterator<Date> columnIteratorVal = columnsKey.iterator();
            List<PerformanceByDropoutColumn> performanceByDropoutColumn = new ArrayList<>();
            while (columnIteratorVal.hasNext()) {
                Date colName = columnIteratorVal.next();
                Long value = colRangeVal.get(colName);
                PerformanceByDropoutColumn performanceByDropoutColumn1 = new PerformanceByDropoutColumn();
                performanceByDropoutColumn1.setColumnName(colName.toString());
                performanceByDropoutColumn1.setValue(value);
                performanceByDropoutColumn.add(performanceByDropoutColumn1);
            }
            performanceByDropoutRange.setColumns(performanceByDropoutColumn);
            performanceByDropoutColumnList.add(performanceByDropoutRange);
        }
        return performanceByDropoutColumnList;

    }

    public Map<String, Long> intiateColumnRangeValues() {
        Map<String, Long> columnRangeValue = new HashMap<>();
        columnRangeValue.put(BELOW_MIN, 0l);
        columnRangeValue.put(MIN, 0l);
        columnRangeValue.put(AVERAGE, 0l);
        columnRangeValue.put(HIGHER, 0l);
        return columnRangeValue;
    }

    public PerformanceByDropoutRateParam prepareParam(Map<String, String[]> filterCriteria) {
        PerformanceByDropoutRateParam filterParam = null;
        if (filterCriteria != null) {
            filterParam = new PerformanceByDropoutRateParam();
            filterParam.setFacility_id(StringUtils.isBlank(filterCriteria.get("facilityId")[0]) ? 0 : Long.parseLong(filterCriteria.get("facilityId")[0])); //defaults to 0
            filterParam.setGeographic_zone_id(filterCriteria.get("geographicZoneId") == null || StringUtils.isBlank(filterCriteria.get("geographicZoneId")[0]) ? 0 : Long.parseLong(filterCriteria.get("geographicZoneId")[0]));
            filterParam.setPeriod_end_date(StringUtils.isBlank(filterCriteria.get("periodEnd")[0]) ? null : filterCriteria.get("periodEnd")[0]);

            filterParam.setPeriod_start_date(StringUtils.isBlank(filterCriteria.get("periodStart")[0]) ? null : filterCriteria.get("periodStart")[0]);
            filterParam.setProduct_id(filterCriteria.get("productId")==null || StringUtils.isBlank(filterCriteria.get("productId")[0]) ? 0 : Long.parseLong(filterCriteria.get("productId")[0]));

        }
        return filterParam;

    }

    public List<DropoutProduct> loadDropoutProductList() {
        List<DropoutProduct> productList = null;
        productList = this.repository.loadDropoutProductList();
        return productList;
    }
}
