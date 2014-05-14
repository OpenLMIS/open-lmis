//services.factory('reportService', function($q,ReportPrograms){
//
//  var programs = function(ReportPrograms){
//    var deferred = $q.defer();
//
//    $timeout(function(){
//      ReportPrograms.get(function (data) {
//        var ps = data.programs;
//
//        ps.unshift({
//          'name': '-- Select Programs --'
//        });
//
//        deferred.resolve(ps);
//      });
//      return deferred.promise();
//    },100);
//  };
//
//  return {
//    programs: programs
//  }
//
//});



app.directive('filterContainer', ['$routeParams', '$location',function ($location) {
  return {
    restrict: 'EA',
    scope: true,
    controller: function($scope, $routeParams, $location){
      $scope.filter = angular.copy($routeParams);
      //$scope.OnFilterChanged();

      $scope.filterChanged = function(){
        // check if all of the required parameters have been specified
        angular.forEach($scope.requiredFilters, function(value){
          if(isUndefined($scope.filter[value]) || $scope.filter[value] === '' || $scope.filter[value] === 0 ){
            return;
          }
        });

        // update the url so users could take it, book mark it etc...
        if(JSON.stringify($scope.filter) !== JSON.stringify($routeParams)){
          var url = $location.url();
          url = url.substring(0, url.indexOf('?'));
          url = url + '?' + jQuery.param($scope.filter);
          $location.url(url);
        }
        $scope.$parent.filter = $scope.filter;
        $scope.$parent.OnFilterChanged();
      };

      $scope.filterChanged();
    },
    link: function (scope, elm, attrs) {
      angular.extend(scope, {
        //filter: {},
        requiredFilters: {},
        showMoreFilters: false,
        toggleMoreFilters: function () {
          scope.showMoreFilters = !scope.showMoreFilters;
        }
      });
    }
  };
}]);

// now comes the program filter
app.directive('programFilter', ['ReportPrograms',
  function (ReportPrograms) {
    return {
      restrict: 'E',
      require: '^filterContainer',
      link: function (scope, elm, attr) {

        if (attr.required) {
          scope.requiredFilters.program = 'program';
        }
        scope.$evalAsync(function(){
          ReportPrograms.get(function (data) {
            scope.programs = data.programs;
            scope.programs.unshift({
              'name': '-- Select Programs --'
            });
          });
        });
      },
      templateUrl: 'filter-program-template'
    };
}]);

app.directive('yearFilter', ['OperationYears',
  function (OperationYears) {
    return {
      restrict: 'E',
      controller: function($scope){

      },
      require: '^filterContainer',
      link: function (scope, elm, attr) {

        if (attr.required) {
          scope.requiredFilters.year = 'year';
        }
        scope.$evalAsync(function() {
          OperationYears.get(function (data) {
            scope.years = data.years;
            scope.filter.year = data.years[0];
          });
        });
      },
      templateUrl: 'filter-year-template'
    };
}]);

app.directive('facilityTypeFilter', ['ReportFacilityTypes','ReportFacilityTypesByProgram', '$routeParams',
  function (ReportFacilityTypes, ReportFacilityTypesByProgram , $routeParams) {

    var onCascadedPVarsChanged = function($scope, newValue){

      ReportFacilityTypesByProgram.get({program: $scope.filter.program}, function(data){
        $scope.facilityTypes = data.facilityTypes;
        $scope.facilityTypes.unshift({ 'name': '-- All Facility Types --', id: 0});
      });

    };

    return {
      restrict: 'E',
      link: function (scope, elm, attr) {

        scope.facilityTypes = [];
        scope.facilityTypes.unshift({  'name': '-- All Facility Types --', id: 0 });

        if (attr.required) {
          scope.requiredFilters.facilityType = 'facilityType';
        }

        scope.filter.facilityType = (isUndefined($routeParams.facilityType) || $routeParams.facilityType === '')? 0: $routeParams.facilityType;

        scope.$watch('filter.program', function (value) {
          onCascadedPVarsChanged(scope, value);
        });
      },
      templateUrl: 'filter-facility-type-template'
    };
}]);

app.directive('scheduleFilter', ['ReportSchedules','$routeParams',
  function (ReportSchedules, $routeParams) {

    return {
      restrict: 'E',
      require: '^filterContainer',
      link: function (scope, elm, attr) {

        scope.schedules = [];
        scope.schedules.unshift({
          name: '-- Select Group --'
        });
        scope.filter.schedule = $routeParams.schedule;

        if (attr.required) {
          scope.requiredFilters.schedule = 'schedule';
        }
        scope.$evalAsync(function() {
          ReportSchedules.get(function (data) {
            scope.schedules = data.schedules;
            scope.schedules.unshift({
              name: '-- Select Group --'
            });
          });
        });
      },
      templateUrl: 'filter-schedule-template'
    };
}]);


