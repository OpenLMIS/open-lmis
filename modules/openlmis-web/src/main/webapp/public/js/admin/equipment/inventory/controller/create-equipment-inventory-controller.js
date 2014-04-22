function CreateEquipmentInventoryController($scope, $location, $routeParams, EquipmentInventory, Equipments , SaveEquipmentInventory){
  
  Equipments.get(function(data){
    $scope.equipments = data.equipments;
  });
  
  if($routeParams.id === undefined){
    $scope.equipment = {};
    $scope.equipment.programId = $routeParams.programId;
    $scope.equipment.facilityId = $routeParams.facilityId;
  }else{
    EquipmentInventory.get({id: $routeParams.id}, function(data){

      $scope.equipment = data.inventory;

    });
  }
  
  $scope.saveEquipment = function () {
    SaveEquipmentInventory.save($scope.equipment, function (data) {
      // success
      $location.path('');
    }, function (data) {
      // error
      $scope.error = data.messages;
    });
  };

  $scope.cancelCreateEquipment = function () {
    $location.path('');
  };
}