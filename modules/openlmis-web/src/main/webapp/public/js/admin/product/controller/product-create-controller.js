function ProductCreateController($scope, $location, $dialog, messageService, CreateProduct, ProductGroups, ProductCategories, ReportPrograms, DosageUnits, ProductForms) {
    $scope.creationError = '';
    $scope.title = 'Products';
    $scope.programProductsCost = [];

    // clear the parent confirmation message if there was any
    $scope.$parent.message = '';

    // Programs list
    ReportPrograms.get(function (data) {
        $scope.product={};

        $scope.$parent.AddEditMode = true;

        //populate the defaults
        $scope.product.active = true;
        $scope.product.tracer = true;
        $scope.product.fullSupply = true;
        $scope.product.roundToZero = true;

        $scope.product.programProducts = [];
        $scope.programs = data.programs;

        for(var i = 0; i < data.programs.length; i++){
            var program = data.programs[i];
            $scope.product.programProducts.push({program:program , currentPrice: 0, dosesPerMonth:1, active:false, programName: program.name });
        }

    });

    // create product
    $scope.createProduct = function () {
        $scope.error = "";
        if ($scope.createProductForm.$invalid) {
            $scope.showErrorForCreate = true;
            return;
        }
        $scope.showErrorForCreate = false;
        CreateProduct.save( $scope.product, function (data) {
               $scope.$parent.message = 'New product created successfully';
               $location.path('');
               $scope.product = {};
            },  function ( data ) {
            $scope.message = "";
            $scope.creationError = data.data.error;
        });
    };

    $scope.cancelAddNewProduct = function (product) {
        $scope.$parent.newProductMode = false;
        $location.path('');
    };

    ProductCategories.get(function (data) {
        $scope.productCategories = data.productCategoryList;
    });

    DosageUnits.get(function (data) {
        $scope.dosageUnits = data.dosageUnits;
    });

    ProductForms.get(function (data) {
        $scope.productForms = data.productForms;
    });

    ProductGroups.get(function (data){
       $scope.productGroups = data.productGroups;
    });


}
