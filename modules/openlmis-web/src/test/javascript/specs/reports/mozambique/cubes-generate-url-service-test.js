describe("cubes generate url service test", function () {

    var cubesGenerateUrlService;
    beforeEach(module('openlmis'));

    beforeEach(function () {
        inject(function (CubesGenerateUrlService) {
            cubesGenerateUrlService = CubesGenerateUrlService;
        })
    });

    it("should generate drill down and cuts parameter for url", function () {
        var cubesName = "xxx";
        var drillDown = "drug";
        var cuts = [{dimension: "facility", values: ["HF1"]},
            {dimension: "drug", values: ["01C01", "08A07"]},
            {dimension: "location", values: [["provinceCode1", "districtCode1"], ["provinceCode2", "districtCode2"]]},
            {dimension: "date", values: ["2015,12,3-2015,12,21"]}];

        expect(cubesGenerateUrlService.generateAggregateUrl(cubesName, drillDown, cuts))
            .toEqual("/cubesreports/cube/xxx/aggregate?drilldown=drug&cut=facility:HF1|drug:01C01;08A07|location:provinceCode1,districtCode1;provinceCode2,districtCode2|date:2015,12,3-2015,12,21");
        expect(cubesGenerateUrlService.generateFactsUrl(cubesName, cuts))
            .toEqual("/cubesreports/cube/xxx/facts?cut=facility:HF1|drug:01C01;08A07|location:provinceCode1,districtCode1;provinceCode2,districtCode2|date:2015,12,3-2015,12,21")
    });

});