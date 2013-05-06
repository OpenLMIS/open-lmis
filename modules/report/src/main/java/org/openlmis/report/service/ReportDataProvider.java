package org.openlmis.report.service;

import org.openlmis.report.DataSourceType;
import org.openlmis.report.model.ReportData;

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
    public ReportData getReportFilterData(final Map<String, String[]> params){
        return new ReportData() {
            @Override
            public String toString() {
                return filterDataToString(params);
            }
        };
    }
    protected abstract List<? extends ReportData> getBeanCollectionReportData(Map<String, String[]> params);
    protected abstract List<? extends ReportData> getResultSetReportData(Map<String, String[]> params);
    public abstract List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(Map<String, String[]> filter , Map<String, String[]> sorter ,int page,int pageSize);
    public abstract int getReportDataCountByFilterCriteria(Map<String, String[]> filter);

    public String filterDataToString(Map<String, String[]> params) {
        return "";
    }
}
