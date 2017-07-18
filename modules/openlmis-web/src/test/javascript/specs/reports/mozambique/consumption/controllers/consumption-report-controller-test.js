describe("consumption report controller", function () {
    var scope;

    beforeEach(module('openlmis'));
    beforeEach(module('ui.bootstrap.dialog'));

    beforeEach(inject(function ($controller, $rootScope) {
        scope = $rootScope.$new();
        $controller(ConsumptionReportController, {$scope: scope});
    }));

    it('should split for each period in selected range', function () {
        var periods = scope.splitPeriods("2016-01-01T00:00:00", "2016-04-21T00:00:00");

        expect(periods.length).toBe(5);
        expect(periods).toEqual([
            {periodStart: new Date("2015-12-21T00:00:00"), periodEnd: new Date("2016-01-20T00:00:00")},
            {periodStart: new Date("2016-01-21T00:00:00"), periodEnd: new Date("2016-02-20T00:00:00")},
            {periodStart: new Date("2016-02-21T00:00:00"), periodEnd: new Date("2016-03-20T00:00:00")},
            {periodStart: new Date("2016-03-21T00:00:00"), periodEnd: new Date("2016-04-20T00:00:00")},
            {periodStart: new Date("2016-04-21T00:00:00"), periodEnd: new Date("2016-05-20T00:00:00")}
        ]);
    });

    it('should only split one period when selected range in one period', function () {
        var periods = scope.splitPeriods("2016-01-01T00:00:00", "2016-01-19T00:00:00");

        expect(periods.length).toBe(1);
        expect(periods).toEqual([
            {periodStart: new Date("2015-12-21T00:00:00"), periodEnd: new Date("2016-01-20T00:00:00")}
        ]);
    });
});