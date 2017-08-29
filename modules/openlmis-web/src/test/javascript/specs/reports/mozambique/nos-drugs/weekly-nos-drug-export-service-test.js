describe("Weekly NOS drugs excel export test", function () {

  var weeklyNosDrugExportService, httpBackend, reportExportExcelService, messageService;

  var nosDrugs = ['08K04', '05A01'];

  var weeklyNosDrugSOH = [
    {
      "drug.drug_code": "08K04",
      "drug.drug_name": "drug name1",
      "location.province_name": "Maputo Prov\u00edncia",
      "facility.facility_name": "Mumemo",
      "soh": "50",
      "facility.facility_code": "HF1",
      "date": "2017-08-04",
      "location.district_name": "Marracuene",
      "area.area_name": "MMIA",
      "area.sub_area_name": "MMIA"
    },
    {
      "drug.drug_code": "08K04",
      "drug.drug_name": "drug name1",
      "location.province_name": "Maputo Prov\u00edncia",
      "facility.facility_name": "Mumemo",
      "soh": "30",
      "facility.facility_code": "HF1",
      "date": "2017-08-11",
      "location.district_name": "Marracuene",
      "area.area_name": "MMIA",
      "area.sub_area_name": "MMIA"
    },
    {
      "drug.drug_code": "08K04",
      "drug.drug_name": "drug name1",
      "location.province_name": "Maputo Prov\u00edncia",
      "facility.facility_name": "Mumemo",
      "soh": "10",
      "facility.facility_code": "HF1",
      "date": "2017-08-18",
      "location.district_name": "Marracuene",
      "area.area_name": "MMIA",
      "area.sub_area_name": "MMIA"
    },
    {
      "drug.drug_code": "05A01",
      "drug.drug_name": "drug name2",
      "location.province_name": "Maputo Prov\u00edncia",
      "facility.facility_name": "Mumemo",
      "soh": "150",
      "facility.facility_code": "HF1",
      "date": "2017-08-11",
      "location.district_name": "Marracuene",
      "area.area_name": "VIA",
      "area.sub_area_name": "Medicamentos Essenciais"
    },
    {
      "drug.drug_code": "05A01",
      "drug.drug_name": "drug name2",
      "location.province_name": "Maputo Prov\u00edncia",
      "facility.facility_name": "Mumemo",
      "soh": "130",
      "facility.facility_code": "HF1",
      "date": "2017-08-18",
      "location.district_name": "Marracuene",
      "area.area_name": "VIA",
      "area.sub_area_name": "Medicamentos Essenciais"
    },
    {
      "drug.drug_code": "08K04",
      "drug.drug_name": "drug name1",
      "location.province_name": "Maputo Prov\u00edncia",
      "facility.facility_name": "Mumemo2",
      "soh": "300",
      "facility.facility_code": "HF2",
      "date": "2017-08-11",
      "location.district_name": "Marracuene",
      "area.area_name": "MMIA",
      "area.sub_area_name": "MMIA"
    },
    {
      "drug.drug_code": "08K04",
      "drug.drug_name": "drug name1",
      "location.province_name": "Maputo Prov\u00edncia",
      "facility.facility_name": "Mumemo2",
      "soh": "100",
      "facility.facility_code": "HF2",
      "date": "2017-08-18",
      "location.district_name": "Marracuene",
      "area.area_name": "MMIA",
      "area.sub_area_name": "MMIA"
    },
    {
      "drug.drug_code": "05A01",
      "drug.drug_name": "drug name2",
      "location.province_name": "Maputo Prov\u00edncia",
      "facility.facility_name": "Mumemo2",
      "soh": "150",
      "facility.facility_code": "HF2",
      "date": "2017-08-11",
      "location.district_name": "Marracuene",
      "area.area_name": "VIA",
      "area.sub_area_name": "Medicamentos Essenciais"
    },
    {
      "drug.drug_code": "05A01",
      "drug.drug_name": "drug name2",
      "location.province_name": "Maputo Prov\u00edncia",
      "facility.facility_name": "Mumemo2",
      "soh": "30",
      "facility.facility_code": "HF2",
      "date": "2017-08-18",
      "location.district_name": "Marracuene",
      "area.area_name": "VIA",
      "area.sub_area_name": "Medicamentos Essenciais"
    }
  ];

  var lastCMMEntriesForNosDrugs = [
    {
      "location.province_code": "MAPUTO_PROVINCIA",
      "product": "08K04",
      "periodend.day": 20.0,
      "periodend.year": 2017.0,
      "facilityCode": "HF1",
      "periodbegin.day": 21.0,
      "location.district_code": "MARRACUENE",
      "cmm": 50,
      "periodend.month": 8.0,
      "periodbegin.month": 7.0,
      "periodbegin.year": 2017.0,
    },
    {
      "location.province_code": "MAPUTO_PROVINCIA",
      "product": "08K04",
      "periodend.day": 20.0,
      "periodend.year": 2017.0,
      "facilityCode": "HF2",
      "periodbegin.day": 21.0,
      "location.district_code": "MARRACUENE",
      "cmm": 30,
      "periodend.month": 8.0,
      "periodbegin.month": 7.0,
      "periodbegin.year": 2017.0,
    },
    {
      "location.province_code": "MAPUTO_PROVINCIA",
      "product": "05A01",
      "periodend.day": 20.0,
      "periodend.year": 2017.0,
      "facilityCode": "HF2",
      "periodbegin.day": 21.0,
      "location.district_code": "MARRACUENE",
      "cmm": 0,
      "periodend.month": 8.0,
      "periodbegin.month": 7.0,
      "periodbegin.year": 2017.0,
    }
  ];

  beforeEach(module('openlmis'));

  beforeEach(function () {
    inject(function (WeeklyNosDrugExportService, _$httpBackend_, ReportExportExcelService, messageService) {
      weeklyNosDrugExportService = WeeklyNosDrugExportService;
      httpBackend = _$httpBackend_;
      reportExportExcelService = ReportExportExcelService;
      messageService_ = messageService;
    });
  });

  it("should match nos drug report data for all facilities under selected district", function() {
    httpBackend.expectGET('/cubesreports/cube/vw_weekly_nos_soh/facts?cut=cutDate%3A2017%2C07%2C21-2017%2C08%2C20%7Clocation%3AMAPUTO_PROVINCIA%2CMARRACUENE&fields=location.province_name%2Clocation.district_name%2Cfacility.facility_code%2Cfacility.facility_name%2Cdrug.drug_name%2Cdrug.drug_code%2Cdate%2Csoh%2Carea.area_name%2Carea.sub_area_name').respond(200, weeklyNosDrugSOH);
    httpBackend.expectGET('/cubesreports/cube/vw_cmm_entries/facts?cut=product%3A08K04%3B05A01%7Cperiodbegin%3A2017%2C07%2C21%7Cperiodend%3A2017%2C08%2C20%7Clocation%3AMAPUTO_PROVINCIA%2CMARRACUENE').respond(200, lastCMMEntriesForNosDrugs);
    spyOn(reportExportExcelService, 'exportAsXlsx');

    weeklyNosDrugExportService.getDataForExport(undefined, {'code' : 'MAPUTO_PROVINCIA'},{'code': 'MARRACUENE'}, '2017-07-21', '2017-08-20', nosDrugs);

    httpBackend.flush();

    var expectedHeader = {
      drugCode: 'report.header.drug.code',
      area: 'report.header.area',
      subArea: 'report.header.subarea',
      drugName: 'report.header.drug.name',
      province: 'report.header.province',
      district: 'report.header.district',
      facility: 'report.header.facility',
      cmmValue:'report.header.cmm',
      reportGeneratedFor: 'report.header.generated.for',
      '2017-08-04': '2017-08-04',
      '2017-08-11': '2017-08-11',
      '2017-08-18': '2017-08-18'
    };

    var expectedContent = [
      {
        drugCode: '08K04',
        area: 'MMIA',
        subArea: 'MMIA',
        drugName: 'drug name1',
        province: 'Maputo Prov\u00edncia',
        district: 'Marracuene',
        facility: 'Mumemo',
        reportGeneratedFor: '20-07-2017 - 19-08-2017',
        '2017-08-04': '50',
        '2017-08-11': '30',
        '2017-08-18': '10',
        cmmValue:50,
      },
      {
        drugCode: '05A01',
        area: 'VIA',
        subArea: 'Medicamentos Essenciais',
        drugName: 'drug name2',
        province: 'Maputo Prov\u00edncia',
        district: 'Marracuene',
        facility: 'Mumemo',
        reportGeneratedFor: '20-07-2017 - 19-08-2017',
        '2017-08-04': 'N/A',
        '2017-08-11': '150',
        '2017-08-18': '130'
      },
      {
        drugCode: '08K04',
        area: 'MMIA',
        subArea: 'MMIA',
        drugName: 'drug name1',
        province: 'Maputo Prov\u00edncia',
        district: 'Marracuene',
        facility: 'Mumemo2',
        reportGeneratedFor: '20-07-2017 - 19-08-2017',
        '2017-08-04': 'N/A',
        '2017-08-11': '300',
        '2017-08-18': '100',
        cmmValue:30
      },
      {
        drugCode: '05A01',
        area: 'VIA',
        subArea: 'Medicamentos Essenciais',
        drugName: 'drug name2',
        province: 'Maputo Prov\u00edncia',
        district: 'Marracuene',
        facility: 'Mumemo2',
        reportGeneratedFor: '20-07-2017 - 19-08-2017',
        '2017-08-04': 'N/A',
        '2017-08-11': '150',
        '2017-08-18': '30',
        cmmValue:0
      }
    ];

    var expectedExcel = {
      reportHeaders: expectedHeader,
      reportContent: expectedContent
    };

    expect(reportExportExcelService.exportAsXlsx).toHaveBeenCalledWith(expectedExcel, 'report.file.nos.drugs.report');

  });

});