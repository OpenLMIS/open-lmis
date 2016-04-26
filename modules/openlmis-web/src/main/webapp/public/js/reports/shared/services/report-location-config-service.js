services.factory('ReportLocationConfigService', function () {

    var getUserSelectedLocationConfig = function (province, district) {
        var isOneDistrict = province !== undefined && district !== undefined;
        var isOneProvince = province !== undefined && district === undefined;
        var isAllProvinces = province === undefined && district === undefined;
        return {isOneDistrict: isOneDistrict, isOneProvince: isOneProvince, isAllProvinces: isAllProvinces};
    };

    return {getUserSelectedLocationConfig: getUserSelectedLocationConfig};
});