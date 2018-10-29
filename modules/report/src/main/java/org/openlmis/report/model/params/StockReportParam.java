package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.report.annotations.RequiredParam;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockReportParam {

    private static Logger logger = Logger.getLogger(StockReportParam.class);
    @RequiredParam
    private Date endTime;

    private Integer provinceId;
    private Integer districtId;
    private Integer facilityId;
    private String productCode;
    private String provinceCode;
    private String districtCode;

    private FilterCondition filterCondition;

    public void setValue(Map<Object, Object> paraMap) {
        try {
            setEndTime(DateUtil.parseDate(paraMap.get("endTime").toString()));
            if (isValidParam(paraMap,"provinceId"))
            {
                setProvinceId(Integer.parseInt(paraMap.get("provinceId").toString()));
            }
            if (isValidParam(paraMap, "districtId")) {
                setDistrictId(Integer.parseInt(paraMap.get("districtId").toString()));
            }
            if (isValidParam(paraMap,"facilityId")) {
                setFacilityId(Integer.parseInt(paraMap.get("facilityId").toString().trim()));
            }
            if (isValidParam(paraMap, "productCode")) {
                setProductCode(paraMap.get("productCode").toString().trim());
            }
        }
        catch (Throwable e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }


    private boolean isValidParam(Map<Object, Object> paraMap, String key) {
        return null != paraMap.get(key)
                && StringUtils.isNotEmpty(paraMap.get(key).toString().trim());
    }

    public interface FilterCondition {
        public String getCondition();
    }
}