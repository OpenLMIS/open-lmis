describe("Stock Out Single Product Report Controller", function () {
    var scope, provinceData, httpBackend, stockOutReportData, facilityData, districtData;

    facilityData = [

        {
            "id": 2,
            "code": "HF1",
            "name": "Marracuene",
            "geographicZoneId": 3,
        }, {
            "id": 3,
            "code": "HF2",
            "name": "Matalane",
            "geographicZoneId": 4,
        },{
            "id": 1,
            "code": "HF3",
            "name": "facility3",
            "geographicZoneId": 5,
        } ,{
            "id": 4,
            "code": "HF4",
            "name": "Michafutene",
            "geographicZoneId": 3,
        }, {
            "id": 5,
            "code": "HF5",
            "name": "Nhongonhane (Ed.Mondl.)",
            "geographicZoneId": 2,
        }];

    provinceData = [
        {
            code: "MAPUTO_PROVINCIA",
            id: 1,
            levelId: 2,
            name: "Maputo Província",
            parentId: 3
        },
        {
            code: "TestProvince",
            id: 2,
            levelId: 2,
            name: "TestProvince",
            parentId: 3
        }];

    districtData = [{
        id: 3,
        code: "MARRACUENE",
        name: "Marracuene",
        parentId: 1
    }, {
        id: 4,
        code: "MARRACUENE2",
        name: "Marracuene2",
        parentId: 1
    }, {
        id: 5,
        code: "MARRACUENE3",
        name: "Marracuene3",
        parentId: 1
    }, {
        id: 6,
        code: "MARRACUENE4",
        name: "Marracuene4",
        parentId: 2
    }];

    stockOutReportData = [
        {
            "drug.drug_code": "07A06",
            "location.province_code": "MAPUTO_PROVINCIA",
            "drug.drug_name": "Paracetamol120mg/5mLXarope",
            "location.province_name": "Maputo Província",
            "facility.facility_name": "Marracuene",
            "overlap_duration": 10,
            "location.district_code": "MARRACUENE",
            "stockout.date": "2016-01-13",
            "facility.facility_code": "HF1",
            "stockout.resolved_date": "2016-01-22",
            "overlapped_month": "2016-01-01",
            "location.district_name": "Marracuene"
        },
        {
            "drug.drug_code": "07A06",
            "location.province_code": "MAPUTO_PROVINCIA",
            "drug.drug_name": "Paracetamol120mg/5mLXarope",
            "location.province_name": "Maputo Província",
            "facility.facility_name": "facility2",
            "overlap_duration": 20,
            "location.district_code": "MARRACUENE",
            "stockout.date": "2016-01-03",
            "facility.facility_code": "HF2",
            "stockout.resolved_date": "2016-01-22",
            "overlapped_month": "2016-01-01",
            "location.district_name": "Marracuene"
        },
        {
            "drug.drug_code": "07A06",
            "location.province_code": "MAPUTO_PROVINCIA",
            "drug.drug_name": "Paracetamol120mg/5mLXarope",
            "location.province_name": "Maputo Província",
            "facility.facility_name": "facility3",
            "overlap_duration": 20,
            "location.district_code": "MARRACUENE2",
            "stockout.date": "2016-01-03",
            "facility.facility_code": "HF3",
            "stockout.resolved_date": "2016-01-22",
            "overlapped_month": "2016-01-01",
            "location.district_name": "Marracuene2"
        },
        {
            "drug.drug_code": "07A06",
            "location.province_code": "MAPUTO_PROVINCIA",
            "drug.drug_name": "Paracetamol120mg/5mLXarope",
            "location.province_name": "Maputo Província",
            "facility.facility_name": "facility4",
            "overlap_duration": 30,
            "location.district_code": "MARRACUENE3",
            "stockout.date": "2016-01-01",
            "facility.facility_code": "HF4",
            "stockout.resolved_date": "2016-01-30",
            "overlapped_month": "2016-01-01",
            "location.district_name": "Marracuene3"
        },
        {
            "drug.drug_code": "07A06",
            "location.province_code": "TestProvince",
            "drug.drug_name": "Paracetamol120mg/5mLXarope",
            "location.province_name": "TestProvinceName",
            "facility.facility_name": "Marracuene",
            "overlap_duration": 20,
            "location.district_code": "MARRACUENE4",
            "stockout.date": "2016-01-12",
            "facility.facility_code": "HF5",
            "stockout.resolved_date": "2016-03-28",
            "overlapped_month": "2016-01-01",
            "location.district_name": "Marracuene4"
        }
    ];

    beforeEach(module('openlmis'));
    beforeEach(module('ui.bootstrap.dialog'));
    beforeEach(inject(function (_$httpBackend_, $rootScope, $http, $controller) {
        scope = $rootScope.$new();
        httpBackend = _$httpBackend_;

        $controller(StockOutSingleProductReportController, {$scope: scope});
    }));

    it('should load stock out single product report successfully', function () {
        scope.provinces = provinceData;
        scope.districts = districtData;
        scope.facilities = facilityData;
        scope.reportParams = {
            provinceId: "1",
            districtId: "4",
            facilityId: "1",
            startTime: "2016-01-01",
            endTime: "2016-03-28"
        };
        scope.reportParams.product = '{"code": "O7A06"}';

        httpBackend.expectGET("/cubesreports/cube/vw_stockouts/facts?cut=overlapped_date:2016,01,01-2016,03,28|drug:O7A06").respond(200, stockOutReportData);
        scope.loadReport();
        httpBackend.flush();


        expect(scope.tree_data.length).toBe(2);
        var provinceResult = scope.tree_data[0];
        expect(provinceResult.name).toEqual('Maputo Província');
        expect(provinceResult.monthlyAvg).toBe(5);
        expect(provinceResult.monthlyOccurrences).toBe(1);
        expect(provinceResult.totalDuration).toBe(80);
        expect(provinceResult.incidents).toBe("");

        expect(provinceResult.children.length).toBe(3);
        var districtResult = provinceResult.children[0];
        expect(districtResult.name).toEqual('Marracuene');
        expect(districtResult.monthlyAvg).toBe(7.5);
        expect(districtResult.monthlyOccurrences).toBe(1);
        expect(districtResult.totalDuration).toBe(30);
        expect(districtResult.incidents).toBe("");

        expect(districtResult.children.length).toBe(2);
        var facilityResult = districtResult.children[0];
        expect(facilityResult.name).toEqual('Marracuene');
        expect(facilityResult.monthlyAvg).toBe(10);
        expect(facilityResult.monthlyOccurrences).toBe(1);
        expect(facilityResult.totalDuration).toBe(10);
        expect(facilityResult.incidents).toBe("2016-01-13to2016-01-22");
    });

});