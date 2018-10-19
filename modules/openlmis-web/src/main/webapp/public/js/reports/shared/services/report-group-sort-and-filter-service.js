services.factory('ReportGroupSortAndFilterService', function ($http, $filter, DateFormatService) {

  var search = function (overStockList, filterText, specialType, timeType) {
    return _.filter(overStockList, function (item) {
      return checkField(item, filterText, specialType, timeType);
    });

  };

  function checkField(item, filterText, specialType, timeType) {
    var flag = false;
    _.forEach(item, function (value, key) {
      if (key !== specialType && checkValueContains(value, filterText)) {
        flag = true;
      }
      if (key === specialType && checkLotItemInclude(value, timeType, filterText)) {
        flag = true;
      }
    });
    return flag;
  }

  function checkLotItemInclude(field, timeField, filterText) {
    return _.find(field, function (lotItem) {
      var flag = false;
      _.forEach(lotItem, function (value, key) {
        if (key === timeField) {
          value = DateFormatService.formatDateWithLocale(value);
        }
        if (checkValueContains(value, filterText)) {
          flag = true;
        }
      });
      return flag;
    });
  }

  function checkValueContains(value, filterText) {
    return (value + "").toLowerCase().indexOf(filterText.toLowerCase()) > -1;
  }

  var groupSort = function(filterList, sortType, sortReverse, sortList) {
    if (sortType) {
      return _.includes(sortList, sortType) ?
        getSortByNestedObject(filterList, sortType, sortReverse) :
        $filter('orderBy')(filterList, sortType, sortReverse);
    }
    return filterList;
  };

  function getSortByNestedObject(filterList, sortType, sortReverse) {
    filterList = _.sortBy(sortLotItem(filterList, sortType, sortReverse), function (o) {
      return o.lotList[0][sortType];
    });
    return sortReverse ? filterList.reverse() :
      filterList;
  }

  function sortLotItem(data, sortType, sortReverse) {
    return _.map(data, function (item) {
      item.lotList = _.sortBy(item.lotList, function (n) {
        return n[sortType];
      });
      sortReverse ? item.lotList.reverse() : item.lotList;
      return item;
    });
  }

  return {
    search: search,
    groupSort: groupSort,
  };
});