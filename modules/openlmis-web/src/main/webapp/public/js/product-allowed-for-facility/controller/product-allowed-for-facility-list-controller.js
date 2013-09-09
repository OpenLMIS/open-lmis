function ProductAllowedForFacilityListController($scope, $location, navigateBackService, ReportFacilityTypes, ProgramCompleteList,ScheduleCompleteList, GetFacilityTypeApprovedProductsCompleteList,GetFacilityTypeProgramProductAlreadyAllowedList,GetProductsCompleteListForAProgram, SaveApprovedProductForFacilityType, GetApprovedProductForFacilityTypeDetail, $dialog, messageService) {
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

                if (facilityType.name.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0) {
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

    $scope.$watchCollection('[selectedFacilityType, selectedProgram]', function(){
        $scope.loadFacilityTypeProgramProducts();
    });

    $scope.loadFacilityTypeProgramProducts = function (){
        if($scope.selectedFacilityType == null || $scope.selectedProgram == null){
            return;
        }

        GetProductsCompleteListForAProgram.get({programId: $scope.selectedProgram.id},function(data){
            $scope.products = data.products;

            GetFacilityTypeProgramProductAlreadyAllowedList.get({facilityTypeId:$scope.selectedFacilityType.id, programId:$scope.selectedProgram.id},function(data){
                $scope.alreadyAllowedProducts = data.products;

                angular.forEach($scope.products, function (product) {
                    angular.forEach($scope.alreadyAllowedProducts, function(allowedProducts){
                        if (allowedProducts.programProduct.product.id  == product.id) {
                            product.isSelected = true;
                            product.maxMonthsOfStock = allowedProducts.maxMonthsOfStock;
                            product.minMonthsOfStock = allowedProducts.minMonthsOfStock;
                        }
                    });

                });

            });
        });




        /*GetFacilityTypeApprovedProductsCompleteList.get({facilityTypeId:$scope.selectedFacilityType.id, programId: $scope.selectedProgram.id},function(data){
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


            angular.forEach($scope.products, function (product) {
                if ($scope.alreadyAllowedProducts.indexOf(product) >= 0) {
                    product.isSelected = true;
                }
            });

        },{});*/
    };

    $scope.saveFacilityTypeAllowedProductTypes = function(){
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

        angular.forEach($scope.products,function(programProduct){
            if(programProduct.isDataChanged){
                GetApprovedProductForFacilityTypeDetail.get({facilityTypeId: $scope.selectedFacilityType.id,programId: $scope.selectedProgram.id,productId: programProduct.id}, function(data){
                    var facilityTypeApprovedProduct = data.facilityTypeApprovedProduct;

                    if(facilityTypeApprovedProduct == null){
                        facilityTypeApprovedProduct={};
                        facilityTypeApprovedProduct.programProduct = programProduct;
                        facilityTypeApprovedProduct.facilityType = $scope.selectedFacilityType;
                    }

                    facilityTypeApprovedProduct.maxMonthsOfStock = programProduct.maxMonthsOfStock;
                    facilityTypeApprovedProduct.minMonthsOfStock = programProduct.minMonthsOfStock;

                    SaveApprovedProductForFacilityType.save(facilityTypeApprovedProduct,successHandler,errorHandler);

                });


            }
        })
    }

    $scope.setDataChanged = function(programProduct){
        programProduct.isDataChanged = true;
        $scope.isDataChanged = true;
    }

    
}