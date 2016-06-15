describe("consumption report controller", function () {
    var scope;

    beforeEach(module('openlmis'));
    beforeEach(module('ui.bootstrap.dialog'));

    beforeEach(inject(function ($controller, $rootScope) {
        scope = $rootScope.$new();
        $controller(ConsumptionReportController, {$scope: scope});
    }));

    it('should split for each period in selected range', function () {
        var periods = scope.splitPeriods("2016-01-01", "2016-04-21");

        expect(periods.length).toBe(5);
        expect(periods).toEqual([
            {periodStart: new Date("2015-12-21"), periodEnd: new Date("2016-01-20")},
            {periodStart: new Date("2016-01-21"), periodEnd: new Date("2016-02-20")},
            {periodStart: new Date("2016-02-21"), periodEnd: new Date("2016-03-20")},
            {periodStart: new Date("2016-03-21"), periodEnd: new Date("2016-04-20")},
            {periodStart: new Date("2016-04-21"), periodEnd: new Date("2016-05-20")}
        ]);
    });

    it('should only split one period when selected range in one period', function () {
        var periods = scope.splitPeriods("2016-01-01", "2016-01-19");

        expect(periods.length).toBe(1);
        expect(periods).toEqual([
            {periodStart: new Date("2015-12-21"), periodEnd: new Date("2016-01-20")}
        ]);
    });
});