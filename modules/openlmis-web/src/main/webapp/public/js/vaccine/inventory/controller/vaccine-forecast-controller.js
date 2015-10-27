function VaccineForecastingController($scope){

    $scope.pageLineItems = [];

    $scope.pageLineItems = {

        "forecasting": [
            {
                "productId": 2412,
                "productName":"BCG",
                "annualNeeds": 1000,
                "quarterlyNeed": 400,
                "reOrderLevel": "300",
                "bufferStock": "100",
                "maximumStock": "500",
                "productCategoryId": 100,
                "productCategoryName": "Vaccine"
            },
            {
                "productId": 2412,
                "productName":"Penta",
                "annualNeeds": 500,
                "quarterlyNeed": 200,
                "reOrderLevel": "150",
                "bufferStock": "50",
                "maximumStock": "20",
                "productCategoryId": 100,
                "productCategoryName": "Vaccine"
            },
            {
                "productId": 2413,
                "productName":"ADS 0.05ml",
                "annualNeeds": 500,
                "quarterlyNeed": 200,
                "reOrderLevel": "150",
                "bufferStock": "50",
                "maximumStock": "20",
                "productCategoryId": 100,
                "productCategoryName": "Safety Injection Equipment"
            }
        ]
    };


}
