var utils = {
  getFormattedDate:function (date) {
    return ('0' + date.getDate()).slice(-2) + '/'
      + ('0' + (date.getMonth() + 1)).slice(-2) + '/'
      + date.getFullYear();
  },

  isNumber:function (number) {
    return !isNaN(utils.parseIntWithBaseTen(number));
  },

  parseIntWithBaseTen:function (number) {
    return parseInt(number, 10);
  },

  getValueFor:function (number, defaultValue) {
    if (!utils.isNumber(number)) return defaultValue ? defaultValue : null;
    return utils.parseIntWithBaseTen(number);
  },

  isNullOrUndefined:function (obj) {
    return obj == undefined || obj == null;
  },

  isPositiveNumber:function (value) {
    var INTEGER_REGEXP = /^\d*$/;
    return INTEGER_REGEXP.test(value);
  },

  isValidPage:function (pageNumber, totalPages) {
    pageNumber = parseInt(pageNumber, 10);
    return !!pageNumber && pageNumber > 0 && pageNumber <= totalPages;
  }

};
