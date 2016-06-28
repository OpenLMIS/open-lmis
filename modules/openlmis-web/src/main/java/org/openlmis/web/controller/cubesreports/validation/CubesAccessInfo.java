package org.openlmis.web.controller.cubesreports.validation;

import lombok.Getter;
import org.openlmis.core.domain.moz.MozFacilityTypes;

import static org.openlmis.core.domain.moz.MozFacilityTypes.*;

@Getter
public class CubesAccessInfo {
    private MozFacilityTypes currentUserFacilityType;
    private String facility;
    private String district;
    private String province;

    public static CubesAccessInfo createInstance(MozFacilityTypes currentUserFacilityType, String cubesQueryString) {
        CubesAccessInfo cubesAccessInfo = new CubesAccessInfo();
        cubesAccessInfo.currentUserFacilityType = currentUserFacilityType;

        assignLocations(cubesQueryString, cubesAccessInfo);

        return cubesAccessInfo;
    }

    public boolean isLocationInfoMissing() {
        if (currentUserFacilityType == DNM) {
            return false;
        } else if (currentUserFacilityType == DPM) {
            return province == null;
        } else if (currentUserFacilityType == DDM) {
            return district == null;
        } else {
            return facility == null;
        }
    }

    private static void assignLocations(String queryString, CubesAccessInfo cubesAccessInfo) {
        if (queryString.contains("cut=")) {
            String[] dimensions = queryString.split("cut=")[1].split("\\|");

            for (String dimension : dimensions) {
                if (dimension.startsWith("facility:")) {
                    cubesAccessInfo.facility = dimension.split(":")[1];
                } else if (dimension.startsWith("location:")) {
                    assignProvinceAndDistrict(cubesAccessInfo, dimension);
                }
            }
        }
    }

    private static void assignProvinceAndDistrict(CubesAccessInfo cubesAccessInfo, String dimension) {
        String provinceAndDistrictInString = dimension.split(":")[1];
        String[] provinceAndDistrict = provinceAndDistrictInString.split(",");
        cubesAccessInfo.province = provinceAndDistrict[0];
        if (provinceAndDistrict.length > 1) {
            cubesAccessInfo.district = provinceAndDistrict[1];
        }
    }
}
