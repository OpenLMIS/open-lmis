/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
function DistributionListController($scope, DeliveryZoneFacilities) {

  var db;
  var request = indexedDB.open("open_lmis", 1);
  $scope.distributionList =[];

  request.onsuccess = function (event) {
    console.log(event.currentTarget);
    db = event.currentTarget.result;
  };

  request.onupgradeneeded = function (event) {
    db = event.target.result;
    var facilityStore = db.createObjectStore("facilityData", {
      "keyPath": "programId"
    });

    var distributionStore = db.createObjectStore("distribution", {
      "keyPath": "id"
    });

    distributionStore.createIndex("id", "id", {"unique": true});

    console.info(db);
  };

  $scope.initiateDistribution = function () {
    var key = $scope.selectedZone.id.toString()+'_'+$scope.selectedProgram.id.toString()+'_'+$scope.selectedPeriod.id.toString();
//    $scope.distributionList = fetchDistributions();

    DeliveryZoneFacilities.get({deliveryZoneId: $scope.selectedZone.id, programId: $scope.selectedProgram.id}, function (data) {
      cacheFacilityData(data.facilities);
      var distribution = {"deliveryZone": $scope.selectedZone, "program": $scope.selectedProgram, "period": $scope.selectedPeriod, "status": "Initiated",
        "id":key};
      $scope.distributionList.push(distribution);
      cacheDistribution(distribution);
    }, {});

  };

//  function fetchDistributions(){
//    $scope.distributionList=[];
//    var transaction = db.transaction('distribution', 'readWrite');
//    var distributionObjectStore = transaction.objectStore('distribution');
//    var index = distributionObjectStore.index("id");
//
//    index.openKeyCursor().onsuccess = function(event) {
//      var cursor = event.target.result;
//      if (cursor) {
//        $scope.distributionList.push(cursor.value);
//        cursor.continue();
//      }
//    };
//  }

  function cacheFacilityData(facilityList) {
    var transaction = db.transaction('facilityData', 'readwrite');
    var objects = transaction.objectStore('facilityData');

    var cacheObject = {"facilityList": facilityList, "programId": $scope.selectedProgram.id}

    objects.put(cacheObject);


    transaction.oncomplete = function () {
      console.log('facility data saved');
    }
  }

  function  cacheDistribution(distribution){
    var transaction = db.transaction('distribution', 'readwrite');
    var objects = transaction.objectStore('distribution');

    objects.put(distribution);


    transaction.oncomplete = function () {
      console.log('distribution saved successfully');
    }

  }

}

