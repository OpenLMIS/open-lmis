function ProductAllowedForFacilityListController($scope, $location, navigateBackService, ReportFacilityTypes, ProgramCompleteList,ScheduleCompleteList, ProgramProducts, GetFacilityApprovedProductsCompleteList,GetFacilityProgramProductAlreadyAllowedList, SaveRequisitionGroupProgramSchedule, $dialog, messageService) {
    $scope.$on('$viewContentLoaded', function () {
        $scope.$apply($scope.query = navigateBackService.query);
        $scope.showFacilityTypeList('txtFilterFacilityTypeName');
        $scope.loadAllPrograms();
    });
    $scope.previousQuery = '';
    $scope.isDataChanged = false;
    $scope.originalSchedule = null;
    $scope.selectedSchedule = null;

    $scope.showFacilityTypeList = function (id) {

        ReportFacilityTypes.get(function (data) {
            $scope.filteredFacilityTypes = data.facilityTypes;
            $scope.facilityTypeList = $scope.filteredFacilityTypes;
        });

        var query = document.getElementById(id).value;
        $scope.query = query;

        filterFacilityTypesByName(query);
        return true;
    };

    $scope.loadAllPrograms = function(){
        ProgramCompleteList.get(function(data){
            $scope.programs = data.programs;
        });
    }


    ScheduleCompleteList.get(function(data){
        $scope.schedules = data.schedules;
    });

    $scope.editRequisitionGroup = function (id) {
        var data = {query: $scope.query};
        navigateBackService.setData(data);
        $location.path('edit/' + id);
    };


    $scope.clearSearch = function () {
        $scope.query = "";
        $scope.resultCount = 0;
        angular.element("#txtFilterRequisitionGroups").focus();
    };

    var filterFacilityTypesByName = function (query) {
        query = query || "";

        if (query.length == 0) {
            $scope.filteredFacilityTypes = $scope.facilityTypeList;
        }
        else {
            $scope.filteredFacilityTypes = [];
            angular.forEach($scope.facilityTypeList, function (facilityType) {

                if (facility.name.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0) {
                    $scope.filteredFacilityTypes.push(facilityType);
                }
            });
            $scope.resultCount = $scope.filteredFacilityTypes.length;
        }
    };

    $scope.filterFacilityType = function (id) {
        var query = document.getElementById(id).value;
        $scope.query = query;
        filterFacilityTypesByName(query);
    };

    $scope.getFacilityTypeColor = function(facilityType){
        if($scope.selectedFacilityType== null){
            return 'none';
        }

        if($scope.selectedFacilityType.id == facilityType.id){
            return "background-color : teal; color: white";
        }
        else{
            return 'none';
        }

    };

    $scope.setSelectedFacilityType = function (facilityType){
        $scope.selectedFacilityType = facilityType;
        $scope.selectedSchedule = null;
    };

    $scope.getProgramColor = function(program){
        if($scope.selectedProgram== null){
            return 'none';
        }

        if($scope.selectedProgram.code == program.code){
            return "background-color : teal; color: white";
        }
        else{
            return 'none';
        }

    };



    $scope.setSelectedProgram = function (program){
        $scope.selectedProgram = program;
    };

    $scope.$watchCollection('[selectedFacility, selectedProgram]', function(){
        $scope.loadFacilityProgramProducts();
    });

    $scope.loadFacilityProgramProducts = function (){
        if($scope.selectedFacilityType == null || $scope.selectedProgram == null){
            return;
        }

        ProgramProducts.get({programId: $scope.selectedProgram.id},function(data){
            $scope.products = data.products;

            if($scope.products==null){
                $scope.message = "No products allowed for " + $scope.selectedFacilityType.name + " in program: " + $scope.selectedProgram.name;
                $scope.showMessage = true;
            }
            else{
                $scope.message="";
                $scope.showMessage=false;

                //$scope.setOriginallySelectedSchedule($scope.selectedRequisitionGroupProgramSchedule.processingSchedule)
            }
        },{});

        GetFacilityProgramProductAlreadyAllowedList.get({facilityId:$scope.selectedFacilityType.id, programId:$scope.selectedProgram.id},function(data){
            $scope.alreadyAllowedProducts = data.programProductList;
        });

        angular.forEach($scope.products, function (product) {
            if ($scope.alreadyAllowedProducts.indexOf(product) >= 0) {
                product.isSelected = true;
            }
        });
    };

    $scope.setDataChanged = function(){
        $scope.isDataChanged = true;
    }

    /*$scope.setOriginallySelectedSchedule = function (schedule){
        angular.forEach($scope.schedules,function(scheduleEntry){
            if(scheduleEntry.id == schedule.id){
                $scope.selectedSchedule = scheduleEntry;
            }
        });
    } */

    $scope.saveRequisitionGroupProgramSchedule = function(){
        var successHandler = function (response) {
            $scope.requisitionGroupProgramSchedule = response.requisitionGroupProgramSchedule;
            $scope.showError = false;
            $scope.error = "";
            $scope.message = response.success;
            $scope.showMessage = true;
        };

        var errorHandler = function (response) {
            $scope.showError = true;
            $scope.error = response.data.error;
        };

        $scope.selectedRequisitionGroupProgramSchedule.requisitionGroup = $scope.selectedRequisitionGroup;
        $scope.selectedRequisitionGroupProgramSchedule.program = $scope.selectedProgram;
        $scope.selectedRequisitionGroupProgramSchedule.processingSchedule = $scope.selectedSchedule;

        SaveRequisitionGroupProgramSchedule.save($scope.selectedRequisitionGroupProgramSchedule,successHandler,errorHandler);
    }

    
}