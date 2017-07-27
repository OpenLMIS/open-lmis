describe('Single Product Report Controller', function () {
    var scope, httpBackend, dateFilter, window, productData, vw_daily_full_soh, lotExpiryDateService, reportExportExcelService, messageService_;

    productData = [
        {
            "drug.drug_code": "01A01",
            "location.province_code": "MAPUTO_PROVINCIA",
            "cmm": -1.0,
            "occurred.month": 12.0,
            "drug.drug_name": "Paracetamol500mgComprimidos",
            "location.province_name": "Maputo Provincia",
            "facility.facility_name": "Marracuene",
            "occurred.day": 8.0,
            "location.district_code": "MARRACUENE",
            "facility.facility_code": "HF1",
            "last_sync_date": "2016-06-21T08:55:53.908508+08:00",
            "expiry_date": "2017-04-08",
            "soh": "1000",
            "occurred_date": "2015-12-08",
            "vw_daily_full_soh_facility_name": "Marracuene",
            "location.district_name": "Marracuene",
            "occurred.year": 2015.0
        },
        {
            "drug.drug_code": "01A01",
            "location.province_code": "MAPUTO_PROVINCIA",
            "cmm": -1.0,
            "occurred.month": 12.0,
            "drug.drug_name": "Paracetamol500mgComprimidos",
            "location.province_name": "Maputo Prov\u00edncia",
            "facility.facility_name": "Marracuene",
            "occurred.day": 17.0,
            "location.district_code": "MARRACUENE",
            "facility.facility_code": "HF1",
            "last_sync_date": "2016-06-21T08:55:53.908508+08:00",
            "expiry_date": "2017-04-08",
            "soh": "1000",
            "occurred_date": "2015-12-17",
            "vw_daily_full_soh_facility_name": "Marracuene",
            "location.district_name": "Marracuene",
            "occurred.year": 2015.0
        }];

    beforeEach(module('openlmis'));
    beforeEach(module('ui.bootstrap.dialog'));
    beforeEach(inject(function (_$httpBackend_, $rootScope, $filter, $controller, LotExpiryDateService, $window, ReportExportExcelService, messageService) {
        scope = $rootScope.$new();
        httpBackend = _$httpBackend_;
        dateFilter = $filter('date');
        window = $window;
        lotExpiryDateService = LotExpiryDateService;
        reportExportExcelService = ReportExportExcelService;
        messageService_ = messageService;
        $controller(SingleProductReportController, {$scope: scope});
    }));

    it('should redirect to lot expiry date report', function () {
        var facilityCode = 'HF1';
        scope.reportParams = {
            endTime: '2016-11-01',
            productCode: '01A03'
        };

        expect(scope.generateRedirectToExpiryDateReportURL(facilityCode)).toBe('/public/pages/reports/mozambique/index.html#/lot-expiry-dates?facilityCode=HF1&date=2016-11-01&drugCode=01A03');
    });

    it('should load single product report successfully', function () {
        scope.reportParams = {};
        scope.loadReport();
        expect(scope.invalid).toBe(true);
        scope.reportParams.productCode = '01A01';
        scope.reportParams.endTime = '2017-01-04';
        scope.reportParams.facilityId = 1;
        scope.reportParams.districtId = 1;
        scope.reportParams.provinceId = 1;

        spyOn(lotExpiryDateService, 'populateLotOnHandInformationForSoonestExpiryDate');

        httpBackend.expectGET('/cubesreports/cube/vw_daily_full_soh/facts?cut=occurred%3A-2017%2C01%2C04%7Cdrug%3A01A01').respond(200, productData);
        httpBackend.expectGET('/cubesreports/cube/vw_cmm_entries/facts?cut=product%3A01A01%7Cperiodbegin%3A2016%2C12%2C21%7Cperiodend%3A2017%2C01%2C20').respond(200, productData);

        scope.loadReport();
        httpBackend.flush();

        expect(scope.reportData.length).toBe(1);
        expect(scope.reportData[0]['drug.drug_name']).toEqual("Paracetamol500mgComprimidos");
    });

    it('should export data with district province name successfully', function () {
        scope.reportData = [
            {
                'cmm': 1,
                'drug.drug_code': '01A01',
                'drug.drug_name': 'Digoxina 0,25mg Comp',
                'estimated_months': 3,
                'expiry_date': 'Apr 2019',
                'facility.facility_code': 'F_CORE',
                'facility.facility_id': 2,
                'facility.facility_name': 'Nhongonhane (Ed.Mondl.)',
                'formatted_expiry_date': 'Apr 2019',
                'formatted_last_sync_date': '12:24 AM 18 July 2017',
                'last_sync_date': '2017-07-18T07:24:44.516902+02:00',
                'location.province_code': 'MAPUTO_PROVINCIA',
                'location.province_name': 'Maputo Prov\u00edncia',
                'location.district_code': 'MARRACUENE',
                'location.district_name': 'Marracuene',
                'occurred.month': 7.0,
                'occurred.day': 26.0,
                'occurred_date': '2017-07-26',
                'occurred.year': 2017.0,
                'soonest_expiring_loh': 40,
                'stock_card_entry_id': 11,
                'stock_status': '',
                'soh': 46,
                'vw_daily_full_soh_stock_card_entry_id': 11
            }
        ];

        spyOn(reportExportExcelService, 'exportAsXlsx');
        scope.exportXLSX();

        var expectedHeader = {
            drugCode: 'report.header.drug.code',
            drugName: 'report.header.drug.name',
            province: 'report.header.province',
            district: 'report.header.district',
            facility: 'report.header.facility',
            quantity: 'report.header.drug.quantity',
            status: 'report.header.status',
            earliestDrugExpiryDate: 'report.header.earliest.drug.expiry.date',
            lotStockOnHand: 'report.header.lot.stock.on.hand',
            estimatedMonths: 'report.header.estimated.months',
            lastUpdateFromTablet: 'report.header.last.update.from.tablet'
        };

        var date = Date.parse("2017-07-18T07:24:44.516902+02:00");
        var expectedSyncDate = dateFilter(date, 'dd/MM/yyyy HH:mm');

        var expectedContent = {
            drugCode: '01A01',
            drugName: 'Digoxina 0,25mg Comp',
            province: 'Maputo Prov\u00edncia',
            district: 'Marracuene',
            facility: 'Nhongonhane (Ed.Mondl.)',
            quantity: 46,
            status: '',
            earliestDrugExpiryDate: 'Apr 2019',
            lotStockOnHand: 40,
            estimatedMonths: 3,
            lastUpdateFromTablet: expectedSyncDate
        };

        var expectedExcel = {
            reportHeaders: expectedHeader,
            reportContent: [expectedContent]
        };

        expect(reportExportExcelService.exportAsXlsx).toHaveBeenCalledWith(expectedExcel, 'report.file.single.product.soh.report');
    });
});