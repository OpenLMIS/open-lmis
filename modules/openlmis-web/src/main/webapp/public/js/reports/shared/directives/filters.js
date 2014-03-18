
app.directive('filters', function(){
  return {
    restrict: 'E',
    link: function(scope, elm, attrs){
     angular.extend(scope,{
        filter:{}
     });
    },
    template: '<div class="filters"></div>'
  } ;
});

// now comes the program filter
app.directive('programFilter',['ReportPrograms' , function(ReportPrograms){
   return {
     restrict: 'E',
     link: function(scope, elm, attr){
       ReportPrograms.get(function (data) {
         scope.programs = data.programs;
         scope.programs.unshift({'name': '-- Select Programs --'});
       });
     },
     templateUrl: 'program-template'
   };
}]);

app.directive('yearFilter',['OperationYears' , function(OperationYears){
  return {
    restrict: 'E',
    link: function(scope, elm, attr){
      OperationYears.get(function (data) {
        scope.years = data.years;
        scope.years.unshift('-- Select Year --');
      });
    },
    templateUrl: 'year-template'
  };
}]);

app.directive('facilityTypeFilter',['ReportFacilityTypes' , function(ReportFacilityTypes){
  return {
    restrict: 'E',
    link: function(scope, elm, attr){
      ReportFacilityTypes.get(function (data) {
        scope.facilityTypes = data.facilityTypes;
        scope.facilityTypes.unshift({'name': '-- All Facility Types --'});
      });
    },
    templateUrl: 'facility-type-template'
  };
}]);

app.directive('scheduleFilter',['ReportSchedules' , function(ReportSchedules){
  return {
    restrict: 'E',
    link: function(scope, elm, attr){
      ReportSchedules.get(function (data) {
        scope.schedules = data.schedules;
        scope.schedules.unshift({name:'-- Select Group --'});
      });
    },
    templateUrl: 'schedule-template'
  };
}]);


app.directive('periodFilter',['ReportPeriods','ReportPeriodsByScheduleAndYear' , function( ReportPeriods, ReportPeriodsByScheduleAndYear){

  var onCascadedVarsChanged = function($scope, newValue){

    if(angular.isDefined($scope.filter) &&  $scope.filter.year !== undefined && $scope.filter.schedule !== undefined){
        ReportPeriodsByScheduleAndYear.get({scheduleId: $scope.filter.schedule, year: $scope.filter.year}, function(data){
          $scope.periods = data.periods;
          if(data.periods !== undefined && data.periods.length > 0)
            $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});
        });

      }else{
        if(angular.isDefined($scope.filter) && angular.isDefined($scope.filter.schedule)){
          ReportPeriods.get({ scheduleId : $scope.filter.schedule },function(data) {
            $scope.periods = data.periods;
            if(data.periods !== undefined && data.periods.length > 0)
              $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});
          });
        }

      }

  };

  return {
    restrict: 'E',
    link: function(scope, elm, attr){

      scope.$watch('filter.year',function(value){
          onCascadedVarsChanged(scope, value);
      });
      scope.$watch('filter.schedule',function(value){
        onCascadedVarsChanged(scope, value);
      });

    },
    templateUrl: 'period-template'
  };
}]);


app.directive('requisitionGroupFilter',['RequisitionGroupsByProgram' , function( RequisitionGroupsByProgram ){

  var onRgCascadedVarsChanged = function($scope, newValue){

    if(angular.isDefined($scope.filter) && angular.isDefined($scope.filter.program)){

      RequisitionGroupsByProgram.get({program: $scope.filter.program }, function (data) {
            $scope.requisitionGroups = data.requisitionGroupList;
            if ($scope.requisitionGroups === undefined || $scope.requisitionGroups.length === 0) {
              $scope.requisitionGroups = [];
              $scope.requisitionGroups.push({'name': '-- All Requisition Groups --'});
            } else {
              $scope.requisitionGroups.unshift({'name': '-- All Requisition Groups --'});
            }
          }
      );

    }

  };

  return {
    restrict: 'E',
    link: function(scope, elm, attr){
      scope.$watch('filter.program',function(value){
        onRgCascadedVarsChanged(scope, value);
      });

    },
    templateUrl: 'requisition-group-template'
  };
}]);


app.directive('productCategoryFilter',['ProductCategoriesByProgram' , function( ProductCategoriesByProgram ){

  var onPgCascadedVarsChanged = function($scope, newValue){

    if(angular.isDefined($scope.filter) && angular.isDefined($scope.filter.program)){
      // load the program-product categories
      ProductCategoriesByProgram.get({programId: $scope.filter.program}, function (data) {
        $scope.productCategories = data.productCategoryList;
        $scope.productCategories.unshift({'name': '-- All Product Categories --'});
      });
    }

  };

  return {
    restrict: 'E',
    link: function(scope, elm, attr){
      scope.$watch('filter.program',function(value){
        onPgCascadedVarsChanged(scope, value);
      });
    },
    templateUrl: 'product-category-template'
  };
}]);

app.directive('facilityFilter',['FacilitiesByProgramParams' , function( FacilitiesByProgramParams ){

  var onPgCascadedVarsChanged = function($scope, newValue){
    var program = (angular.isDefined($scope.filter) && angular.isDefined($scope.filter.program))?$scope.filter.program : 0;
    var schedule = (angular.isDefined($scope.filter) && angular.isDefined($scope.filter.schedule))?$scope.filter.schedule: 0;
    var facilityType = (angular.isDefined($scope.filter) && angular.isDefined($scope.filter.facilityType))?$scope.filter.facilityType: 0;
    // load facilities
    FacilitiesByProgramParams.get({
          program: program,
          schedule: schedule,
          type: facilityType
        }, function (data) {
          $scope.facilities = data.facilities;
          if($scope.facilities === null){
            $scope.facilities = [];
          }
          $scope.facilities.unshift({name: '-- All Facilities --'});
        }
    );
  };

  return {
    restrict: 'E',
    link: function(scope, elm, attr){
      scope.$watch('filter.program',function(value){
        onPgCascadedVarsChanged(scope, value);
      });
      scope.$watch('filter.schedule',function(value){
        onPgCascadedVarsChanged(scope, value);
      });
      scope.$watch('filter.facilityType',function(value){
        onPgCascadedVarsChanged(scope, value);
      });
    },
    templateUrl: 'facility-template'
  };
}]);

app.directive('productFilter',['ReportProductsByProgram' , function( ReportProductsByProgram ){

  var onPgCascadedVarsChanged = function($scope, newValue){
    var program = (angular.isDefined($scope.filter) && angular.isDefined($scope.filter.program))?$scope.filter.program : 0;
    ReportProductsByProgram.get({programId: program }, function (data) {
      $scope.products = data.productList;
      if ($scope.products.length === 0) {
        $scope.products.push({'name': '-- All Products --'});
      } else {
        $scope.products.unshift({'name': '-- All Products --'});
      }
    });

  };


  return {
    restrict: 'E',
    link: function(scope, elm, attr){


      scope.productCFilter = function(option){
        return  ( !angular.isDefined(scope.filter) || !angular.isDefined(scope.filter.productCategory) ||  option.categoryId == scope.filter.productCategory );
      };


      scope.$watch('filter.program',function(value){
        onPgCascadedVarsChanged(scope, value);
      });
    },
    templateUrl: 'product-template'
  };

}]);


app.directive('clientSideSortPagination', ['$filter','ngTableParams', function( $filter, ngTableParams ){

  return {
    restrict: 'A',
    link: function(scope, elm, attr){

      // the grid options
      scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        total: 0,           // length of data
        count: 25           // count per page
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