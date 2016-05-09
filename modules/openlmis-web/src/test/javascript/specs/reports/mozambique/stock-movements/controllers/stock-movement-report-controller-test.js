describe("stock movement report controller", function () {
    var scope, httpBackend, routeParams;

    var stockMovements ={
        "stockMovement":[
            {
                "id": 17650,
                "type": "ADJUSTMENT",
                "quantity": 0,
                "referenceNumber": null,
                "adjustmentReason": {
                    "id": null,
                    "name": "INVENTORY",
                    "description": "Inventory",
                    "additive": true
                },
                "lotOnHand": null,
                "notes": null,
                "occurred": 1461513600000,
                "extensions": [
                    {
                        "key": "expirationdates",
                        "value": "8/8/2016",
                        "syncedDate": 1461756504099
                    },
                    {
                        "key": "signature",
                        "value": "nhome",
                        "syncedDate": 1461756504099
                    },
                    {
                        "key": "soh",
                        "value": "150",
                        "syncedDate": 1461756504099
                    }
                ]
            }
            , {
                "id": 5390,
                "type": "ADJUSTMENT",
                "quantity": 0,
                "referenceNumber": null,
                "adjustmentReason": {
                    "id": null,
                    "name": "INVENTORY",
                    "description": "Inventory",
                    "additive": true
                },
                "lotOnHand": null,
                "notes": null,
                "occurred": 1451318400000,
                "extensions": [
                    {
                        "key": "expirationdates",
                        "value": "8/8/2016",
                        "syncedDate": 1451382261370
                    },
                    {
                        "key": "signature",
                        "value": "",
                        "syncedDate": 1452757229588
                    },
                    {
                        "key": "soh",
                        "value": "160",
                        "syncedDate": 1453109125420
                    }
                ]
            }
        ]
    };

    beforeEach(module('openlmis'));
    beforeEach(inject(function (_$httpBackend_, $rootScope, $controller) {
        scope = $rootScope.$new();
        httpBackend = _$httpBackend_;
        routeParams = {'facilityId': 199, 'productCode': 'productCode'};

        $controller(StockMovementReportController, {$scope: scope, $routeParams: routeParams});
    }));

    it('should load stock movements successfully', function () {
        httpBackend.expectGET('/reports/stockMovements?facilityId=199&productCode=productCode').respond(200, stockMovements);

        scope.loadStockMovements();
        httpBackend.flush();

        expect(scope.stockMovements.length).toBe(2);
    });
});