describe("stock out report calculation service test", function () {

    var stockOutReportCalculationService;

    var stockOuts = [{
        "facility.facility_code": "FA",
        "facility.facility_name": "FA name",

        "stockout.date": "2016-02-05",
        "stockout.resolved_date": "2016-03-02",

        "overlapped_month": "2016-02-01",
        "overlap_duration": 25
    }, {
        "facility.facility_code": "FA",
        "facility.facility_name": "FA name",

        "stockout.date": "2016-03-06",
        "stockout.resolved_date": "2016-03-12",

        "overlapped_month": "2016-03-01",
        "overlap_duration": 7
    }, {
        "facility.facility_code": "FA",
        "facility.facility_name": "FA name",

        "stockout.date": "2016-04-05",
        "stockout.resolved_date": "2016-04-08",

        "overlapped_month": "2016-04-01",
        "overlap_duration": 4
    }, {
        "facility.facility_code": "FA",
        "facility.facility_name": "FA name",

        "stockout.date": "2016-05-05",
        "stockout.resolved_date": "2016-05-10",

        "overlapped_month": "2016-05-01",
        "overlap_duration": 6
    }];

    beforeEach(module('openlmis'));

    beforeEach(function () {
        inject(function (StockOutReportCalculationService) {
            stockOutReportCalculationService = StockOutReportCalculationService;
        })
    });

    it("should calculate stock out result correctly", function () {
        var stockoutResult = stockOutReportCalculationService.calculateStockoutResult(stockOuts, 10);

        expect(stockoutResult).toEqual({
            avgDuration: '4.2',
            totalOccurrences: 10,
            totalDuration: 42
        });
    });

    it("should calculate incidents", function () {
        var incidentsResult = stockOutReportCalculationService.generateIncidents(stockOuts);

        expect(incidentsResult).toEqual(['2016-02-05 to 2016-03-02', '2016-03-06 to 2016-03-12', '2016-04-05 to 2016-04-08', '2016-05-05 to 2016-05-10']);
    });

});