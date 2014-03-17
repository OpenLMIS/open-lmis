
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
     template: '<label class="labels">Program</label>' +
         '<div>' +
         ' <select class="input-large" ui-select2 ng-model="filter.program" >' +
         '     <option ng-repeat="program in programs" value="{{ program.id }}">{{program.name}}</option>' +
         ' </select>' +
         '</div>'
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
    template: '<label class="labels">Year</label>' +
        '<div>' +
        '<select class="input-medium" ui-select2 ng-model="filter.year">' +
          '<option ng-repeat="year in years" value="{{ year }}">{{year}}</option>' +
        '</select>' +
        '</div>'
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
    template: '<label class="labels">Facility Type</label>' +
        '<div>' +
        ' <select class="input-large" ui-select2 ng-model="filter.facilityType" ng-change="OnFilterChanged()">' +
        '     <option ng-repeat="type in facilityTypes" value="{{ type.id }}">{{type.name}}</option>' +
        ' </select>' +
        '</div>'
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
    template: '<label class="labels">Schedule</label>' +
        '<div>' +
        '<select class="input-medium" ui-select2 ng-model="filter.schedule">' +
          '<option ng-repeat="schedule in schedules" value="{{ schedule.id }}">{{schedule.name}}</option>' +
        '</select>' +
        '</div>'
  };
}]);


app.directive('periodFilter',['ReportPeriods','ReportPeriodsByScheduleAndYear' , function( ReportPeriods, ReportPeriodsByScheduleAndYear){

  var onCascadedVarsChanged = function($scope, newValue){

    if($scope.filter.year !== undefined && $scope.filter.schedule !== undefined){
        ReportPeriodsByScheduleAndYear.get({scheduleId: $scope.filter.schedule, year: $scope.filter.year}, function(data){
          $scope.periods = data.periods;
          if(data.periods !== undefined && data.periods.length > 0)
            $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});
        });

      }else{
        ReportPeriods.get({ scheduleId : $scope.filter.schedule },function(data) {
          $scope.periods = data.periods;
          if(data.periods !== undefined && data.periods.length > 0)
            $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});
        });
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
    template: '<label class="labels">Period</label><div>' +
        '<select class="input-medium" ui-select2 ng-model="filter.period" ng-change="OnFilterChanged();">' +
          '<option ng-repeat="period in periods" value="{{ period.id }}">{{period.name}}</option>' +
        '</select>' +
        '</div>'
  };
}]);


app.directive('requisitionGroupFilter',['RequisitionGroupsByProgram' , function( RequisitionGroupsByProgram ){

  var onRgCascadedVarsChanged = function($scope, newValue){

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
  };

  return {
    restrict: 'E',
    link: function(scope, elm, attr){
      scope.$watch('filter.program',function(value){
        onRgCascadedVarsChanged(scope, value);
      });

    },
    template: '<label class="labels">Requisition Groups</label><div>' +
        '<select class="input-large" ui-select2 ng-model="filter.requisitionGroup" ng-change="OnFilterChanged();">' +
        '     <option ng-repeat="requisitionGroup in requisitionGroups" value="{{ requisitionGroup.id }}">{{requisitionGroup.name}}</option>' +
        '</select>' +
        '</div>'
  };
}]);


app.directive('productCategoryFilter',['ProductCategoriesByProgram' , function( ProductCategoriesByProgram ){

  var onPgCascadedVarsChanged = function($scope, newValue){
      // load the program-product categories
      ProductCategoriesByProgram.get({programId: $scope.filter.program}, function (data) {
        $scope.productCategories = data.productCategoryList;
        $scope.productCategories.unshift({'name': '-- All Product Categories --'});
      });
  };

  return {
    restrict: 'E',
    link: function(scope, elm, attr){
      scope.$watch('filter.program',function(value){
        onPgCascadedVarsChanged(scope, value);
      });
    },
    template: '<label class="labels">Product Category</label><div>' +
        '<select class="input-large" ui-select2 ng-model="filter.productCategory" ng-change="OnFilterChanged();">' +
        '   <option  ng-repeat="option in productCategories" value="{{ option.id }}">{{ option.name }}</option>' +
        '</select>' +
        '</div>'
  };
}]);

app.directive('facilityFilter',['FacilitiesByProgramParams' , function( FacilitiesByProgramParams ){

  var onPgCascadedVarsChanged = function($scope, newValue){
    // load facilities
    FacilitiesByProgramParams.get({
          program: $scope.filter.program,
          schedule: angular.isDefined($scope.filter.schedule)?0: $scope.filter.schedule,
          type: angular.isDefined($scope.filter.facilityType)?0: $scope.filter.facilityType
        }, function (data) {
          $scope.facilities = data.facilities;
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
    template: '<label class="labels">Facility</label><div>' +
        '<select class="input-large" ui-select2 ng-model="filter.facility" ng-change="OnFilterChanged();">' +
        '   <option  ng-repeat="option in facilities" value="{{ option.id }}">{{ option.name }}</option>' +
        '</select>' +
        '</div>'
  };
}]);

app.directive('productFilter',['ReportProductsByProgram' , function( ReportProductsByProgram ){

  var onPgCascadedVarsChanged = function($scope, newValue){

    ReportProductsByProgram.get({programId: $scope.filter.program}, function (data) {
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
      scope.$watch('filter.program',function(value){
        onPgCascadedVarsChanged(scope, value);
      });
    },
    template: '<label class="labels">Product</label><div>' +
        '<select class="input-large" ui-select2 ng-model="filter.product" ng-change="OnFilterChanged();">' +
        '   <option  ng-repeat="option in products" value="{{ option.id }}">{{ option.name }}</option>' +
        '</select>' +
        '</div>'
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