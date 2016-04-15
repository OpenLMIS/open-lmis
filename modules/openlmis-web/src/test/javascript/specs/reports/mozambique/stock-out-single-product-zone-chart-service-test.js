describe("stock out single product zone chart service test", function () {

    var stockoutSingleProductZoneChartService;

    var stockOuts = [
        {
            "facility.facility_code": "HF1",
            "facility.facility_name": "HF1 name",
            "location.district_code": "Z1",
            "location.province_code": "P1",
            "stockout.date": "2015-12-01",
            "stockout.resolved_date": "2016-01-01"
        }, {
            "facility.facility_code": "HF1",
            "facility.facility_name": "HF1 name",
            "location.district_code": "Z1",
            "location.province_code": "P1",
            "stockout.date": "2016-01-01",
            "stockout.resolved_date": "2016-01-02"
        }, {
            "facility.facility_code": "HF2",
            "facility.facility_name": "HF2 name",
            "location.province_code": "P1",
            "location.district_code": "Z1",
            "stockout.date": "2016-01-02",
            "stockout.resolved_date": "2016-01-04"
        }, {
            "facility.facility_code": "HF3",
            "facility.facility_name": "HF3 name",
            "location.province_code": "P1",
            "location.district_code": "Z1",
            "stockout.date": "2016-02-05",
            "stockout.resolved_date": "2016-04-04"
        }];

    var carryStartDates = [{
        "facility.facility_code": "HF1",
        "facility.facility_name": "HF1 name",

        "location.district_code": "Z1",
        "location.province_code": "P1",
        "dates.carry_start_date": "2015-12-17"
    }, {
        "facility.facility_code": "HF2",
        "facility.facility_name": "HF2 name",
        "location.district_code": "Z1",
        "location.province_code": "P1",
        "dates.carry_start_date": "2015-12-17"
    }, {
        "facility.facility_code": "HF3",
        "facility.facility_name": "HF3 name",
        "location.district_code": "Z1",
        "location.province_code": "P1",
        "dates.carry_start_date": "2015-12-17"
    }, {
        "facility.facility_code": "HF4",
        "facility.facility_name": "HF4 name",
        "location.district_code": "Z1",
        "location.province_code": "P1",
        "dates.carry_start_date": "2015-12-17"
    }];

    beforeEach(module('openlmis'));

    beforeEach(function () {
        inject(function (StockoutSingleProductZoneChartService) {
            stockoutSingleProductZoneChartService = StockoutSingleProductZoneChartService;
        })
    });

    it("should generate district chart data items", function () {
        var zone = {zoneCode: "Z1", zonePropertyName: "location.district_code"};

        var generatedChartDataItems = stockoutSingleProductZoneChartService.generateChartDataItemsForZone(zone, new Date("2016-01-01"), new Date("2016-01-02"), stockOuts, carryStartDates);

        expect(generatedChartDataItems).toEqual([{
            date: new Date("2016-01-01"),
            percentage: '25',
            stockOutFacilities: ["HF1 name"],
            carryingFacilities: ["HF1 name", "HF2 name", "HF3 name", "HF4 name"]
        }, {
            date: new Date("2016-01-02"),
            percentage: '50',
            stockOutFacilities: ["HF1 name", "HF2 name"],
            carryingFacilities: ["HF1 name", "HF2 name", "HF3 name", "HF4 name"]
        }]);
    });

    it("should generate province chart data items", function () {
        var zone = {zoneCode: "P1", zonePropertyName: "location.province_code"};

        var generatedChartDataItems = stockoutSingleProductZoneChartService.generateChartDataItemsForZone(zone, new Date("2016-01-01"), new Date("2016-01-02"), stockOuts, carryStartDates);

        expect(generatedChartDataItems).toEqual([{
            date: new Date("2016-01-01"),
            percentage: '25',
            stockOutFacilities: ["HF1 name"],
            carryingFacilities: ["HF1 name", "HF2 name", "HF3 name", "HF4 name"]
        }, {
            date: new Date("2016-01-02"),
            percentage: '50',
            stockOutFacilities: ["HF1 name", "HF2 name"],
            carryingFacilities: ["HF1 name", "HF2 name", "HF3 name", "HF4 name"]
        }]);
    });

    it("should generate chart data items for all provinces", function () {
        var zone = undefined;

        var generatedChartDataItems = stockoutSingleProductZoneChartService.generateChartDataItemsForZone(zone, new Date("2016-01-01"), new Date("2016-01-02"), stockOuts, carryStartDates);

        expect(generatedChartDataItems).toEqual([{
            date: new Date("2016-01-01"),
            percentage: '25',
            stockOutFacilities: ["HF1 name"],
            carryingFacilities: ["HF1 name", "HF2 name", "HF3 name", "HF4 name"]
        }, {
            date: new Date("2016-01-02"),
            percentage: '50',
            stockOutFacilities: ["HF1 name", "HF2 name"],
            carryingFacilities: ["HF1 name", "HF2 name", "HF3 name", "HF4 name"]
        }]);
    });

});