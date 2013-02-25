describe('ConvertToOrderListController', function () {

  var scope, ctrl, httpBackend, controller;
  var requisitionList;

  beforeEach(module('openlmis.services'));

  beforeEach(inject(function ($httpBackend, $rootScope, $controller) {
    scope = $rootScope.$new();
    controller = $controller;
    httpBackend = $httpBackend;

    requisitionList = [
      {"facilityName":"first facility", "programName":"first program", "facilityCode":"first code", supplyingDepot:"supplying depot first "},
      {"facilityName":"second facility", "programName":"second program", "facilityCode":"second code", supplyingDepot:"supplying depot second"}
    ];
    ctrl = controller(ConvertToOrderListController, {$scope:scope, requisitionList:requisitionList});
  }));

  it('should show all requisitions if filter is not applied', function () {
    expect(scope.filteredRequisitions).toEqual(requisitionList);
    expect(scope.query).toBeUndefined();
    expect(scope.searchField).toBeUndefined();
  });


  it('should Filter requisitions against program name', function () {
    scope.query = "first";
    scope.searchField = "programName";

    scope.filterRequisitions();

    expect(scope.filteredRequisitions.length).toEqual(1);
    expect(scope.filteredRequisitions[0]).toEqual(requisitionList[0]);
  });

  it('should Filter requisitions against facility name', function () {
    scope.query = "second facility";
    scope.searchField = "facilityName";
    scope.filterRequisitions();

    expect(scope.filteredRequisitions.length).toEqual(1);
    expect(scope.filteredRequisitions[0]).toEqual(requisitionList[1]);
  });

  it('should Filter requisitions against facility code', function () {
    scope.query = "second CO";
    scope.searchField = "facilityCode";
    scope.filterRequisitions();

    expect(scope.filteredRequisitions.length).toEqual(1);
    expect(scope.filteredRequisitions[0]).toEqual(requisitionList[1]);
  });

  it('should Filter requisitions against supplying depot', function () {
    scope.query = "depot first";
    scope.searchField = "supplyingDepot";
    scope.filterRequisitions();

    expect(scope.filteredRequisitions.length).toEqual(1);
    expect(scope.filteredRequisitions[0]).toEqual(requisitionList[0]);
  });

  it('should be able to Filter requisitions against all fields also', function () {
    scope.query = "second";
    scope.searchField = "";

    scope.filterRequisitions();

    expect(scope.filteredRequisitions.length).toEqual(1);
    expect(scope.filteredRequisitions[0]).toEqual(requisitionList[1]);
  });

  it('should be able to case-insensitively Filter requisitions', function () {
    scope.query = "seCOnD";
    scope.searchField = "";

    scope.filterRequisitions();

    expect(scope.filteredRequisitions.length).toEqual(1);
    expect(scope.filteredRequisitions[0]).toEqual(requisitionList[1]);
  });

  it("should convert the selected requisitions to order", function () {
    scope.gridOptions.selectedItems = [requisitionList[0]];
    httpBackend.expectPOST('/requisitionOrder.json', {"rnrList":scope.gridOptions.selectedItems}).respond(200);
    httpBackend.expectGET('/requisitions-for-convert-to-order.json').respond({"rnr_list":[requisitionList[1]]});

    scope.convertToOrder();

    httpBackend.flush();
    expect(scope.message).toEqual("The requisition(s) have been successfully converted to Orders");
    expect(scope.error).toEqual("");
    expect(scope.requisitions).toEqual([requisitionList[1]]);
  });

  it('should give message if no requisition selected', function() {
    scope.gridOptions.selectedItems = [];

    scope.convertToOrder();

    expect(scope.message).toEqual("Please select atleast one Requisition for Converting to Order.");
  });
});

