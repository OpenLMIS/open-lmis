describe('Date pciker container controller test', function(){
    var scope,dateFormatService, filter;
    beforeEach(module('openlmis'));
    beforeEach(module('ui.bootstrap.dialog'));

    beforeEach(inject(function ($rootScope, $controller,DateFormatService, $filter) {
        scope = $rootScope;
        dateFormatService = DateFormatService;
        filter = $filter;
        $controller(DatePickerContainerController, {$scope: scope});
    }));

    it('should change time options correctly', function () {
        scope.changeTimeOption("3month");
        var expectLastThreeMonth = new Date(new Date().setMonth(new Date().getMonth() - 2)).getMonth() + 1;
        expect(Number(scope.dateRange.startTime.substring(5,7))).toEqual(expectLastThreeMonth);

        scope.changeTimeOption("year");
        var expectLastYear = new Date(new Date().setMonth(new Date().getMonth() - 11)).getMonth() + 1;
        expect(Number(scope.dateRange.startTime.substring(5,7))).toEqual(expectLastYear);
    });

    it('should set showIncompleteWarning true when change endTime not the last day in current month', function () {
        scope.dateRange.endTime = filter('date')(new Date("2016-03-12"), "yyyy-MM-dd");
        scope.checkCompletenessOfEndTime();

        expect(scope.showIncompleteWarning).toEqual(true);

        scope.dateRange.endTime = filter('date')(new Date("2016-03-31"), "yyyy-MM-dd");
        scope.checkCompletenessOfEndTime();

        expect(scope.showIncompleteWarning).toEqual(false);
    });
});