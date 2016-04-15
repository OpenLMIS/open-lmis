describe("tracer drugs chart service test", function () {

    var tracerDrugsChartService, httpBackend;

    beforeEach(module('openlmis'));

    beforeEach(function () {
        inject(function (TracerDrugsChartService, _$httpBackend_) {
            tracerDrugsChartService = TracerDrugsChartService;
            httpBackend = _$httpBackend_;
        });
    });

    // it("test when all provinces or all districts");
    iit("should generate chart data items for tracer drugs", function () {
        var tracerDrugs = [
            {
                drug: "code1"
            },
            {
                drug: "code2"
            }
        ];

        var stockOuts = [{
            "facility.facility_code": "HF1",
            "facility.facility_name": "HF1 name",
            "location.district_code": "D1",
            "drug.drug_code": "code1",
            "drug.drug_name": "drug1",
            "stockout.date": "2015-12-31",
            "stockout.resolved_date": "2016-01-02"
        }, {
            "facility.facility_code": "HF2",
            "facility.facility_name": "HF2 name",
            "location.district_code": "D1",
            "drug.drug_code": "code1",
            "drug.drug_name": "drug1",
            "stockout.date": "2016-01-03",
            "stockout.resolved_date": "2016-01-04"
        }, {
            "facility.facility_code": "HF3",
            "facility.facility_name": "HF3 name",
            "location.district_code": "D1",
            "drug.drug_code": "code2",
            "drug.drug_name": "drug2",
            "stockout.date": "2016-01-07",
            "stockout.resolved_date": "2016-01-09"
        }];

        var carryStartDates = [{
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

        var tracerDrugChartDataItems = tracerDrugsChartService.generateTracerDrugsChartDataItems(tracerDrugs, stockOuts, carryStartDates, new Date("2015-12-31"), new Date("2016-01-09"), "P1", "D1");

        expect(tracerDrugChartDataItems).toEqual([
            {
                date: new Date("2016-01-01"),

                code1: 50,
                code1StockOutFacilities: ["HF1 name"],
                code1CarryingFacilities: ["HF1 name", "HF2 name"],

                code2: 100,
                code2StockOutFacilities: [],
                code2CarryingFacilities: ["HF3 name", "HF4 name"],

                average: "75"
            }, {
                date: new Date("2016-01-08"),

                code1: 100,
                code1StockOutFacilities: [],
                code1CarryingFacilities: ["HF1 name", "HF2 name"],

                code2: 50,
                code2StockOutFacilities: ["HF3 name"],
                code2CarryingFacilities: ["HF3 name", "HF4 name"],

                average: "75"
            }
        ]);
 
        //1. average 2. time format
    })
});
