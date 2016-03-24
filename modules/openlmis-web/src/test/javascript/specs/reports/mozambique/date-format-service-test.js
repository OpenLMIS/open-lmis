describe("date format service test", function () {

    var dateFormatService;
    beforeEach(module('openlmis'));

    beforeEach(function () {
        inject(function (DateFormatService) {
            dateFormatService = DateFormatService;
        })
    });

    it("should format first day of month", function () {
        var formattedDate = dateFormatService.formatDateWithFirstDayOfMonth(new Date("2011-11-11"));
        expect(formattedDate).toEqual("2011-11-01");
    });

    it("should format last day of month", function () {
        var formattedDate = dateFormatService.formatDateWithLastDayOfMonth(new Date("2011-11-11"));
        expect(formattedDate).toEqual("2011-11-30");
    });
});