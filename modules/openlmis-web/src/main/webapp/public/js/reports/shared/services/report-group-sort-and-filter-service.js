services.factory('ReportGroupSortAndFilterService', function ($http, $filter, DateFormatService) {

  var search = function (overStockList, filterText, specialType, timeFieldList, ignoreSearchList) {
    return _.filter(overStockList, function (item) {
      return checkField(item, filterText, specialType, timeFieldList, ignoreSearchList);
    });

  };

  function checkField(item, filterText, specialType, timeFieldList, ignoreSearchList) {
    var flag = false;
    _.forEach(item, function (value, key) {
      if (!_.includes(ignoreSearchList, key)) {
        if (_.includes(timeFieldList, key)) {
          value = DateFormatService.formatDateWithLocale(value);
        }
        if (key !== specialType && checkValueContains(value, filterText)) {
          flag = true;
        }
        if (key === specialType && checkField(value, filterText, specialType, timeFieldList, ignoreSearchList)) {
          flag = true;
        }
      }
    });
    return flag;
  }

  function checkValueContains(value, filterText) {
    return (value + "").toLowerCase().indexOf(filterText.toLowerCase()) > -1;
  }

  var groupSort = function (filterList, sortType, sortReverse, sortList) {
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
    groupSort: groupSort
  };
});