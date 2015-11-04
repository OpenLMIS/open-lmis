describe('Report Filter tests', function () {
    var provinces, districts, facities;

    beforeEach(module('openlmis'));
    beforeEach(function () {
        provinces = [{
            "id": 1,
            "name": "Province 1",
            "parent": null,
            "parentId": 73,
            "code": "Province",
            "catchmentPopulation": null,
            "latitude": null,
            "longitude": null,
            "levelId": 6
        }, {
            "id": 2,
            "name": "Province 2",
            "parent": null,
            "parentId": 73,
            "code": "Province",
            "catchmentPopulation": null,
            "latitude": null,
            "longitude": null,
            "levelId": 6
        }
        ];

        districts = [{
            "id": 3,
            "name": "District 1",
            "parent": null,
            "parentId": 1,
            "code": "district",
            "catchmentPopulation": null,
            "latitude": null,
            "longitude": null,
            "levelId": 7
        }, {
            "id": 4,
            "name": "District 2",
            "parent": null,
            "parentId": 2,
            "code": "district",
            "catchmentPopulation": null,
            "latitude": null,
            "longitude": null,
            "levelId": 7
        }];

        facities = [{
                "id": 413,
                "code": "D02",
                "name": "DPS 1",
                "description": "DPS1",
                "geographicZoneId": 1,
                "typeId": 14,
                "sdp": true,
                "active": true,
                "goLiveDate": 1352563200000
            }, {
                "id": 409,
                "code": "F10",
                "name": "Health Facility 1",
                "description": "health facility 1",
                "geographicZoneId": 3,
                "typeId": 12,
                "sdp": true,
                "active": true,
                "goLiveDate": 1352563200000
            }, {
                "id": 410,
                "code": "F20",
                "name": "Health Facility 2",
                "description": "health facility 2",
                "geographicZoneId": 4,
                "typeId": 12,
                "sdp": true,
                "active": true,
                "goLiveDate": 1352563200000
            }, {
                "id": 411,
                "code": "F30",
                "name": "Health Facility 3",
                "description": "health facility 3",
                "geographicZoneId": 4,
                "typeId": 12,
                "sdp": true,
                "active": true,
                "goLiveDate": 1352563200000
            }];
    });

    it('filter the districts in province.', function () {
        var filteredDistricts = (new DistrictFilter())(districts, 1);
        expect(filteredDistricts.length).toBe(1);
    });

    it('filter the province by provinceId.', function () {
        expect((new ProvinceFilter())(provinces, 2).length).toBe(1);
        expect((new ProvinceFilter())(provinces, 3).length).toBe(0);
    });

    it('filter the facilities by provinceId and districtId.', function () {
        expect((new FacilityFilter())(facities,districts,3,null).length).toBe(1);
        expect((new FacilityFilter())(facities,districts,null,1).length).toBe(1);
        expect((new FacilityFilter())(facities,districts,null,2).length).toBe(2);
    });

});