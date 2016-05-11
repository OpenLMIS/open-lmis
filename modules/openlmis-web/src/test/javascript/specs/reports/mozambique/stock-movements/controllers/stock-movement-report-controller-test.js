describe("stock movement report controller", function () {
    var scope, httpBackend, routeParams;

    var facility = {
        "facility": {"id":2,
            "code":"HF1",
            "name":"Marracuene",
            "geographicZone":{
                "id":5,
                "code":"MARRACUENE",
                "name":"Marracuene",
                "level":{
                    "id":3,
                    "code":"district",
                    "name":"District",
                    "levelNumber":3},
                "parent":{
                    "code":"MAPUTO_PROVINCIA",
                    "name":"Maputo Província",
                    "level":{
                        "code":"province",
                        "name":"Province"}
                }
            }
        }
    };

    var stockMovements =[
        {
            "movement.reason": "INVENTORY",
            "product.product_name": "Levonorgestrel (Microlut) 30mcg Ciclo",
            "movement.signature": null,
            "facility": "Marracuene",
            "movement.quantity": 110,
            "movement.type": "RECEIVE",
            "movement.soh": "150",
            "movement.latest_soh": 212,
            "movement.date": "2016-02-29T20:45:11+08:00",
            "movement.documentnumber": null,
            "product.product_code": "04F06Y",
            "vw_stock_movements_reason": "INVENTORY",
            "movement.expirationdates": "31/1/2018"
        },
        {
            "movement.reason": "INVENTORY_POSITIVE",
            "product.product_name": "Levonorgestrel (Microlut) 30mcg Ciclo",
            "movement.signature": "nelso",
            "facility": "Marracuene",
            "movement.quantity": 10,
            "movement.type": "ISSUE",
            "movement.soh": "480",
            "movement.latest_soh": 212,
            "movement.date": "2016-03-02T17:28:33+08:00",
            "movement.documentnumber": "",
            "product.product_code": "04F06Y",
            "vw_stock_movements_reason": "INVENTORY_POSITIVE",
            "movement.expirationdates": "31/1/2018"
        }
    ];

    beforeEach(module('openlmis'));
    beforeEach(inject(function (_$httpBackend_, $rootScope, $controller) {
        scope = $rootScope.$new();
        httpBackend = _$httpBackend_;
        routeParams = {'facilityId': 199, 'productCode': 'productCode'};

        $controller(StockMovementReportController, {$scope: scope, $routeParams: routeParams});
    }));

    it('should load facility and stock movements successfully', function () {
        httpBackend.expectGET('/facilities/199.json').respond(200, facility);
        httpBackend.expectGET('/cubesreports/cube/vw_stock_movements/facts?cut=facility:Marracuene|product:productCode').respond(200, stockMovements);

        scope.loadFacilityAndStockMovements();
        httpBackend.flush();

        expect(scope.facilityName).toBe("Marracuene");
        expect(scope.district).toBe("Marracuene");
        expect(scope.province).toBe("Maputo Província");

        expect(scope.stockMovements.length).toBe(2);
        expect(scope.stockMovements[0].entries).toBe(110);
        expect(scope.stockMovements[1].issues).toBe(10);

    });
});