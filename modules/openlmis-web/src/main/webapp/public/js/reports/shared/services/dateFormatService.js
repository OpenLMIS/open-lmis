services.factory('DateFormatService', function ($filter, messageService) {

    var formatDateWithFirstDayOfMonth = function (date) {
        return $filter('date')(new Date(date.getFullYear(), date.getMonth(), 1), "yyyy-MM-dd");
    };

    var formatDateWithLastDayOfMonth = function (date) {
        return $filter('date')(new Date(date.getFullYear(), date.getMonth() + 1, 0), "yyyy-MM-dd");
    };

    var formatDateWithStartDayOfPeriod = function (date) {
        if (date.getDate() < 21) {
            return $filter('date')(new Date(date.getFullYear(), date.getMonth() - 1, 21), "yyyy-MM-dd");
        } else {
            return $filter('date')(new Date(date.getFullYear(), date.getMonth(), 21), "yyyy-MM-dd");
        }
    };

    var formatDateWithEndDayOfPeriod = function (date) {
        if (date.getDate() > 20) {
            return $filter('date')(new Date(date.getFullYear(), date.getMonth() + 1, 20), "yyyy-MM-dd");
        } else {
            return $filter('date')(new Date(date.getFullYear(), date.getMonth(), 20), "yyyy-MM-dd");
        }
    };

    var formatDateWithLocale = function (dateString) {
        return formatDate(dateString, true);
    };

    var formatDateWithLocaleNoDay = function (dateString) {
        return formatDate(dateString, false);
    };

    function formatDate(dateString, withDay) {
        var date = new Date(dateString);
        var monthYear = messageService.get('month.abbr.' + (date.getMonth() + 1)) + " " + date.getFullYear();

        if (withDay) {
            return date.getDate() + " " + monthYear;
        } else {
            return monthYear;
        }
    }

    function formatDateWithTimeAndLocale(dateString) {
        var dateWithLocale = formatDateWithLocale(dateString);
        var date = new Date(dateString);
        var time = $filter('date')(date, "hh:mm a");
        return time + ' ' + dateWithLocale;
    }

    function formatDateWith24HoursTime(dateString) {
        var date = Date.parse(dateString);
        return $filter('date')(date, 'dd/MM/yyyy HH:mm');
    }


    function formatDateWithUnderscore(date) {
        return formatDateElementsTwoCharacters(date.getFullYear()) + "_" + formatDateElementsTwoCharacters((date.getMonth() + 1)) + "_" + formatDateElementsTwoCharacters(date.getDate()) + "_at_" + formatDateElementsTwoCharacters(date.getHours()) + "." + formatDateElementsTwoCharacters(date.getMinutes()) + "." + formatDateElementsTwoCharacters(date.getSeconds());
    }

    function formatDateElementsTwoCharacters(element) {
        if (element < 10) {
            return "0" + element;
        }
        return element;
    }

    return {
        formatDateWithFirstDayOfMonth: formatDateWithFirstDayOfMonth,
        formatDateWithLastDayOfMonth: formatDateWithLastDayOfMonth,
        formatDateWithLocale: formatDateWithLocale,
        formatDateWithLocaleNoDay: formatDateWithLocaleNoDay,
        formatDateWithStartDayOfPeriod: formatDateWithStartDayOfPeriod,
        formatDateWithEndDayOfPeriod: formatDateWithEndDayOfPeriod,
        formatDateWithTimeAndLocale: formatDateWithTimeAndLocale,
        formatDateWith24HoursTime: formatDateWith24HoursTime,
        formatDateWithUnderscore: formatDateWithUnderscore,
        formatDateElementsTwoCharacters: formatDateElementsTwoCharacters
    };
});