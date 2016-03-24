services.factory('DateFormatService', function ($filter) {

    var formatDateWithFirstDayOfMonth = function (date) {
        return $filter('date')(new Date(date.getFullYear(), date.getMonth(), 1), "yyyy-MM-dd");
    };

    var formatDateWithLastDayOfMonth = function (date) {
        return $filter('date')(new Date(date.getFullYear(), date.getMonth() + 1, 0), "yyyy-MM-dd");
    };

    return {
        formatDateWithFirstDayOfMonth: formatDateWithFirstDayOfMonth,
        formatDateWithLastDayOfMonth: formatDateWithLastDayOfMonth
    };
});