/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function AdjustmentSummaryReportController($scope, $filter, ngTableParams, AdjustmentSummaryReport, ReportProductsByProgram, ReportPrograms, ProductCategoriesByProgram, RequisitionGroupsByProgram, ReportFacilityTypes, GeographicZones, AdjustmentTypes, OperationYears, Months, $http, $routeParams, $location) {


  // default to the monthly period type
  $scope.periodType = 'monthly';
  $scope.requisitionGroups = [];

  $scope.periodTypes = [
    {'name': 'Monthly', 'value': 'monthly'},
    {'name': 'Quarterly', 'value': 'quarterly'},
    {'name': 'Semi Anual', 'value': 'semi-anual'},
    {'name': 'Annual', 'value': 'annual'}
  ];
  $scope.startYears = [];
  OperationYears.get(function (data) {
    $scope.startYears = $scope.endYears = data.years;
    adjustEndYears();
  });

  Months.get(function (data) {
    var months = data.months;

    if (months !== null) {
      $scope.startMonths = [];
      $scope.endMonths = [];
      $.each(months, function (idx, obj) {
        $scope.startMonths.push({'name': obj.toString(), 'value': idx + 1});
        $scope.endMonths.push({'name': obj.toString(), 'value': idx + 1});
      });
    }

  });

  $scope.startQuarters = function () {
    return $scope.quarters;
  };

  $scope.endQuarters = function () {
    if ($scope.startYear === $scope.endYear && $scope.startQuarter !== '') {
      var arr = [];
      for (var i = $scope.startQuarter - 1; i < $scope.quarters.length; i++) {
        arr.push($scope.quarters[i]);
      }
      return arr;
    }
    return $scope.quarters;
  };

  $scope.quarters = [
    {'name': 'One', 'value': '1'},
    {'name': 'Two', 'value': '2'},
    {'name': 'Three', 'value': '3'},
    {'name': 'Four', 'value': '4'}
  ];

  $scope.semiAnnuals = [
    {'name': 'First Half', 'value': '1'},
    {'name': 'Second Half', 'value': '2'}
  ];

  $scope.product;

  // copy over the start month and end months
  // this is just for initial loading.
  $(function () {
    $scope.startQuarters = $scope.quarters;
    $scope.endQuarters = $scope.quarters;
    $scope.endYears = $scope.startYears;
    $scope.startSemiAnnuals = $scope.semiAnnuals;
    $scope.endSemiAnnuals = $scope.semiAnnuals;
    $scope.toQuarter = 1;
    $scope.fromQuarter = 1;
    $scope.startHalf = 1;
    $scope.endHalf = 1;
  });


  $scope.isMonthly = function () {
    return $scope.periodType == 'monthly';
  };

  $scope.isQuarterly = function () {
    return $scope.periodType == 'quarterly';
  };

  $scope.isSemiAnnualy = function () {
    return $scope.periodType == 'semi-anual';
  };

  $scope.filterGrid = function () {
    $scope.getPagedDataAsync(0, 0);
  };

  //filter form data section
  $scope.filterObject = {
    facilityTypeId: $scope.facilityType,
    zoneId: $scope.zone,
    periodType: $scope.periodType,
    fromYear: $scope.fromYear,
    fromMonth: $scope.fromMonth,
    fromQuarter: $scope.fromQuarter,
    fromSemiAnnual: $scope.startHalf,
    toYear: $scope.toYear,
    toMonth: $scope.toMonth,
    toQuarter: $scope.toQuarter,
    toSemiAnnual: $scope.endHalf,
    productId: $scope.product,
    productCategoryId: $scope.productCategory,
    rgroupId: $scope.rgroup,
    programId: $scope.program,
    facility: $scope.facilityId,
    facilityType: "",
    rgroup: "",
    pdformat: 0,
    adjustmentTypeId: $scope.adjustmentType,
    adjustmentType: ""
  };

  ReportFacilityTypes.get(function (data) {
    $scope.facilityTypes = data.facilityTypes;
    $scope.facilityTypes.unshift({'name': '-- All Facility Types --'});
  });

  AdjustmentTypes.get(function (data) {
    $scope.adjustmentTypes = data.adjustmentTypeList;
    $scope.adjustmentTypes.unshift({'description': '--All Adjustment Types --'});
  });

  GeographicZones.get(function (data) {
    $scope.zones = data.zones;
    $scope.zones.unshift({'name': '-- All Geographic Zones --'});
  });

  ReportPrograms.get(function (data) {
    $scope.programs = data.programs;
    $scope.programs.unshift({'name': '-- Select Programs --'});
  });

  $scope.$watch('zone.value', function (selection) {
    if (selection !== undefined || selection === "") {
      $scope.filterObject.zoneId = selection;

    } else {
      $scope.filterObject.zoneId = 0;
    }
    $scope.filterGrid();
  });

  $scope.$watch('status.value', function (selection) {
    if (selection !== undefined || selection === "") {
      $scope.filterObject.statusId = selection;
    } else {
      $scope.filterObject.statusId = '';
    }
    $scope.filterGrid();
  });
  $scope.$watch('facilityType.value', function (selection) {
    if (selection !== undefined || selection === "") {
      $scope.filterObject.facilityTypeId = selection;
      $.each($scope.facilityTypes, function (item, idx) {
        if (idx.id == selection) {
          $scope.filterObject.facilityType = idx.name;
        }
      });

    } else {
      $scope.filterObject.facilityTypeId = 0;
      $scope.filterObject.facilityType = "";
    }
    $scope.filterGrid();
  });

  $scope.$watch('startYear', function (selection) {
    var date = new Date();
    if (selection !== undefined || selection === "") {
      $scope.filterObject.fromYear = selection;
      adjustEndYears();
      adjustEndMonths();
      adjustEndQuarters();
      adjustEndSemiAnnuals();
    } else {
      $scope.startYear = date.getFullYear().toString();
      $scope.filterObject.fromYear = date.getFullYear();
    }
    $scope.filterGrid();
  });

  $scope.$watch('endYear', function (selection) {
    var date = new Date();
    if (selection !== undefined || selection === '') {
      $scope.filterObject.toYear = selection;
      adjustEndMonths();
      adjustEndQuarters();
      adjustEndSemiAnnuals();
    } else {
      $scope.endYear = date.getFullYear().toString();
      $scope.filterObject.toYear = date.getFullYear();
    }
    $scope.filterGrid();
  });

  $scope.$watch('startQuarter', function (selection) {
    var date = new Date();
    if (selection !== undefined || selection === "") {
      $scope.filterObject.fromQuarter = selection;
      adjustEndQuarters();
    } else {
      $scope.filterObject.fromQuarter = 1;
    }
    $scope.filterGrid();
  });

  $scope.$watch('endQuarter', function (selection) {
    var date = new Date();
    if (selection !== undefined || selection === "") {
      $scope.filterObject.toQuarter = selection;
    } else {
      $scope.filterObject.toQuarter = $scope.filterObject.fromQuarter;
    }
    $scope.filterGrid();
  });

  $scope.$watch('startHalf', function (selection) {

    if (selection !== undefined || selection === "") {
      $scope.filterObject.fromSemiAnnual = selection;
      adjustEndSemiAnnuals();
    } else {
      $scope.filterObject.fromSemiAnnual = 1;
    }
    $scope.filterGrid();
  });
  $scope.$watch('endHalf', function (selection) {

    if (selection !== undefined || selection === "") {
      $scope.filterObject.toSemiAnnual = selection;
    } else {
      var date = new Date();
      $scope.filterObject.toSemiAnnual = 1;
    }
    $scope.filterGrid();
  });


  $scope.$watch('startMonth', function (selection) {
    $scope.filterObject.fromMonth = $scope.startMonth;
    if ($scope.startMonth !== undefined || $scope.startMonth === "") {
      adjustEndMonths();
    } else {
      var date = new Date();
      $scope.endMonth = $scope.startMonth = (date.getMonth() + 1 ).toString();
    }

    $scope.filterGrid();
  });

  $scope.$watch('endMonth', function (selection) {
    $scope.filterObject.toMonth = $scope.endMonth;
    $scope.filterGrid();
  });

  var adjustEndMonths = function () {
    if ($scope.startMonth !== undefined && $scope.startMonths !== undefined && $scope.startYear === $scope.endYear) {
      $scope.endMonths = [];
      $.each($scope.startMonths, function (idx, obj) {
        if (obj.value >= $scope.startMonth) {
          $scope.endMonths.push({'name': obj.name, 'value': obj.value});
        }
      });
    }
  };

  var adjustEndQuarters = function () {
    if ($scope.startYear == $scope.endYear) {
      $scope.endQuarters = [];
      $.each($scope.startQuarters, function (idx, obj) {
        if (obj.value >= $scope.startQuarter) {
          $scope.endQuarters.push({'name': obj.name, 'value': obj.value});
        }
      });
      if ($scope.endQuarter < $scope.startQuarter) {
        $scope.endQuarter = $scope.startQuarter;
      }
    } else {
      $scope.endQuarters = $scope.startQuarters;
    }
  };

  var adjustEndSemiAnnuals = function () {

    if ($scope.startYear == $scope.endYear) {
      $scope.endSemiAnnuals = [];
      $.each($scope.startSemiAnnuals, function (idx, obj) {
        if (obj.value >= $scope.startHalf) {
          $scope.endSemiAnnuals.push({'name': obj.name, 'value': obj.value});
        }
      });
      if ($scope.endHalf < $scope.startHalf) {
        $scope.endHalf = $scope.startHalf;
      }
    } else {
      $scope.endSemiAnnuals = $scope.startSemiAnnuals;
    }
  };

  var adjustEndYears = function () {
    $scope.endYears = [];
    $.each($scope.startYears, function (idx, obj) {
      if (obj >= $scope.startYear) {
        $scope.endYears.push(obj);
      }
    });
    if ($scope.endYear <= $scope.startYear) {
      $scope.endYear = new Date().getFullYear();
    }
  };


  $scope.$watch('periodType', function (selection) {
    if (selection !== undefined || selection === "") {
      $scope.filterObject.periodType = selection;

    } else {
      $scope.filterObject.periodType = "monthly";
    }
    $scope.filterGrid();
  });

  $scope.$watch('productCategory', function (selection) {
    if (selection !== undefined || selection === "") {
      $scope.filterObject.productCategoryId = selection;
    } else {
      $scope.filterObject.productCategoryId = 0;
    }
    $scope.filterGrid();
  });

  $scope.$watch('product', function (selection) {
    if (selection == "All") {
      $scope.filterObject.productId = -1;
    } else if (selection !== undefined || selection === "") {
      $scope.filterObject.productId = selection;
    } else {
      $scope.filterObject.productId = 0;
    }
    $scope.filterGrid();
  });


  $scope.$watch('rgroup', function (selection) {
    if (selection !== undefined || selection === "") {
      $scope.filterObject.rgroupId = selection;
      $.each($scope.requisitionGroups, function (item, idx) {
        if (idx.id == selection) {
          $scope.filterObject.rgroup = idx.name;
        }
      });
    } else {
      $scope.filterObject.rgroupId = 0;
      $scope.filterObject.rgroup = "";
    }
    $scope.filterGrid();
  });

  $scope.$watch('program', function (selection) {
    if (selection !== undefined || selection === "") {
      $scope.filterObject.programId = selection;

      if (selection === '') {
        return;
      }

      // load the program-product categories
      ProductCategoriesByProgram.get({programId: selection}, function (data) {
        $scope.productCategories = data.productCategoryList;
        $scope.productCategories.unshift({'name': '-- All Product Categories --'});
      });

      ReportProductsByProgram.get({programId: selection}, function (data) {
        $scope.products = data.productList;
        if ($scope.products.length === 0) {
          $scope.products.push({'name': '-- All Products --'});
        } else {
          $scope.products.unshift({'name': '-- All Products --'});
        }
      });

      RequisitionGroupsByProgram.get({program: selection }, function (data) {
        $scope.requisitionGroups = data.requisitionGroupList;
        if ($scope.requisitionGroups === undefined || $scope.requisitionGroups.length === 0) {
          $scope.requisitionGroups = [];
          $scope.requisitionGroups.push({'name': '-- All Requisition Groups --'});
        } else {
          $scope.requisitionGroups.unshift({'name': '-- All Requisition Groups --'});
        }

      });
    } else {
      //$scope.filterObject.programId =  0;
      return;
    }
    $scope.filterGrid();
  });

  $scope.$watch('adjustmentType.value', function (selection) {
    if (selection == "All") {
      $scope.filterObject.adjustmentTypeId = -1;
      $scope.filterObject.adjustmentType = "All";

    } else if (selection !== undefined || selection === "") {
      $scope.filterObject.adjustmentTypeId = selection;
      $.each($scope.adjustmentTypes, function (item, idx) {
        if (idx.name == selection) {
          $scope.filterObject.adjustmentType = idx.description;
        }
      });

    } else {
      $scope.filterObject.adjustmentTypeId = "";
      $scope.filterObject.adjustmentType = "";
    }
    $scope.filterGrid();
  });


  $scope.exportReport = function (type) {

    $scope.filterObject.pdformat = 1;
    var params = jQuery.param($scope.filterObject);
    var url = '/reports/download/adjustment_summary/' + type + '?' + params;
    window.location.href = url;
  };

  // the grid options
  $scope.tableParams = new ngTableParams({
    page: 1,            // show first page
    total: 0,           // length of data
    count: 25           // count per page
  });

  $scope.paramsChanged = function (params) {

    // slice array data on pages
    if ($scope.data === undefined) {
      $scope.datarows = [];
      params.total = 0;
    } else {
      var data = $scope.data;
      var orderedData = params.filter ? $filter('filter')(data, params.filter) : data;
      orderedData = params.sorting ? $filter('orderBy')(orderedData, params.orderBy()) : data;

      params.total = orderedData.length;
      $scope.datarows = orderedData.slice((params.page - 1) * params.count, params.page * params.count);
      var i = 0;
      var baseIndex = params.count * (params.page - 1) + 1;
      while (i < $scope.datarows.length) {
        $scope.datarows[i].no = baseIndex + i;
        i++;
      }
    }
  };

  // watch for changes of parameters
  $scope.$watch('tableParams', $scope.paramsChanged, true);

  $scope.getPagedDataAsync = function (pageSize, page) {
    var params = {
      "max": 10000,
      "page": 1
    };
    if ($scope.program === null || $scope.program === undefined || $scope.program === '') {
      // do not send a request to the server before the basic selection was done.
      return;
    }

    $.each($scope.filterObject, function (index, value) {
      if (value !== undefined)
        params[index] = value;
    });

    // clear existing data
    $scope.data = [];

    // try to load the new data based on the selected parameters
    AdjustmentSummaryReport.get(params, function (data) {
      if (data.pages !== undefined) {
        $scope.data = data.pages.rows;
        $scope.paramsChanged($scope.tableParams);
      }
    });

  };


}