app.directive('periodFilter', ['ReportPeriods', 'ReportPeriodsByScheduleAndYear','$routeParams',
  function (ReportPeriods, ReportPeriodsByScheduleAndYear, $routeParams) {

    var onCascadedVarsChanged = function ($scope, newValue) {
      // don't call the server if you don't have all that it takes.
      if (isUndefined($scope.filter) || isUndefined($scope.filter.year) || isUndefined($scope.filter.schedule))
        return;

      if (angular.isDefined($scope.filter) && $scope.filter.year !== undefined && $scope.filter.schedule !== undefined) {
        ReportPeriodsByScheduleAndYear.get({
          scheduleId: $scope.filter.schedule,
          year: $scope.filter.year
        }, function (data) {
          $scope.periods = data.periods;
          if (data.periods !== undefined && data.periods.length > 0)
            $scope.periods.unshift({
              'name': '-- Select a Period --',
              'id': '0'
            });
          $scope.filter.period = $routeParams.period;
        });

      } else {
        if (angular.isDefined($scope.filter) && angular.isDefined($scope.filter.schedule)) {
          scope.$evalAsync(function() {
            ReportPeriods.get({
              scheduleId: $scope.filter.schedule
            }, function (data) {
              $scope.periods = data.periods;
              if (data.periods !== undefined && data.periods.length > 0)
                $scope.periods.unshift({ 'name': '-- Select a Period --', 'id': '0' });
            });
          });
        }

      }

    };

    return {
      restrict: 'E',
      require: '^filterContainer',
      link: function (scope, elm, attr) {

        scope.periods = [];
        scope.periods.push({
          name: '-- Select Period --'
        });

        if (attr.required) {
          scope.requiredFilters.period = 'period';
        }

        scope.$watch('filter.year', function (value) {
          onCascadedVarsChanged(scope, value);
        });
        scope.$watch('filter.schedule', function (value) {
          onCascadedVarsChanged(scope, value);
        });

      },
      templateUrl: 'filter-period-template'
    };
}]);


app.directive('requisitionGroupFilter', ['RequisitionGroupsByProgram','$routeParams',
  function (RequisitionGroupsByProgram, $routeParams) {

    var onRgCascadedVarsChanged = function ($scope, newValue) {

      if (isUndefined($scope.filter) || isUndefined($scope.filter.program) || $scope.filter.program === 0)
        return;

        RequisitionGroupsByProgram.get({
          program: $scope.filter.program
        }, function (data) {
          $scope.requisitionGroups = data.requisitionGroupList;
          if($scope.requisitionGroups === undefined || $scope.requisitionGroups.length === 0) {
            $scope.requisitionGroups = [];
            $scope.requisitionGroups.push({ 'name': '-- All Requisition Groups --', id: 0 });
          } else {
            $scope.requisitionGroups.unshift({ 'name': '-- All Requisition Groups --', id: 0  });
          }
        });
    };

    return {
      restrict: 'E',
      require: '^filterContainer',
      link: function (scope, elm, attr) {

        scope.requisitionGroups = [];
        scope.requisitionGroups.unshift({ 'name': '-- All Requisition Groups --', id: 0 });

        scope.filter.requisitionGroup = (isUndefined($routeParams.requisitionGroup) || $routeParams.requisitionGroup === '')? 0: $routeParams.requisitionGroup;

        if (attr.required) {
          scope.requiredFilters.requisitionGroup = 'requisitionGroup';
        }

        scope.$watch('filter.program', function (value) {
          onRgCascadedVarsChanged(scope, value);
        });
      },
      templateUrl: 'filter-requisition-group-template'
    };
}]);


app.directive('adjustmentTypeFilter',['AdjustmentTypes','$routeParams', function(AdjustmentTypes, $routeParams){

  return {
    restrict: 'E',
    require: '^filterContainer',
    link: function (scope, elm, attr) {

      AdjustmentTypes.get(function (data) {
        scope.adjustmentTypes = data.adjustmentTypeList;
        scope.adjustmentTypes.unshift({'description': '--All Adjustment Types --',id:0});
      });

      scope.filter.adjustmentType = (isUndefined($routeParams.adjustmentType) || $routeParams.adjustmentType === '')? 0: $routeParams.adjustmentType;

      if (attr.required) {
        scope.requiredFilters.adjustmentType = 'adjustmentType';
      }
    },
    templateUrl: 'filter-adjustment-type-template'
  };

}]);


