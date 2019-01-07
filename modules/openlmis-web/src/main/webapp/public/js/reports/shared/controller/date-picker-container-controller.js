function DatePickerContainerController($scope, $filter, DateFormatService, messageService) {
    var currentDate = new Date();
    var todayDateString = $filter('date')(new Date(), "yyyy-MM-dd");

    $scope.dateRange = {};

    $scope.timeTagSelected = "month";
    $scope.periodTagSelected = $scope.pickerType === "period-no-current" ? "3periods" : "period";

    var thisPeriodStartDate = currentDate.getDate() < 21 ?
        new Date(currentDate.getFullYear(), currentDate.getMonth() - 1, 21) :
        new Date(currentDate.getFullYear(), currentDate.getMonth(), 21);

    var lastPeriodStartDate = currentDate.getDate() === 20 ?
      thisPeriodStartDate : new Date(thisPeriodStartDate.getFullYear(), thisPeriodStartDate.getMonth() - 1, 21);

    var threeMonthsAgo = new Date().setMonth(new Date().getMonth() - 2);
    var aYearAgo = new Date().setMonth(new Date().getMonth() - 11);

    var threePeriodsBeforeExcludingCurrent = new Date(lastPeriodStartDate.getFullYear(), lastPeriodStartDate.getMonth() - 2, 21);
    if (lastPeriodStartDate.getMonth() - 2 > currentDate.getMonth()) {
        threePeriodsBeforeExcludingCurrent = new Date(threePeriodsBeforeExcludingCurrent).setYear(currentDate.getFullYear() - 1);
    }

    var twelvePeriodsBeforeExcludingCurrent = new Date(lastPeriodStartDate.getFullYear(), lastPeriodStartDate.getMonth() - 11, 21);
    if (lastPeriodStartDate.getMonth() - 11 > currentDate.getMonth()) {
        twelvePeriodsBeforeExcludingCurrent = new Date(twelvePeriodsBeforeExcludingCurrent).setYear(lastPeriodStartDate.getFullYear() - 2);
    } else {
        twelvePeriodsBeforeExcludingCurrent = new Date(twelvePeriodsBeforeExcludingCurrent).setYear(lastPeriodStartDate.getFullYear() - 1);
    }

    var threePeriodsBeforeIncludingCurrent = new Date(thisPeriodStartDate.getFullYear(), thisPeriodStartDate.getMonth() - 2, 21);
    if (thisPeriodStartDate.getMonth() - 2 > currentDate.getMonth()) {
        threePeriodsBeforeIncludingCurrent = new Date(threePeriodsBeforeIncludingCurrent).setYear(currentDate.getFullYear() - 1);
    }

    var twelvePeriodsBeforeIncludingCurrent = new Date(thisPeriodStartDate.getFullYear(), thisPeriodStartDate.getMonth() - 11, 21);
    if (thisPeriodStartDate.getMonth() - 11 > currentDate.getMonth()) {
        twelvePeriodsBeforeIncludingCurrent = new Date(twelvePeriodsBeforeIncludingCurrent).setYear(currentDate.getFullYear() - 2);
    } else {
        twelvePeriodsBeforeIncludingCurrent = new Date(twelvePeriodsBeforeIncludingCurrent).setYear(currentDate.getFullYear() - 1);
    }

    if ($scope.pickerType === "period") {
        $scope.dateRange.startTime = DateFormatService.formatDateWithStartDayOfPeriod(thisPeriodStartDate);
        $scope.dateRange.endTime = DateFormatService.formatDateWithEndDayOfPeriod(new Date());
    } else if ($scope.pickerType === "period-no-current") {
        $scope.dateRange.startTime = DateFormatService.formatDateWithStartDayOfPeriod(new Date(threePeriodsBeforeExcludingCurrent));
        $scope.dateRange.endTime = DateFormatService.formatDateWithEndDayOfPeriod(lastPeriodStartDate);
    } else {
        $scope.dateRange.startTime = DateFormatService.formatDateWithFirstDayOfMonth(new Date());
        $scope.dateRange.endTime = todayDateString;
    }

    var defaultEndTime = new Date($scope.dateRange.endTime);

    var timeOptions = {
        "month": new Date(),
        "3month": threeMonthsAgo,
        "year": aYearAgo
    };

    var periodOptions = $scope.pickerType === "period-no-current" ?
    {
        "3periods": threePeriodsBeforeExcludingCurrent,
        "year": twelvePeriodsBeforeExcludingCurrent
    } :
    {
        "period": thisPeriodStartDate,
        "3periods": threePeriodsBeforeIncludingCurrent,
        "year": twelvePeriodsBeforeIncludingCurrent
    };

    $scope.timeTags = Object.keys(timeOptions);
    $scope.periodTags = Object.keys(periodOptions);
    $scope.showDateRangeInvalidWarning = false;

    function baseTimePickerOptions() {
        return {
            prevText: "<<",
            nextText: ">>",
            monthNames: [ "Janeiro","Fevereiro","Março","Abril","Maio","Junho",
                "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro" ],
            monthNamesShort: [ "Jan","Fev","Mar","Abr","Mai","Jun",
                "Jul","Ago","Set","Out","Nov","Dez" ],
            dayNames: [
                "Domingo",
                "Segunda-feira",
                "Terça-feira",
                "Quarta-feira",
                "Quinta-feira",
                "Sexta-feira",
                "Sábado"
            ],
            dayNamesShort: [ "Dom","Seg","Ter","Qua","Qui","Sex","Sáb" ],
            dayNamesMin: [ "Dom","Seg","Ter","Qua","Qui","Sex","Sáb" ],
            weekHeader: "Sem",
            dateFormat: 'dd/mm/yy',
            changeYear: true,
            changeMonth: true,
            showMonthAfterYear: true,
            beforeShow: function () {
                $("#ui-datepicker-div").addClass("hide-calendar");
                $("#ui-datepicker-div").addClass('MonthDatePicker');
                $("#ui-datepicker-div").addClass('HideTodayButton');
            }
        };
    }

    var monthNamesForStartPeriod = [];
    var monthNamesForEndPeriod = [];
    var monthNumbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];

    monthNumbers.forEach(function (month) {
        var monthNamesString = messageService.get("month.abbr." + month);

        monthNamesForStartPeriod.push("21 " + monthNamesString);
        monthNamesForEndPeriod.push("20 " + monthNamesString);
    });


    $scope.periodStartOptions = angular.extend(baseTimePickerOptions(), {
        maxDate: thisPeriodStartDate,
        showMonthAfterYear: false,
        monthNamesShort: monthNamesForStartPeriod,
        onClose: function () {
          $scope.timeTagSelected = "";
            var selectedYear = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
            var selectedMonth = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
            $scope.$apply(function () {
                $scope.dateRange.startTime = selectedMonth === null ?
                    DateFormatService.formatDateWithStartDayOfPeriod(currentDate) :
                    DateFormatService.formatDateWithStartDayOfPeriod(new Date(selectedYear, selectedMonth, 21));
            });
        }
    });

    $scope.periodNoCurrentStartOptions = angular.extend(baseTimePickerOptions(), {
        maxDate: new Date(defaultEndTime.getFullYear(),defaultEndTime.getMonth()-1,21),
        showMonthAfterYear: false,
        monthNamesShort: monthNamesForStartPeriod,
        onClose: function () {
          $scope.timeTagSelected = "";
            var selectedYear = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
            var selectedMonth = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
            $scope.$apply(function () {
                $scope.dateRange.startTime = selectedMonth === null ?
                    DateFormatService.formatDateWithStartDayOfPeriod(currentDate) :
                    DateFormatService.formatDateWithStartDayOfPeriod(new Date(selectedYear, selectedMonth, 21));
            });
        }
    });

    $scope.periodEndOptions = angular.extend(baseTimePickerOptions(), {
        showMonthAfterYear: false,
        monthNamesShort: monthNamesForEndPeriod,
        onClose: function () {
          $scope.timeTagSelected = "";
            var selectedYear = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
            var selectedMonth = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
            $scope.$apply(function () {
                $scope.dateRange.endTime = selectedMonth === null ?
                    DateFormatService.formatDateWithEndDayOfPeriod(defaultStartTime.getFullYear, defaultStartTime.getMonth() + 1) :
                    DateFormatService.formatDateWithEndDayOfPeriod(new Date(selectedYear, selectedMonth));
            });
        }
    });

    $scope.periodNoCurrentEndOptions = angular.extend($scope.periodEndOptions, {
        maxDate: defaultEndTime
    });

    $scope.datePickerStartOptions = angular.extend(baseTimePickerOptions(), {
        maxDate: currentDate,
        onClose: function () {
          $scope.timeTagSelected = "";
            var selectedYear = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
            var selectedMonth = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
            $scope.$apply(function () {
                $scope.dateRange.startTime = selectedMonth === null ? DateFormatService.formatDateWithFirstDayOfMonth(currentDate) : $filter('date')(new Date(selectedYear, selectedMonth, 01), "yyyy-MM-dd");
            });
        }
    });

    $scope.datePickerEndOptions = angular.extend(baseTimePickerOptions(), {
        maxDate: DateFormatService.formatDateWithLastDayOfMonth(currentDate),
        onClose: function () {
          $scope.timeTagSelected = "";
            var selectedYear = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
            var selectedMonth = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
            $scope.$apply(function () {
                $scope.dateRange.endTime = selectedMonth === null ? todayDateString : DateFormatService.formatDateWithLastDayOfMonth(new Date(selectedYear, selectedMonth));
            });
        }
    });

    $scope.changeTimeOption = function (timeTag) {
        $scope.timeTagSelected = timeTag;
        $scope.dateRange.startTime = DateFormatService.formatDateWithFirstDayOfMonth(new Date(timeOptions[timeTag]));
        $scope.dateRange.endTime = todayDateString;
      $scope.getTimeRange({
        'dateRange': $scope.dateRange
      });
      $scope.loadReport();
    };

    $scope.changePeriodOption = function (periodTag) {
        $scope.periodTagSelected = periodTag;
        if ($scope.pickerType === "period-no-current") {
            $scope.dateRange.startTime = DateFormatService.formatDateWithStartDayOfPeriod(new Date(periodOptions[periodTag]));
            $scope.dateRange.endTime = DateFormatService.formatDateWithEndDayOfPeriod(defaultEndTime);
        } else {
          $scope.dateRange.startTime = DateFormatService.formatDateWithStartDayOfPeriod(new Date(periodOptions[periodTag]));
          $scope.dateRange.endTime = DateFormatService.formatDateWithEndDayOfPeriod(defaultEndTime);
        }
      $scope.getTimeRange({
        'dateRange': $scope.dateRange
      });
      $scope.loadReport();
    };

    $scope.$on("update-date-pickers", function (event, range) {
        $scope.dateRange = range;
    });

    $scope.$watch('dateRange.startTime', function () {
        $scope.getTimeRange({
            'dateRange': $scope.dateRange
        }, true);
    });

    $scope.$watch('dateRange.endTime', function () {
        $scope.checkCompletenessOfEndTime();

        $scope.getTimeRange({
            'dateRange': $scope.dateRange
        }, true);
    });

    $scope.checkCompletenessOfEndTime = function () {
      $scope.showIncompleteWarning = $scope.pickerType === "period-no-current" ? false : $scope.dateRange.endTime != DateFormatService.formatDateWithLastDayOfMonth(new Date($scope.dateRange.endTime));
    };
}