describe("report location config service test", function () {
    var reportLocationConfigService;

    beforeEach(module('openlmis'));
    beforeEach(function () {
        inject(function (ReportLocationConfigService) {
            reportLocationConfigService = ReportLocationConfigService;
        })
    });

    it("should get corresponding location config", function () {
        expect(reportLocationConfigService.getUserSelectedLocationConfig(undefined, undefined)).toEqual({
            isOneDistrict: false,
            isOneProvince: false,
            isAllProvinces: true
        });

        expect(reportLocationConfigService.getUserSelectedLocationConfig({code:'provinceCode'}, undefined)).toEqual({
            isOneDistrict: false,
            isOneProvince: true,
            isAllProvinces: false
        });

        expect(reportLocationConfigService.getUserSelectedLocationConfig({code:'provinceCode'}, {code:'districtCode'})).toEqual({
            isOneDistrict: true,
            isOneProvince: false,
            isAllProvinces: false
        });
    });
});