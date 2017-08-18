services.factory('ReportLocationConfigService', function () {

    var getUserSelectedLocationConfig = function (province, district) {
        var isOneDistrict = province !== undefined && district !== undefined;
        var isOneProvince = province !== undefined && district === undefined;
        var isAllProvinces = province === undefined && district === undefined;
        return {isOneDistrict: isOneDistrict, isOneProvince: isOneProvince, isAllProvinces: isAllProvinces};
    };

    var getLocationHierarchy = function (province, district) {
      var config = getUserSelectedLocationConfig(province, district);
      if (config.isOneDistrict) {
          return province.code + ',' + district.code;
      } else if (config.isOneProvince) {
          return province.code;
      } else {
          return null;
      }
    };

    var getZone = function(province, district) {
        var locationConfig = getUserSelectedLocationConfig(province, district);

        if (locationConfig.isOneDistrict) {
            return {
                zoneCode: district.code,
                zonePropertyName: "location.district_code"
            };
        }
        else if (locationConfig.isOneProvince) {
            return {
                zoneCode: province.code,
                zonePropertyName: "location.province_code"
            };
        }
        else if (locationConfig.isAllProvinces) {
            return undefined;
        }
    };

    return {
        getUserSelectedLocationConfig: getUserSelectedLocationConfig,
        getZone: getZone,
        getLocationHierarchy: getLocationHierarchy
    };
});