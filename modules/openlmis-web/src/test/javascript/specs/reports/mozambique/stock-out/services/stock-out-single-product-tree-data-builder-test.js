describe("stock out single product tree data builder test", function () {

    var stockoutSingleProductTreeDataBuilder;

    beforeEach(module('openlmis'));

    beforeEach(function () {
        inject(function (StockoutSingleProductTreeDataBuilder) {
            stockoutSingleProductTreeDataBuilder = StockoutSingleProductTreeDataBuilder;
        })
    });

    it("should generate tree data", function () {

        var stockoutEvents = [{
            "location.province_code": "PA",
            "location.province_name": "PA name",

            "location.district_code": "DA",
            "location.district_name": "DA name",

            "facility.facility_code": "FA",
            "facility.facility_name": "FA name",

            "stockout.date": "2016-02-05",
            "stockout.resolved_date": "2016-03-02",

            "overlapped_month": "2016-02-01",
            "overlap_duration": 25
        }, {
            "location.province_code": "PA",
            "location.province_name": "PA name",

            "location.district_code": "DA",
            "location.district_name": "DA name",

            "facility.facility_code": "FA",
            "facility.facility_name": "FA name",

            "stockout.date": "2016-02-05",
            "stockout.resolved_date": "2016-03-02",

            "overlapped_month": "2016-03-01",
            "overlap_duration": 2
        }, {
            "location.province_code": "PA",
            "location.province_name": "PA name",

            "location.district_code": "DA",
            "location.district_name": "DA name",

            "facility.facility_code": "FA2",
            "facility.facility_name": "FA2 name",

            "stockout.date": "2016-02-05",
            "stockout.resolved_date": "2016-02-08",

            "overlapped_month": "2016-02-01",
            "overlap_duration": 4
        }, {
            "location.province_code": "PB",
            "location.province_name": "PB name",

            "location.district_code": "DB",
            "location.district_name": "DB name",

            "facility.facility_code": "FB",
            "facility.facility_name": "FB name",

            "stockout.date": "2016-02-05",
            "stockout.resolved_date": "2016-02-10",

            "overlapped_month": "2016-02-01",
            "overlap_duration": 6
        }];

        var carryStartDates = [
            {
                "location.province_code": "PA",
                "location.province_name": "PA name",

                "location.district_code": "DA",
                "location.district_name": "DA name",

                "facility.facility_code": "FA",
                "facility.facility_name": "FA name",

                "dates.carry_start_date": "2015-12-17"
            }, {
                "location.province_code": "PA",
                "location.province_name": "PA name",

                "location.district_code": "DA",
                "location.district_name": "DA name",

                "facility.facility_code": "FA2",
                "facility.facility_name": "FA2 name",

                "dates.carry_start_date": "2015-11-17"
            }, {
                "location.province_code": "PA",
                "location.province_name": "PA name",

                "location.district_code": "DA2",
                "location.district_name": "DA2 name",

                "facility.facility_code": "FA3",
                "facility.facility_name": "FA3 name",

                "dates.carry_start_date": "2015-11-17"
            }, {
                "location.province_code": "PB",
                "location.province_name": "PB name",

                "location.district_code": "DB",
                "location.district_name": "DB name",

                "facility.facility_code": "FB",
                "facility.facility_name": "FB name",

                "dates.carry_start_date": "2015-10-17"
            }, {
                "location.province_code": "PB",
                "location.province_name": "PB name",

                "location.district_code": "DB",
                "location.district_name": "DB name",

                "facility.facility_code": "FB2",
                "facility.facility_name": "FB2 name",

                "dates.carry_start_date": "2015-9-17"
            }
        ];

        var treeData = stockoutSingleProductTreeDataBuilder.buildTreeData(stockoutEvents, carryStartDates);

        expect(treeData).toEqual([{
            name: "PA name",
            avgDuration: '15.5',
            totalOccurrences: 2,
            totalDuration: 31,
            provinceCode: "PA",
            children: [
                {
                    name: "DA name",
                    avgDuration: '15.5',
                    totalOccurrences: 2,
                    totalDuration: 31,
                    districtCode: "DA",
                    children: [{
                        name: "FA name",
                        avgDuration: '27.0',
                        totalOccurrences: 1,
                        totalDuration: 27,
                        incidents: "2016-02-05 to 2016-03-02",
                        facilityCode: "FA"
                    }, {
                        name: "FA2 name",
                        avgDuration: '4.0',
                        totalOccurrences: 1,
                        totalDuration: 4,
                        incidents: "2016-02-05 to 2016-02-08",
                        facilityCode: "FA2"
                    }]
                },
                {
                    name: "DA2 name",
                    avgDuration: 0,
                    totalOccurrences: 0,
                    totalDuration: 0,
                    districtCode: "DA2",
                    children: [{
                        name: "FA3 name",
                        avgDuration: 0,
                        totalOccurrences: 0,
                        totalDuration: 0,
                        incidents: "",
                        facilityCode: "FA3"
                    }]
                }
            ]
        }, {
            name: "PB name",
            avgDuration: '6.0',
            totalOccurrences: 1,
            totalDuration: 6,
            provinceCode: "PB",
            children: [
                {
                    name: "DB name",
                    avgDuration: '6.0',
                    totalOccurrences: 1,
                    totalDuration: 6,
                    districtCode: "DB",
                    children: [{
                        name: "FB name",
                        avgDuration: '6.0',
                        totalOccurrences: 1,
                        totalDuration: 6,
                        incidents: "2016-02-05 to 2016-02-10",
                        facilityCode: "FB"
                    }, {
                        name: "FB2 name",
                        avgDuration: 0,
                        totalOccurrences: 0,
                        totalDuration: 0,
                        incidents: "",
                        facilityCode: "FB2"
                    }]
                }]
        }]);
    });
});