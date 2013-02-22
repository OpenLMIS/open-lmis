describe('ViewRnrController', function () {
  var scope, httpBackend, controller, facilities, rnrList;

  beforeEach(module('openlmis.services'));
  beforeEach(inject(function ($httpBackend, $rootScope, $controller) {
    scope = $rootScope.$new();
    httpBackend = $httpBackend;
    controller = $controller;
    facilities = [
      {"id":1}
    ];
    rnrList = {'rnr_list':[]};
    controller(ViewRnrController, {$scope:scope, facilities:facilities});
  }));

  it('should initialize facilities', function () {
    expect(facilities).toEqual(scope.facilities);
  });

  it('should set facility label', function () {
    controller(ViewRnrController, {$scope:scope, facilities:[]});
    expect("--none assigned--").toEqual(scope.facilityLabel);
  });

  it('should set program label', function () {
    expect("--select program--").toEqual(scope.programLabel);
  });

  it('should load should raise error and return if if form invalid', function () {
    scope.viewRequisitionForm = {$invalid:true};
    scope.loadRequisitions();
    expect(scope.errorShown).toBeTruthy();
  });

  it('should get requisitions with program id if selected ', function () {
    scope.viewRequisitionForm = {$invalid:false};
    scope.selectedFacilityId = 1;
    scope.startDate = 'startDate';
    scope.endDate = 'endDate';
    scope.selectedProgramId = 1;
    var expectedUrl = '/requisitions-list.json?dateRangeEnd=endDate&dateRangeStart=startDate&facilityId=1&programId=1';
    loadRequisitions(expectedUrl, rnrList);
  });

  it('should get requisitions without program id if all', function () {
    scope.viewRequisitionForm = {$invalid:false};
    scope.selectedFacilityId = 1;
    scope.startDate = 'startDate';
    scope.endDate = 'endDate';
    var urlWithoutProgramId = '/requisitions-list.json?dateRangeEnd=endDate&dateRangeStart=startDate&facilityId=1';
    loadRequisitions(urlWithoutProgramId, rnrList);
  });


  it('should get requisitions with program id if selected, set requisitions and filteredRequisitions', function () {
    scope.viewRequisitionForm = {$invalid:false};
    scope.selectedFacilityId = 1;
    scope.startDate = 'startDate';
    scope.endDate = 'endDate';
    scope.selectedProgramId = 1;
    var expectedUrl = '/requisitions-list.json?dateRangeEnd=endDate&dateRangeStart=startDate&facilityId=1&programId=1';
    loadRequisitions(expectedUrl, rnrList);
    scope.requisitions = rnrList;
    scope.filteredRequisitions = rnrList;
    scope.requisitionFoundMessage = "No R&Rs found";
  });

  it('should get requisitions with program id if selected, set requisitions and filteredRequisitions', function () {
    scope.viewRequisitionForm = {$invalid:false};
    scope.selectedFacilityId = 1;
    scope.startDate = 'startDate';
    scope.endDate = 'endDate';
    scope.selectedProgramId = 1;
    var expectedUrl = '/requisitions-list.json?dateRangeEnd=endDate&dateRangeStart=startDate&facilityId=1&programId=1';
    var rnrList = {'rnr_list':[
      {'id':1}
    ]};

    loadRequisitions(expectedUrl, rnrList);

    scope.requisitions = rnrList;
    scope.filteredRequisitions = rnrList;
    scope.requisitionFoundMessage = "";
  });

  it('should filter requisitions against program name and return no results if not found', function () {
    scope.requisitions = [
      {'status':"abcd"}
    ];
    scope.query = "first";

    scope.filterRequisitions();

    expect(scope.filteredRequisitions.length).toEqual(0);
  });

  it('should filter requisitions against program name and return results', function () {
    scope.requisitions = [
      {'status':"first requisition"},
      {'status':"abcd"}
    ];
    scope.query = "first";

    scope.filterRequisitions();

    expect(scope.filteredRequisitions.length).toEqual(1);
    expect(scope.filteredRequisitions[0]).toEqual(scope.requisitions[0]);
  });

  it('should set end date offset based on start date', function () {
    scope.startDate = new Date();

    scope.setEndDateOffset();

    expect(scope.endDateOffset).toBeGreaterThan(0);
  });


  it('', function() {
    scope.startDate = new Date();

    scope.endDate = new Date(scope.startDate);
    scope.endDate.setDate(scope.startDate.getDate() - 1);

    scope.setEndDateOffset();

    expect(scope.endDateOffset).toBeGreaterThan(0);
    expect(scope.endDate).toBeUndefined();
  });

  it('should create grid with filtered requisitions', function () {
    expect('filteredRequisitions').toEqual(scope.rnrListGrid.data);
  });

  function loadRequisitions(expectedUrl, respondWith) {
    httpBackend.expect('GET', expectedUrl)
      .respond(200, respondWith);
    scope.loadRequisitions();
    httpBackend.flush();
  }
});