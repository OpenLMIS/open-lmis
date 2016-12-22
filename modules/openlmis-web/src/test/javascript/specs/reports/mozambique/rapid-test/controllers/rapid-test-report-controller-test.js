describe("Rapid Test Report Controller", function () {
  var scope, httpBackend, messageService;

  beforeEach(module('openlmis'));
  beforeEach(module('ui.bootstrap.dialog'));
  beforeEach(inject(function (_$httpBackend_, $rootScope, $http, $controller, $filter, _messageService_) {
    scope = $rootScope.$new();
    httpBackend = _$httpBackend_;
    messageService = _messageService_;

    $controller(RapidTestReportController, {$scope: scope});
  }));

  it('should calculate total value', function () {
    scope.rapidTestReportData = [
      {
        CONSUME_HIVDETERMINE: 10,
        POSITIVE_HIVDETERMINE: "",
        CONSUME_HIVUNIGOLD: 30,
        POSITIVE_HIVUNIGOLD: 0,
        CONSUME_SYPHILLIS: 0,
        POSITIVE_SYPHILLIS: 0,
        CONSUME_MALARIA: 0,
        POSITIVE_MALARIA: 0
      },
      {
        CONSUME_HIVDETERMINE: 10,
        POSITIVE_HIVDETERMINE: "",
        CONSUME_HIVUNIGOLD: 0,
        POSITIVE_HIVUNIGOLD: 0,
        CONSUME_SYPHILLIS: 0,
        POSITIVE_SYPHILLIS: 0,
        CONSUME_MALARIA: 0,
        POSITIVE_MALARIA: 0
      },
      {
        CONSUME_HIVDETERMINE: 10,
        POSITIVE_HIVDETERMINE: "",
        CONSUME_HIVUNIGOLD: 0,
        POSITIVE_HIVUNIGOLD: 0,
        CONSUME_SYPHILLIS: 0,
        POSITIVE_SYPHILLIS: 0,
        CONSUME_MALARIA: 0,
        POSITIVE_MALARIA: 100
      }
    ];

    scope.calculateTotalValues();
    expect(scope.totalValues).toEqual({
      formatted_name: messageService.get('report.header.total'),
      CONSUME_HIVDETERMINE: 30,
      POSITIVE_HIVDETERMINE: 0,
      CONSUME_HIVUNIGOLD: 30,
      POSITIVE_HIVUNIGOLD: 0,
      CONSUME_SYPHILLIS: 0,
      POSITIVE_SYPHILLIS: 0,
      CONSUME_MALARIA: 0,
      POSITIVE_MALARIA: 100
    });
  });
});