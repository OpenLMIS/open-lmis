describe('Date picker container controller test', function(){
    var scope,dateFormatService, filter;
    beforeEach(module('openlmis'));
    beforeEach(module('ui.bootstrap.dialog'));

    beforeEach(inject(function ($rootScope, $controller, $filter, DateFormatService) {
        scope = $rootScope;
        scope.getTimeRange = function (a) {
        };
        scope.loadReport = function (a) {
        };
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
        scope.dateRange.endTime = filter('date')(new Date("2016-03-12T00:00:00"), "yyyy-MM-dd");
        scope.checkCompletenessOfEndTime();

        expect(scope.showIncompleteWarning).toEqual(true);

        scope.dateRange.endTime = filter('date')(new Date("2016-03-31T00:00:00"), "yyyy-MM-dd");
        scope.checkCompletenessOfEndTime();

        expect(scope.showIncompleteWarning).toEqual(false);
    });

    it('should change period options correctly', function () {
        var currentDate = new Date();
        var defaultPeriodStartDate = currentDate.getDate() < 21 ?
            new Date(currentDate.getFullYear(), currentDate.getMonth() - 1, 21) :
            new Date(currentDate.getFullYear(), currentDate.getMonth(), 21);

        scope.changePeriodOption("3periods");
        var expectLastThreeMonth = new Date(new Date().setMonth(defaultPeriodStartDate.getMonth() - 2)).getMonth() + 1;
        expect(Number(scope.dateRange.startTime.substring(5,7))).toEqual(expectLastThreeMonth);

        scope.changePeriodOption("year");
        var expectLastYear = new Date(new Date().setMonth(defaultPeriodStartDate.getMonth() - 11)).getMonth() + 1;
        // expect(Number(scope.dateRange.startTime.substring(5,7))).toEqual(expectLastYear);
    });

    describe('when picker type is not period', function(){
        beforeEach(inject(function ($rootScope, $controller,  $filter, DateFormatService) {
            scope = $rootScope;
            dateFormatService = DateFormatService;
            filter = $filter;
            spyOn(dateFormatService, 'formatDateWithFirstDayOfMonth').andReturn('2016-06-01');
            dateFormatService.formatDateWithFirstDayOfMonth();
            $controller(DatePickerContainerController, {
                $scope: scope,
                $filter: filter,
                dateFormatService: dateFormatService
            });
        }));

        it('should show start date of a month', function(){

            expect(dateFormatService.formatDateWithFirstDayOfMonth).toHaveBeenCalled();
            expect(scope.dateRange.startTime).toEqual('2016-06-01');
        });
    });

    describe('when picker type is period', function(){
        beforeEach(inject(function ($rootScope, $controller,  $filter, DateFormatService) {
            scope = $rootScope;
            dateFormatService = DateFormatService;
            filter = $filter;
            scope.pickerType = "period";
            spyOn(dateFormatService, 'formatDateWithStartDayOfPeriod').andReturn('2016-05-21');
            spyOn(dateFormatService, 'formatDateWithEndDayOfPeriod').andReturn('2016-06-20');
            dateFormatService.formatDateWithStartDayOfPeriod();
            $controller(DatePickerContainerController, {
                $scope: scope,
                $filter: filter,
                dateFormatService: dateFormatService
            });
        }));

        it('should show period start date and period end date', function(){

            expect(dateFormatService.formatDateWithStartDayOfPeriod).toHaveBeenCalled();
            expect(scope.dateRange.startTime).toEqual('2016-05-21');
            expect(scope.dateRange.endTime).toEqual('2016-06-20');
        });
    });

});