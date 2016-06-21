describe("date format service test", function () {

    var dateFormatService, messageService;
    beforeEach(module('openlmis'));

    beforeEach(function () {
        inject(function (DateFormatService, _messageService_) {
            dateFormatService = DateFormatService;
            messageService = _messageService_;
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

    it("should format date with local", function () {
        var formattedDate = dateFormatService.formatDateWithLocale(new Date("2011-11-11"));
        expect(formattedDate).toEqual("11 " + messageService.get("month.abbr.11") + " 2011");
    });

    it("should format start date of period", function () {
        var formattedDate = dateFormatService.formatDateWithStartDayOfPeriod(new Date("2011-11-11"));
        expect(formattedDate).toEqual("2011-11-21");
    });

    it("should format end date of period", function () {
        var formattedDate = dateFormatService.formatDateWithEndDayOfPeriod(new Date("2011-11-11"));
        expect(formattedDate).toEqual("2011-11-20");
    });
});