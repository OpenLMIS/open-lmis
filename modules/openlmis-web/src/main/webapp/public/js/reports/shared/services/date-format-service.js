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

  function formatDateWithUTC(dateString, withDay) {
    var date = new Date(dateString);
    var monthYear = messageService.get('month.abbr.' + (date.getUTCMonth() + 1)) + " " + date.getUTCFullYear();

    if (withDay) {
      return date.getUTCDate() + " " + monthYear;
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
  
  function formatDateWithDateMonthYear(timestamp) {
    return moment(timestamp).format('DD-MM-YYYY');
  }
  
  function formatDateWithDateMonthYearForString(stringDate) {
    return moment(stringDate, 'YYYY-MM-DD').format('DD-MM-YYYY');
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
  
  function getFridaysBetween(start, end) {
    var dates = [];
    for (var day = new Date(start); day <= end; day.setDate(day.getDate() + 1)) {
      var isFriday = day.getDay() == 5;
      if (isFriday) {
        dates.push(new Date(day));
      }
    }
    return dates;
  }
  
  
  function convertPortugueseDateStringToNormalDateString(inputValue) {
    var portugueseDateMap = {
      Jan: 'Jan',
      Fev: 'Feb',
      Mar: 'Mar',
      Abr: 'Apr',
      Mai: 'May',
      Jun: 'June',
      Jul: 'July',
      Ago: 'Aug',
      Set: 'Sep',
      Out: 'Oct',
      Nov: 'Nov',
      Dez: 'Dec'
    };
    
    var matchArray = inputValue.match(/[a-zA-Z]{3}/g);
    if (!matchArray) {
      return inputValue;
    }
    
    var matchValue = matchArray[0];
    if (_.isUndefined(portugueseDateMap[matchValue])) {
      return inputValue;
    }
  
    return inputValue.replace(matchValue, portugueseDateMap[matchValue]);
  }
  
  return {
    formatDateWithFirstDayOfMonth: formatDateWithFirstDayOfMonth,
    formatDateWithLastDayOfMonth: formatDateWithLastDayOfMonth,
    formatDateWithLocale: formatDateWithLocale,
    formatDateWithUTC: formatDateWithUTC,
    formatDateWithLocaleNoDay: formatDateWithLocaleNoDay,
    formatDateWithStartDayOfPeriod: formatDateWithStartDayOfPeriod,
    formatDateWithEndDayOfPeriod: formatDateWithEndDayOfPeriod,
    formatDateWithTimeAndLocale: formatDateWithTimeAndLocale,
    formatDateWith24HoursTime: formatDateWith24HoursTime,
    formatDateWithDateMonthYear: formatDateWithDateMonthYear,
    formatDateWithDateMonthYearForString: formatDateWithDateMonthYearForString,
    formatDateWithUnderscore: formatDateWithUnderscore,
    formatDateElementsTwoCharacters: formatDateElementsTwoCharacters,
    getFridaysBetween: getFridaysBetween,
    convertPortugueseDateStringToNormalDateString: convertPortugueseDateStringToNormalDateString
  };
});