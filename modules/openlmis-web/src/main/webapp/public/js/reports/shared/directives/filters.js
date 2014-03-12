
app.directive('filters', function(){
  return {
    restrict: 'E',
    link: function(scope, elm, attrs){

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
         ' <select class="input-large" ui-select2 ng-model="program" >' +
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
        '<select class="input-medium" ui-select2 ng-model="year">' +
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
        ' <select class="input-large" ui-select2 ng-model="facilityType" ng-change="OnFilterChanged()">' +
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
        '<select class="input-medium" ui-select2 ng-model="schedule">' +
          '<option ng-repeat="schedule in schedules" value="{{ schedule.id }}">{{schedule.name}}</option>' +
        '</select>' +
        '</div>'
  };
}]);


app.directive('periodFilter',['ReportPeriods','ReportPeriodsByScheduleAndYear' , function( ReportPeriods, ReportPeriodsByScheduleAndYear){

  var onCascadedVarsChanged = function($scope, newValue){

    if($scope.year !== undefined && $scope.schedule !== undefined){
        ReportPeriodsByScheduleAndYear.get({scheduleId: $scope.schedule, year: $scope.year}, function(data){
          $scope.periods = data.periods;
          if(data.periods !== undefined && data.periods.length > 0)
            $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});
        });

      }else{
        ReportPeriods.get({ scheduleId : $scope.schedule },function(data) {
          $scope.periods = data.periods;
          if(data.periods !== undefined && data.periods.length > 0)
            $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});
        });
      }

  };

  return {
    restrict: 'E',
    link: function(scope, elm, attr){

      scope.$watch('year',function(value){
          onCascadedVarsChanged(scope, value);
      });
      scope.$watch('schedule',function(value){
        onCascadedVarsChanged(scope, value);
      });

    },
    template: '<label class="labels">Period</label><div>' +
        '<select class="input-medium" ui-select2 ng-model="period" ng-change="OnFilterChanged();">' +
          '<option ng-repeat="period in periods" value="{{ period.id }}">{{period.name}}</option>' +
        '</select>' +
        '</div>'
  };
}]);


app.directive('requisitionGroupFilter',['RequisitionGroupsByProgram' , function( RequisitionGroupsByProgram ){

  var onRgCascadedVarsChanged = function($scope, newValue){

    RequisitionGroupsByProgram.get({program: $scope.program }, function (data) {
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
      scope.$watch('program',function(value){
        onRgCascadedVarsChanged(scope, value);
      });

    },
    template: '<label class="labels">Requisition Groups</label><div>' +
        '<select class="input-large" ui-select2 ng-model="requisitionGroup" ng-change="OnFilterChanged();">' +
        '     <option ng-repeat="requisitionGroup in requisitionGroups" value="{{ requisitionGroup.id }}">{{requisitionGroup.name}}</option>' +
        '</select>' +
        '</div>'
  };
}]);


app.directive('productCategoryFilter',['ProductCategoriesByProgram' , function( ProductCategoriesByProgram ){

  var onPgCascadedVarsChanged = function($scope, newValue){
      // load the program-product categories
      ProductCategoriesByProgram.get({programId: $scope.program}, function (data) {
        $scope.productCategories = data.productCategoryList;
        $scope.productCategories.unshift({'name': '-- All Product Categories --'});
      });
  };

  return {
    restrict: 'E',
    link: function(scope, elm, attr){
      scope.$watch('program',function(value){
        onPgCascadedVarsChanged(scope, value);
      });
    },
    template: '<label class="labels">Product Category</label><div>' +
        '<select class="input-large" ui-select2 ng-model="productCategory" ng-change="OnFilterChanged();">' +
        '   <option  ng-repeat="option in productCategories" value="{{ option.id }}">{{ option.name }}</option>' +
        '</select>' +
        '</div>'
  };
}]);

app.directive('productCategoryFilter',['FacilitiesByProgramParams' , function( FacilitiesByProgramParams ){

  var onPgCascadedVarsChanged = function($scope, newValue){
    // load facilities
    FacilitiesByProgramParams.get({
          program: $scope.program,
          schedule: 0,
          type: 0
        }, function (data) {
          $scope.facilities = data.facilities;
          $scope.facilities.unshift({name: '-- All Facilities --'});
        }
    );
  };

  return {
    restrict: 'E',
    link: function(scope, elm, attr){
      scope.$watch('program',function(value){
        onPgCascadedVarsChanged(scope, value);
      });
    },
    template: '<label class="labels">Facility</label><div>' +
        '<select class="input-large" ui-select2 ng-model="facility" ng-change="OnFilterChanged();">' +
        '   <option  ng-repeat="option in facilities" value="{{ option.id }}">{{ option.name }}</option>' +
        '</select>' +
        '</div>'
  };
}]);