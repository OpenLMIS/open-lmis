describe("adjustment occurrences report controller", function () {
  var scope;
  var period;

  beforeEach(module('openlmis'));
  beforeEach(module('ui.bootstrap.dialog'));

  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    $controller(AdjustmentOccurrencesReportController, {$scope: scope});
    period = {
      periodStart: new Date("2016-05-21T00:00:00.000Z"),
      periodEnd: new Date("2016-06-20T00:00:00.000Z")
    };
  }));

  it('should generate cut params with negative reason codes when adjustment type is negative', function () {
    var adjustmentType = "negative";
    var cutParams = scope.generateCutParamsByAdjustmentType(period, adjustmentType);

    expect(cutParams).toContain({
      "dimension": "reason_code",
      "values": [
        "EXPIRED_RETURN_TO_SUPPLIER",
        "DAMAGED",
        "LOANS_DEPOSIT",
        "INVENTORY_NEGATIVE",
        "PROD_DEFECTIVE",
        "RETURN_TO_DDM"
      ]
    });
  });

  it('should generate cut params with positive reason codes when adjustment type is positive', function () {
    var adjustmentType = "positive";
    var cutParams = scope.generateCutParamsByAdjustmentType(period, adjustmentType);

    expect(cutParams).toContain({
      "dimension": "reason_code",
      "values": [
        "CUSTOMER_RETURN",
        "EXPIRED_RETURN_FROM_CUSTOMER",
        "DONATION",
        "LOANS_RECEIVED",
        "INVENTORY_POSITIVE",
        "RETURN_FROM_QUARANTINE"
      ]
    });
  });

});