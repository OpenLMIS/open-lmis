describe("cubes generate cut params service test", function () {

    var cubesGenerateCutParamsService, facility, drugs, province, district;

    facility = {
        code: 'facilityCode'
    };

    province = {
        code: 'provinceCode'
    };

    district = {
        code: 'districtCode'
    };

    drugs = [
        {'drug.drug_code': 'drugCode1'},
        {'drug.drug_code': 'drugCode2'}
    ];

    beforeEach(module('openlmis'));

    beforeEach(function () {
        inject(function (CubesGenerateCutParamsService) {
            cubesGenerateCutParamsService = CubesGenerateCutParamsService;
        })
    });

    it("should generate cut params with time range and location", function () {
        expect(cubesGenerateCutParamsService.generateCutsParams("cutDate", "2015,10,01", "2016,04,01", undefined, undefined, province, district))
            .toEqual([
                {
                    dimension: 'cutDate',
                    values: ['2015,10,01-2016,04,01'],
                    skipEscape : true
                },
                {
                    dimension: 'location',
                    values: [['provinceCode', 'districtCode']]
                }]);
    });

    it("should generate cut params with endTime and drug", function () {
        expect(cubesGenerateCutParamsService.generateCutsParams("carry_start", undefined, "2016,04,01", undefined, drugs, undefined, undefined))
            .toEqual([
                {
                    dimension: 'carry_start',
                    values: ['-2016,04,01'],
                    skipEscape : true
                },
                {
                    dimension: 'drug',
                    values: ['drugCode1', 'drugCode2']
                }]);
    });

    it("should generate cut params with date range and facility and location", function () {
        expect(cubesGenerateCutParamsService.generateCutsParams("overlapped_date", "2015,10,01", "2016,04,01", facility, drugs, province, district))
            .toEqual([
                {
                    dimension: 'overlapped_date',
                    values: ['2015,10,01-2016,04,01'],
                    skipEscape : true
                },
                {
                    dimension: 'facility',
                    values: ['facilityCode']
                },
                {
                    dimension: 'drug',
                    values: ['drugCode1', 'drugCode2']
                },
                {
                    dimension: 'location',
                    values: [['provinceCode', 'districtCode']]
                }]);
    });

    it("should generate cut params with date range and drug and location", function () {
        expect(cubesGenerateCutParamsService.generateCutsParams("overlapped_date", "2015,10,01", "2016,04,01", undefined, drugs, province, district))
            .toEqual([
                {
                    dimension: 'overlapped_date',
                    values: ['2015,10,01-2016,04,01'],
                    skipEscape : true
                },
                {
                    dimension: 'drug',
                    values: ['drugCode1', 'drugCode2']
                }, {
                    dimension: 'location',
                    values: [['provinceCode', 'districtCode']]
                }]);
    });


});