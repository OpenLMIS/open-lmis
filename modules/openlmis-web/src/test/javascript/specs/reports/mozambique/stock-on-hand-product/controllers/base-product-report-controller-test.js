describe("Base Product Report Controller", function () {
  var scope, geoZoneData, levels, httpBackend, dateFilter, facilityData, fullGeoZoneList, cacheFactory,
    dateFormatService;
  
  levels = [{
    "id": 5,
    "code": "national",
    "name": "National",
    "levelNumber": 1
  }, {
    "id": 6,
    "code": "province",
    "name": "Province",
    "levelNumber": 2
  }, {
    "id": 7,
    "code": "district",
    "name": "District",
    "levelNumber": 3
  }];
  
  geoZoneData = {
    "geographic-zones": [{
      "id": 74,
      "name": "Maputo Província",
      "parent": null,
      "parentId": 73,
      "code": "MAPUTO_PROVINCIA",
      "catchmentPopulation": null,
      "latitude": null,
      "longitude": null,
      "levelId": 6
    }, {
      "id": 75,
      "name": "Marracuene",
      "parent": null,
      "parentId": 74,
      "code": "MARRACUENE",
      "catchmentPopulation": null,
      "latitude": null,
      "longitude": null,
      "levelId": 7
    }, {
      "id": 76,
      "name": "Matola",
      "parent": null,
      "parentId": 74,
      "code": "MATOLA",
      "catchmentPopulation": null,
      "latitude": null,
      "longitude": null,
      "levelId": 7
    }, {
      "id": 73,
      "name": "Mozambique",
      "parent": null,
      "parentId": null,
      "code": "MOZ",
      "catchmentPopulation": null,
      "latitude": null,
      "longitude": null,
      "levelId": 5
    }]
  };
  
  facilityData = [{
    code: "HF8",
    id: 1,
    name: "Habel Jafar",
    geographicZoneId: 1
  }, {
    code: "HF3",
    id: 4,
    name: "Machubo",
    geographicZoneId: 2
  }];
  
  fullGeoZoneList = [{
    code: "testDist",
    id: 1,
    name: "testDist",
    parentId: 2
  }, {
    code: "testProv",
    id: 2,
    name: "testProv",
    parentId: 7
  }];
  
  beforeEach(module('openlmis'));
  beforeEach(inject(function (_$httpBackend_, $rootScope, $http, $filter, ProductReportService, $cacheFactory, $timeout, $location, DateFormatService) {
    scope = $rootScope.$new();
    $location.$$path = 'some-report';
    httpBackend = _$httpBackend_;
    dateFilter = $filter('date');
    cacheFactory = $cacheFactory;
    dateFormatService = DateFormatService;
    BaseProductReportController(scope, $filter, ProductReportService, cacheFactory, $timeout, undefined, undefined, undefined, dateFormatService, $location, undefined, undefined);
  }));
  
  it('should get provinces and districts', function () {
    scope.getProvincesAndDistricts(levels, geoZoneData);
    
    expect(scope.fullGeoZoneList.length).toEqual(3);
    expect(scope.provinces.length).toEqual(1);
    expect(scope.districts.length).toEqual(2);
  });
  
  it('should get parent by geoZoneId', function () {
    scope.getProvincesAndDistricts(levels, geoZoneData);
    
    var parentZone = scope.getParent(75);
    expect(parentZone.id).toEqual(74);
    expect(parentZone.name).toEqual("Maputo Província");
  });
  
  it('should fill corresponding district and province when select facility', function () {
    scope.fullGeoZoneList = fullGeoZoneList;
    scope.facilities = facilityData;
    
    scope.reportParams.facilityId = undefined;
    scope.fillGeographicZone();
    expect(scope.reportParams.districtId).toEqual(undefined);
    expect(scope.reportParams.provinceId).toEqual(undefined);
    
    scope.reportParams.facilityId = 1;
    scope.fillGeographicZone();
    
    expect(scope.reportParams.districtId).toEqual(1);
    expect(scope.reportParams.provinceId).toEqual(2);
    
  });
  
  it('should get facility code', function () {
    scope.facilities = facilityData;
    expect(scope.getFacilityByCode('HF3')).toBe(facilityData[1]);
    expect(scope.getFacilityByCode('HF8')).toBe(facilityData[0]);
  });
  
  it('should return true if drug has expiration risk', function () {
    scope.reportParams.endTime = '2016-11-01T05:00:00';
    var entry = {
      expiry_date: '2017-02-28T05:00:00',
      estimated_months: 3.2
    };
    expect(scope.hasExpirationRisk(entry)).toBeTruthy();
    
    scope.reportParams.endTime = '2016-10-01T05:00:00';
    expect(scope.hasExpirationRisk(entry)).toBeFalsy();
  });
  
  it('should set cmm status according to cmm and soh', function () {
    var entry1 = {
      cmm: 10,
      soh: 40
    };
    
    var entry2 = {
      cmm: 10,
      soh: 1
    };
    
    var entry3 = {
      cmm: 100,
      soh: 1
    };
    
    expect(scope.cmmStatus(entry1)).toEqual('over-stock');
    expect(scope.cmmStatus(entry2)).toEqual('regular-stock');
    expect(scope.cmmStatus(entry3)).toEqual('low-stock');
  });
  
  it('should get drug name by code', function () {
    scope.products = [
      {
        "id": 588,
        "code": "08S01Z",
        "primaryName": "ABACAVIR + LAMIVUDINA60mg + 30mg, 60Cps(Baby)Embalagem",
        "description": "ABACAVIR + LAMIVUDINA",
        "strength": "60mg + 30mg, 60Cps(Baby)"
      },
      {
        "id": 1156,
        "code": "23A04",
        "primaryName": "ADESIVO ELáSTICO (10CM X 2CM)SEM DOSAGEMRolo",
        "description": "ADESIVO ELáSTICO (10CM X 2CM)",
        "strength": "SEM DOSAGEM"
      },
      {
        "id": 1152,
        "code": "23A02",
        "primaryName": "ADESIVO ESPARADRAPO (TRANSPORE) (10CM X 10M)SEM DOSAGEMRolo",
        "description": "ADESIVO ESPARADRAPO (TRANSPORE) (10CM X 10M)",
        "strength": "SEM DOSAGEM"
      }
    ];
    
    expect(scope.getDrugByCode('23A04')).toEqual({
      id: 1156,
      code: '23A04',
      primaryName: 'ADESIVO ELáSTICO (10CM X 2CM)SEM DOSAGEMRolo',
      description: 'ADESIVO ELáSTICO (10CM X 2CM)',
      strength: 'SEM DOSAGEM'
    });
  });
});