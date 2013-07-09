/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('View non full supply controller',function(){
  var scope;

  beforeEach(inject(function($rootScope,$controller){
    scope = $rootScope.$new();
    $controller(ViewFullSupplyController, {$scope:scope});
  }));

  it('should set current line item and set lossesAndAdjustmentsModal to true ',function(){
    var linItem = new RnrLineItem();

    scope.showLossesAndAdjustments(linItem);

    expect(scope.currentRnrLineItem).toEqual(linItem);
    expect(scope.lossesAndAdjustmentsModal).toBeTruthy();
  });

  it('should  set lossesAndAdjustmentsModal to false ',function(){
    scope.closeLossesAndAdjustmentModal();

    expect(scope.lossesAndAdjustmentsModal).toBeFalsy();
  });
});