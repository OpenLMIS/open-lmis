/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
function AverageConsumptionReportController($scope, $filter, ReportProductsByProgram, ngTableParams, FacilityByFacilityType, FacilityByProgramByFacilityType, AverageConsumptionReport, RequisitionGroupsByProgram, ReportPrograms, ProductCategoriesByProgram, RequisitionGroups, ReportFacilityTypes, GeographicZones, OperationYears, Months) {

  // product filter customizations
  $scope.wideOption = {
    'multiple': true,
    dropdownCss: {
      'min-width': '500px'
    }
  };

  $scope.productFilter = function(option) {
    return (option.categoryId == $scope.productCategory || option.id == '0');
  };
  // end of product filter customizations

  // initialize the selections that will be loaded later
  $scope.products = [];
  $scope.products.unshift({
    name: '-- All Products --',
    id: '0'
  });

  $scope.requisitionGroups = [];
  $scope.requisitionGroups.push({
    name: '-- All Requisition Groups --'
  });
  $scope.facillities = [];
  $scope.facillities.push({
    name: '-- All Facilities --'
  });
  $scope.productCategories = [];
  $scope.productCategories.push({
    name: '-- All Product Categories --'
  });

  $scope.startYears = [];
  OperationYears.get(function(data) {
    $scope.startYears = $scope.endYears = data.years;
    adjustEndYears();
  });

  // default to the monthly period type
  $scope.periodType = 'monthly';

  $scope.periodTypes = [{
    'name': 'Monthly',
    'value': 'monthly'
  }, {
    'name': 'Quarterly',
    'value': 'quarterly'
  }, {
    'name': 'Semi Anual',
    'value': 'semi-anual'
  }, {
    'name': 'Annual',
    'value': 'annual'
  }];

  $scope.startQuarters = function() {
    return $scope.quarters;
  };

  // initialize default quarters
  $scope.fromQuarter = $scope.toQuarter = 1;

  $scope.endQuarters = function() {
    if ($scope.startYear == $scope.endYear && $scope.startQuarter !== '') {
      var arr = [];
      for (var i = $scope.startQuarter - 1; i < $scope.quarters.length; i++) {
        arr.push($scope.quarters[i]);
      }
      return arr;
    }
    return $scope.quarters;
  };

  Months.get(function(data) {
    var months = data.months;

    if (months !== null) {
      $scope.startMonths = [];
      $scope.endMonths = [];
      $.each(months, function(idx, obj) {
        $scope.startMonths.push({
          'name': obj.toString(),
          'value': idx + 1
        });
        $scope.endMonths.push({
          'name': obj.toString(),
          'value': idx + 1
        });
      });
    }
  });

  $scope.quarters = [{
    'name': 'One',
    'value': '1'
  }, {
    'name': 'Two',
    'value': '2'
  }, {
    'name': 'Three',
    'value': '3'
  }, {
    'name': 'Four',
    'value': '4'
  }];
  $scope.semiAnnuals = [{
    'name': 'First Half',
    'value': '1'
  }, {
    'name': 'Second Half',
    'value': '2'
  }];

  $scope.product;


  // copy over the start month and end months
  // this is just for initial loading.
  $(function() {
    $scope.endYears = $scope.startYears;
    $scope.startQuarters = $scope.quarters;
    $scope.endQuarters = $scope.quarters;
    $scope.startSemiAnnuals = $scope.semiAnnuals;
    $scope.endSemiAnnuals = $scope.semiAnnuals;
  });


  $scope.isMonthly = function() {
    return $scope.periodType == 'monthly';
  };

  $scope.isQuarterly = function() {
    return $scope.periodType == 'quarterly';
  };

  $scope.isSemiAnnualy = function() {
    return $scope.periodType == 'semi-anual';
  };


  $scope.filterGrid = function() {
    if ($scope.filterForm.$invalid) {
      $scope.errorShown = true;
      //return;
    }
    $scope.getPagedDataAsync(0, 0);

  };

  //filter form data section
  $scope.filterOptions = {
    filterText: "",
    useExternalFilter: false
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
    pdformat: 0
  };

  ReportFacilityTypes.get(function(data) {
    $scope.facilityTypes = data.facilityTypes;
    $scope.facilityTypes.unshift({
      'name': '-- All Facility Types --'
    });
  });


  GeographicZones.get(function(data) {
    $scope.zones = data.zones;
    $scope.zones.unshift({
      'name': '-- All Geographic Zones --'
    });

  });

  ReportPrograms.get(function(data) {
    $scope.programs = data.programs;
    $scope.programs.unshift({
      'name': '-- Select Program --'
    });
  });

  $scope.$watch('zone.value', function(selection) {
    if (selection !== undefined || selection === "") {
      $scope.filterObject.zoneId = selection;
      //$scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
    } else {
      $scope.filterObject.zoneId = 0;
    }
    $scope.filterGrid();
  });

  $scope.$watch('status.value', function(selection) {
    if (selection !== undefined || selection === "") {
      $scope.filterObject.statusId = selection;
      //$scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
    } else {
      $scope.filterObject.statusId = '';
    }
    $scope.filterGrid();
  });

  $scope.$watch('facilityType.value', function(selection) {
    if (selection !== undefined || selection === "") {
      $scope.filterObject.facilityTypeId = selection;
      //            GetFacilityByFacilityType

      //            FacilityByFacilityType.get({facilityTypeId: selection}, function (data) {
      //                $scope.facillities = data.facilities;
      //                $scope.facillities.unshift({name: '-- All Facilities --'});
      //            });

      $scope.getFacilitiesList();

      $.each($scope.facilityTypes, function(item, idx) {
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
  $scope.$watch('facilityId.value', function(selection) {
    if (selection !== undefined || selection === "") {

      $scope.filterObject.facility = selection;
      //            GetFacilityByFacilityType


    } else {
      $scope.filterObject.facility = 0;

    }
    $scope.filterGrid();
  });
  $scope.$watch('startYear', function(selection) {
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

  $scope.$watch('endYear', function(selection) {
    var date = new Date();
    if (selection !== undefined || selection === "") {
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

  $scope.$watch('startQuarter', function(selection) {
    if (selection !== undefined || selection === "") {
      $scope.filterObject.fromQuarter = selection;
      adjustEndQuarters();
    } else {
      var date = new Date();
      $scope.filterObject.fromQuarter = 1;
    }
    $scope.filterGrid();
  });

  $scope.$watch('endQuarter', function(selection) {
    if (selection !== undefined || selection === "") {
      $scope.filterObject.toQuarter = selection;
    } else {
      var date = new Date();
      $scope.filterObject.toQuarter = $scope.filterObject.fromQuarter;
    }
    $scope.filterGrid();
  });

  $scope.$watch('startHalf', function(selection) {

    if (selection !== undefined || selection === "") {
      $scope.filterObject.fromSemiAnnual = selection;
      adjustEndSemiAnnuals();
    } else {
      $scope.filterObject.fromSemiAnnual = 1;
    }
    $scope.filterGrid();
  });
  $scope.$watch('endHalf', function(selection) {

    if (selection !== undefined || selection === "") {
      $scope.filterObject.toSemiAnnual = selection;
    } else {
      var date = new Date();
      $scope.filterObject.toSemiAnnual = 1;
    }
    $scope.filterGrid();
  });

  $scope.$watch('startMonth', function(selection) {
    if ($scope.startMonth !== undefined || $scope.startMonth === "") {
      adjustEndMonths();
    } else {
      var date = new Date();
      $scope.endMonth = $scope.startMonth = (date.getMonth() + 1).toString();
    }
    $scope.filterObject.fromMonth = $scope.startMonth;
    $scope.filterGrid();
  });

  $scope.$watch('endMonth', function(selection) {
    $scope.filterObject.toMonth = $scope.endMonth;
    $scope.filterGrid();
  });

  var adjustEndMonths = function() {
    if ($scope.startMonth !== undefined && $scope.startMonths !== undefined && $scope.startYear == $scope.endYear) {
      $scope.endMonths = [];
      $.each($scope.startMonths, function(idx, obj) {
        if (obj.value >= $scope.startMonth) {
          $scope.endMonths.push({
            'name': obj.name,
            'value': obj.value
          });
        }
      });
    }
  };

  var adjustEndSemiAnnuals = function() {

    if ($scope.startYear == $scope.endYear) {
      $scope.endSemiAnnuals = [];
      $.each($scope.startSemiAnnuals, function(idx, obj) {
        if (obj.value >= $scope.startHalf) {
          $scope.endSemiAnnuals.push({
            'name': obj.name,
            'value': obj.value
          });
        }
      });
      if ($scope.endHalf < $scope.startHalf) {
        $scope.endHalf = $scope.startHalf;
      }
    } else {
      $scope.endSemiAnnuals = $scope.startSemiAnnuals;
    }
  };

  var adjustEndQuarters = function() {
    if ($scope.startQuarter !== undefined && $scope.startYear == $scope.endYear) {
      $scope.endQuarters = [];
      $.each($scope.startQuarters, function(idx, obj) {
        if (obj.value >= $scope.startQuarter) {
          $scope.endQuarters.push({
            'name': obj.name,
            'value': obj.value
          });
        }
      });
      if ($scope.endQuarter < $scope.startQuarter) {
        $scope.endQuarter = $scope.startQuarter;
      }
    } else {
      $scope.endQuarters = $scope.startQuarters;
    }
  };

  var adjustEndYears = function() {
    $scope.endYears = [];
    $.each($scope.startYears, function(idx, obj) {
      if (obj >= $scope.startYear) {
        $scope.endYears.push(obj);
      }
    });
    if ($scope.endYear < $scope.startYear) {
      $scope.endYear = new Date().getFullYear();
    }
  };

  $scope.$watch('periodType', function(selection) {
    if (selection !== undefined || selection === "") {
      $scope.filterObject.periodType = selection;

    } else {
      $scope.filterObject.periodType = "monthly";
    }
    $scope.filterGrid();

  });

  $scope.$watch('productCategory', function(selection) {
    if (selection !== undefined || selection === "") {
      $scope.filterObject.productCategoryId = selection;
    } else {
      $scope.filterObject.productCategoryId = 0;
    }
    $scope.filterGrid();
  });

  $scope.$watch('product', function(selection) {
    console.warn($scope.product);
    if (selection !== undefined || selection === "") {
      $scope.filterObject.productId = JSON.stringify($scope.product);
    } else {
      $scope.filterObject.productId = 0;
    }
    $scope.filterGrid();
  });


  $scope.$watch('rgroup', function(selection) {
    if (selection !== undefined || selection === "") {
      $scope.filterObject.rgroupId = selection;
      $.each($scope.requisitionGroups, function(item, idx) {
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

  $scope.$watch('program', function(selection) {
    if (selection !== undefined || selection === "") {
      $scope.filterObject.programId = selection;
      // load the program-product categories
      ProductCategoriesByProgram.get({
        programId: selection
      }, function(data) {
        $scope.productCategories = data.productCategoryList;
        $scope.productCategories.unshift({
          'name': '-- All Product Categories --'
        });
      });

      ReportProductsByProgram.get({
        programId: selection
      }, function(data) {
        $scope.products = data.productList;
        $scope.products.unshift({
          name: '-- All Products --',
          id: '0'
        });
      });

      RequisitionGroupsByProgram.get({
        program: selection
      }, function(data) {
        $scope.requisitionGroups = data.requisitionGroupList;
        $scope.requisitionGroups.unshift({
          'name': '-- All Requisition Groups --'
        });
      });
    } else {
      //$scope.filterObject.programId =  0;
      return;
    }
    $scope.filterGrid();
  });


  $scope.exportReport = function(type) {
    $scope.filterObject.pdformat = 1;
    var params = jQuery.param($scope.filterObject);
    var url = '/reports/download/average_consumption/' + type + '?' + params;
    window.open(url);
  };

  // the grid options
  $scope.tableParams = new ngTableParams({
    page: 1, // show first page
    total: 0, // length of data
    count: 25 // count per page
  });

  $scope.paramsChanged = function(params) {
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

  $scope.getPagedDataAsync = function(pageSize, page) {
    // Clear the results on the screen
    $scope.datarows = [];
    $scope.data = [];
    var params = {
      "max": 10000,
      "page": 1
    };

    $.each($scope.filterObject, function(index, value) {
      if (value !== undefined)
        params[index] = value;
    });

    AverageConsumptionReport.get(params, function(data) {
      if (data.pages !== undefined && data.pages.rows !== undefined) {
        $scope.MinMos = data.pages.rows[0].minMOS;
        $scope.MaxMos = data.pages.rows[0].maxMOS;
        $scope.data = data.pages.rows;
        $scope.paramsChanged($scope.tableParams);
      }
    });
  };
  $scope.getFacilitiesList = function() {
    var params = {
      "max": 10000,
      "page": 1
    };

    $.each($scope.filterObject, function(index, value) {
      if (value !== undefined)
        params[index] = value;
    });
    FacilityByProgramByFacilityType.get(params, function(data) {

      $scope.facillities = data.facilities;
      $scope.facillities.unshift({
        name: '-- All Facilities --'
      });
    });
  };
}
