describe('Date pciker container controller test', function(){
    var scope;
    beforeEach(module('openlmis'));
    beforeEach(module('ui.bootstrap.dialog'));
    beforeEach(inject(function ($rootScope, $controller) {
        scope = $rootScope;
        $controller(DatePickerContainerController, {$scope: scope});
    }));

    it('should change time options correctly', function () {
        scope.$on('$viewContentLoaded');
        scope.changeTimeOption("3month");
        var expectLastThreeMonth = new Date(new Date().setMonth(new Date().getMonth() - 2)).getMonth() + 1;
        expect(Number(scope.dateRange.startTime.substring(5,7))).toEqual(expectLastThreeMonth);

        scope.changeTimeOption("year");
        var expectLastYear = new Date(new Date().setMonth(new Date().getMonth() - 11)).getMonth() + 1;
        expect(Number(scope.dateRange.startTime.substring(5,7))).toEqual(expectLastYear);
    });
});