app.directive('productCategoryFilter', ['ProductCategoriesByProgram','$routeParams',
  function (ProductCategoriesByProgram, $routeParams) {

    var onPgCascadedVarsChanged = function ($scope, newValue) {

      if (isUndefined($scope.filter) || isUndefined($scope.filter.program) || $scope.filter.program === 0)
        return;

      // load the program-product categories
      ProductCategoriesByProgram.get({
        programId: $scope.filter.program
      }, function (data) {
        $scope.productCategories = data.productCategoryList;
        $scope.productCategories.unshift({'name': '-- All Product Categories --', id: 0 });
        $scope.filter.productCategory = (isUndefined($routeParams.productCategory) || $routeParams.productCategory === '')? 0: $routeParams.productCategory;
      });
    };

    return {
      restrict: 'E',
      require: '^filterContainer',
      link: function (scope, elm, attr) {

        scope.productCategories = [];
        scope.productCategories.unshift({ 'name': '-- All Product Categories --', id: 0 });

        if (attr.required) {
          scope.requiredFilters.productCategory = 'productCategory';
        }

        scope.filter.productCategory = (isUndefined($routeParams.productCategory) || $routeParams.productCategory === '')? 0: $routeParams.productCategory;

        scope.$watch('filter.program', function (value) {
          onPgCascadedVarsChanged(scope, value);
        });
      },
      templateUrl: 'filter-product-category-template'
    };
}]);

app.directive('facilityFilter', ['FacilitiesByProgramParams', '$routeParams',
  function (FacilitiesByProgramParams, $routeParams) {

    var onPgCascadedVarsChanged = function ($scope, newValue) {

      if (isUndefined($scope.filter) || isUndefined($scope.filter.program) || $scope.filter.program === 0)
        return;

      var program = (angular.isDefined($scope.filter) && angular.isDefined($scope.filter.program)) ? $scope.filter.program : 0;
      var schedule = (angular.isDefined($scope.filter) && angular.isDefined($scope.filter.schedule)) ? $scope.filter.schedule : 0;
      var facilityType = (angular.isDefined($scope.filter) && angular.isDefined($scope.filter.facilityType)) ? $scope.filter.facilityType : 0;
      var requisitionGroup = (angular.isDefined($scope.filter) && angular.isDefined($scope.filter.requisitionGroup)) ? $scope.filter.requisitionGroup : 0;
      // load facilities
      FacilitiesByProgramParams.get({
        program: program,
        schedule: schedule,
        type: facilityType,
        requisitionGroup: requisitionGroup
      }, function (data) {
        $scope.facilities = data.facilities;
        if (isUndefined($scope.facilities)) {
          $scope.facilities = [];
        }
        $scope.facilities.unshift({
          name: '-- All Facilities --',id: 0
        });
      });
    };

    return {
      restrict: 'E',
      require: '^filterContainer',
      link: function (scope, elm, attr) {

        scope.facilities = [];
        scope.facilities.push({
          name: '-- All Facilities --', id: 0
        });

        scope.filter.facility = (isUndefined($routeParams.facility) || $routeParams.facility === '')? 0: $routeParams.facility;

          if (attr.required) {
          scope.requiredFilters.facility = 'facility';
        }

        scope.$watch('filter.requisitionGroup', function (value) {
          onPgCascadedVarsChanged(scope, value);
        });

        scope.$watch('filter.program', function (value) {
          onPgCascadedVarsChanged(scope, value);
        });
        scope.$watch('filter.schedule', function (value) {
          onPgCascadedVarsChanged(scope, value);
        });
        scope.$watch('filter.facilityType', function (value) {
          onPgCascadedVarsChanged(scope, value);
        });
      },
      templateUrl: 'filter-facility-template'
    };
}]);

