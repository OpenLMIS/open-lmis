describe("tracer drugs chart service test", function () {

    var tracerDrugsChartService, httpBackend;

    var tracerDrugs = [
        {
            "drug.drug_code": "code2",
            "drug.drug_name": "drug name2"
        },
        {
            "drug.drug_code": "code1",
            "drug.drug_name": "drug name1"
        },
        {
            "drug.drug_code": "noCarrierCode",
            "drug.drug_name": "noCarrierName"
        }
    ];

    var stockOuts = [
        {
            "facility.facility_code": "HF1",
            "facility.facility_name": "HF1 name",
            "location.province_code": "P1",
            "location.district_code": "D1",
            "drug.drug_code": "code1",
            "drug.drug_name": "drug1",
            "stockout.date": "2015-12-31",
            "stockout.resolved_date": "2016-01-02"
        }, {
            "facility.facility_code": "HF2",
            "facility.facility_name": "HF2 name",
            "location.province_code": "P1",
            "location.district_code": "D1",
            "drug.drug_code": "code1",
            "drug.drug_name": "drug1",
            "stockout.date": "2016-01-03",
            "stockout.resolved_date": "2016-01-04"
        }, {
            "facility.facility_code": "HF3",
            "facility.facility_name": "HF3 name",
            "location.province_code": "P1",
            "location.district_code": "D1",
            "drug.drug_code": "code2",
            "drug.drug_name": "drug2",
            "stockout.date": "2016-01-07",
            "stockout.resolved_date": "2016-01-09"
        }
    ];

    var carryStartDates = [
        {
            "location.province_code": "P1",
            "location.district_code": "D1",
            "facility.facility_code": "HF1",
            "facility.facility_name": "HF1 name",
            "drug.drug_code": "code1",
            "dates.carry_start_date": "2015-12-17"
        }, {
            "location.province_code": "P1",
            "location.district_code": "D1",
            "facility.facility_code": "HF2",
            "facility.facility_name": "HF2 name",
            "drug.drug_code": "code1",
            "dates.carry_start_date": "2015-12-17"
        }, {
            "location.province_code": "P1",
            "location.district_code": "D1",
            "facility.facility_code": "HF3",
            "facility.facility_name": "HF3 name",
            "drug.drug_code": "code2",
            "dates.carry_start_date": "2015-12-17"
        }, {
            "location.province_code": "P1",
            "location.district_code": "D1",
            "facility.facility_code": "HF4",
            "facility.facility_name": "HF4 name",
            "drug.drug_code": "code2",
            "dates.carry_start_date": "2015-12-17"
        }
    ];

    var expectedTracerDrugChartDataItems = [
        {
            date: new Date("2016-01-01"),

            code1: 50,
            code1StockOutFacilities: ["HF1 name"],
            code1CarryingFacilities: ["HF1 name", "HF2 name"],

            code2: 100,
            code2StockOutFacilities: [],
            code2CarryingFacilities: ["HF3 name", "HF4 name"],

            noCarrierCode: 0,
            noCarrierCodeStockOutFacilities: [],
            noCarrierCodeCarryingFacilities: [],

            average: "50"
        }, {
            date: new Date("2016-01-08"),

            code1: 100,
            code1StockOutFacilities: [],
            code1CarryingFacilities: ["HF1 name", "HF2 name"],

            code2: 50,
            code2StockOutFacilities: ["HF3 name"],
            code2CarryingFacilities: ["HF3 name", "HF4 name"],

            noCarrierCode: 0,
            noCarrierCodeStockOutFacilities: [],
            noCarrierCodeCarryingFacilities: [],

            average: "50"
        }
    ];

    beforeEach(module('openlmis'));

    beforeEach(function () {
        inject(function (TracerDrugsChartService, _$httpBackend_) {
            tracerDrugsChartService = TracerDrugsChartService;
            httpBackend = _$httpBackend_;
        });
    });

    it("should generate chart data items for tracer drugs for all provinces", function () {
        var tracerDrugChartDataItems = tracerDrugsChartService.generateTracerDrugsChartDataItems(tracerDrugs, stockOuts, carryStartDates, new Date("2015-12-31"), new Date("2016-01-09"), undefined, undefined);
        expect(tracerDrugChartDataItems).toEqual(expectedTracerDrugChartDataItems);
    });

    it("should generate chart data items for tracer drugs for one province", function () {
        var tracerDrugChartDataItems = tracerDrugsChartService.generateTracerDrugsChartDataItems(tracerDrugs, stockOuts, carryStartDates, new Date("2015-12-31"), new Date("2016-01-09"), {code: "P1"}, undefined);
        expect(tracerDrugChartDataItems).toEqual(expectedTracerDrugChartDataItems);
    });

    it("should generate chart data items for tracer drugs for one district", function () {
        var tracerDrugChartDataItems = tracerDrugsChartService.generateTracerDrugsChartDataItems(tracerDrugs, stockOuts, carryStartDates, new Date("2015-12-31"), new Date("2016-01-09"), {code: "P1"}, {code: "D1"});
        expect(tracerDrugChartDataItems).toEqual(expectedTracerDrugChartDataItems);
    });

    it("should generate graphs for tracer drugs", function () {
        var graphs = tracerDrugsChartService.generateGraphs(tracerDrugs);

        var joc = jasmine.objectContaining;
        expect(graphs).toEqual(joc([
            joc({
                id: "average",
                lineColor: "red"
            }),
            joc({
                title: "drug name1[code1]",
                valueField: "code1"
            }),
            joc({
                title: "drug name2[code2]",
                valueField: "code2"
            }),
            joc({
                title: "noCarrierName[noCarrierCode]",
                valueField: "noCarrierCode"
            }),
            joc({
                id: "all"
            })]));
    })
});