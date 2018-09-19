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
    var formattedDate = dateFormatService.formatDateWithLocale(new Date("2011-11-11T00:00:00"));
    expect(formattedDate).toEqual("11 " + messageService.get("month.abbr.11") + " 2011");
  });

  it("should format start date of period", function () {
    var formattedDate = dateFormatService.formatDateWithStartDayOfPeriod(new Date("2011-11-11"));
    expect(formattedDate).toEqual("2011-10-21");
  });

  it("should format end date of period", function () {
    var formattedDate = dateFormatService.formatDateWithEndDayOfPeriod(new Date("2011-11-11"));
    expect(formattedDate).toEqual("2011-11-20");
  });

  it('should format date with full time', function () {
    var formattedDate = dateFormatService.formatDateWithDateMonthYear(1502945999999);
    expect(formattedDate).toEqual('17-08-2017');
  });

  xit('should format date with dd-MM-yyyy', function () {
    var dateString = '2016-10-25T19:54:00.998865';
    var formattedDate = dateFormatService.formatDateWith24HoursTime(dateString);
    expect(formattedDate).toEqual('25/10/2016 19:54');
  });

  it('should format date with dd-MM-yyyy from string date', function () {
    var dateString = '2016-10-25T00:00:00.000000';
    var formattedDate = dateFormatService.formatDateWithDateMonthYearForString(dateString);
    expect(formattedDate).toEqual('25-10-2016');
  });

  it("should format splited '_' ", function () {
    var initDate = new Date("2011-11-11T00:00:00");
    initDate.setTime(initDate.getTime() + initDate.getTimezoneOffset()*60*1000);

    var formattedDate = dateFormatService.formatDateWithUnderscore(initDate);
    expect(formattedDate).toEqual("2011_11_11_at_00.00.00");
  });

  it("should add a character 0 when the value is less than 10", function () {
    var value = Math.floor((Math.random() * 9));
    var expectedValue = '0' + value;
    var valueStringFormatted = dateFormatService.formatDateElementsTwoCharacters(value);
    expect(expectedValue).toEqual(valueStringFormatted);
  });

  it("should not add a character 0 when the value is greater than 9", function () {
    var value = Math.floor((Math.random() * 31) + 10);
    var expectedValue = value;
    var valueStringFormatted = dateFormatService.formatDateElementsTwoCharacters(value);
    expect(expectedValue).toEqual(valueStringFormatted);
  });
});