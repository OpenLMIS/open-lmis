/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.service;

import org.openlmis.report.DataSourceType;
import org.openlmis.report.model.ReportData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public abstract class ReportDataProvider {

    public final List<? extends ReportData> getReportDataByFilterCriteria(Map<String, String[]> params, DataSourceType dataSourceType){

        if(dataSourceType.equals(DataSourceType.BEAN_COLLECTION_DATA_SOURCE)){
            return getBeanCollectionReportData(params);
        }else if (dataSourceType.equals(DataSourceType.RESULT_SET_DATA_SOURCE)){
            return getResultSetReportData(params);
        }

        return null;
    }
    public final List<? extends ReportData> getReportDataByFilterCriteria(Map<String, String[]> params){
        return getReportDataByFilterCriteria(params, DataSourceType.BEAN_COLLECTION_DATA_SOURCE);
    }

    public ReportData getReportFilterData(Map<String, String[]> params){
        return new ReportData() {
            @Override
            public String toString() {
                return "";
            }
        };
    }
    protected abstract List<? extends ReportData> getBeanCollectionReportData(Map<String, String[]> params);
    protected abstract List<? extends ReportData> getResultSetReportData(Map<String, String[]> params);
    public abstract List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(Map<String, String[]> filter , Map<String, String[]> sorter ,int page,int pageSize);

    public String filterDataToString(Map<String, String[]> params) {
        return "";
    }
    public HashMap<String,String> getAdditionalReportData(Map params){
        return null;
    }
}