app.directive('productFilter', ['ReportProductsByProgram','$routeParams',
  function (ReportProductsByProgram, $routeParams) {

    var onPgCascadedVarsChanged = function ($scope, newValue) {

      if (isUndefined($scope.filter) || isUndefined($scope.filter.program) || $scope.filter.program === 0)
        return;

      var program = (angular.isDefined($scope.filter) && angular.isDefined($scope.filter.program)) ? $scope.filter.program : 0;
      ReportProductsByProgram.get({
        programId: program
      }, function (data) {
        $scope.products = data.productList;
        $scope.products.unshift({
          'name': '-- Indicator Products --',
          id: 0
        });
        $scope.products.unshift({
          'name': '-- All Products --',
          id: -1
        });

      });

    };

    return {
      restrict: 'E',
      link: function (scope, elm, attr) {

        scope.products = [];
        scope.products.push({
          'name': '-- All Products --',
          id: -1
        });

          scope.filter.product = (isUndefined($routeParams.product) || $routeParams.product === '')? -1: $routeParams.product;

          if (attr.required) {
          scope.requiredFilters.product = 'product';
        }

        scope.productCFilter = function (option) {
          return (!angular.isDefined(scope.filter) || !angular.isDefined(scope.filter.productCategory) || scope.filter.productCategory === '' || scope.filter.productCategory === '0' || option.categoryId == scope.filter.productCategory);
        };


        scope.$watch('filter.program', function (value) {
          onPgCascadedVarsChanged(scope, value);
        });
      },
      templateUrl: 'filter-product-template'
    };

}]);


app.directive('programByRegimenFilter',['ReportRegimenPrograms', function(ReportRegimenPrograms){

    return {
        restrict: 'E',
        require: '^filterContainer',
        link: function (scope, elm, attr) {

            ReportRegimenPrograms.get(function (data) {
                scope.programs = data.regimenPrograms;
                scope.programs.unshift({'name': '--Select a Program --',id:-1});
            });

            if (attr.required) {
                scope.requiredFilters.program = 'program';
            }
        },
        templateUrl: 'filter-program-by-regimen-template'
    };

}]);

app.directive('regimenCategoryFilter',['ReportRegimenCategories', function(ReportRegimenCategories){

    return {
        restrict: 'E',
        require: '^filterContainer',
        link: function (scope, elm, attr) {

            ReportRegimenCategories.get(function (data) {
                scope.regimenCategories = data.regimenCategories;
                scope.regimenCategories.unshift({'name': '--All Regimen Categories --'});
            });

            if (attr.required) {
                scope.requiredFilters.regimenCategory = 'regimenCategory';
            }
        },
        templateUrl: 'filter-regimen-category-template'
    };
}]);

app.directive('regimenFilter', ['ReportRegimensByCategory','$routeParams',
    function (ReportRegimensByCategory, $routeParams) {

        var onPgCascadedVarsChanged = function ($scope, newValue) {

            if (isUndefined($scope.filter) || isUndefined($scope.filter.regimenCategory) || $scope.filter.regimenCategory === 0)
                return;

            var regimenCategory = (angular.isDefined($scope.filter) && angular.isDefined($scope.filter.regimenCategory)) ? $scope.filter.regimenCategory : 0;
            ReportRegimensByCategory.get({
                regimenCategoryId: regimenCategory
            }, function (data) {
                $scope.regimens = data.regimens;
                $scope.regimens.unshift({
                    'name': '-- All Regimens --'

                });
            });

        };


        return {
            restrict: 'E',
            link: function (scope, elm, attr) {

                scope.regimens = [];
                scope.regimens.push({
                    'name': '-- All Regimens --'

                });
                scope.filter.regimen = (isUndefined($routeParams.regimen) || $routeParams.regimen === '')? -1: $routeParams.regimen;

                if (attr.required) {
                    scope.requiredFilters.regimen = 'regimen';
                }
                scope.$watch('filter.regimenCategory', function (value) {
                    onPgCascadedVarsChanged(scope, value);
                });
            },
            templateUrl: 'filter-regimen-template'
        };

    }]);





app.directive('clientSideSortPagination', ['$filter', 'ngTableParams',
  function ($filter, ngTableParams) {

    return {
      restrict: 'A',
      link: function (scope, elm, attr) {

        // the grid options
        scope.tableParams = new ngTableParams({
          page: 1, // show first page
          total: 0, // length of data
          count: 25 // count per page
        });

        scope.paramsChanged = function (params) {

          // slice array data on pages
          if (scope.data === undefined) {
            scope.datarows = [];
            params.total = 0;
          } else {
            var data = scope.data;
            var orderedData = params.filter ? $filter('filter')(data, params.filter) : data;
            orderedData = params.sorting ? $filter('orderBy')(orderedData, params.orderBy()) : data;

            params.total = orderedData.length;
            scope.datarows = orderedData.slice((params.page - 1) * params.count, params.page * params.count);
            var i = 0;
            var baseIndex = params.count * (params.page - 1) + 1;
            while (i < scope.datarows.length) {
              scope.datarows[i].no = baseIndex + i;
              i++;
            }
          }
        };

        // watch for changes of parameters
        scope.$watch('tableParams', scope.paramsChanged, true);

      }
    };

}]);