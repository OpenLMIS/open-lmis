/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('loss-and-adjustment', function () {
  it('should validate a lossAndAdjustment to be invalid if not quantity is present', function(){
      var lossAndAdjustment = new LossAndAdjustment({"type":{"name":"LOSS1", "additive":true}});
      expect(lossAndAdjustment.isQuantityValid()).toBeFalsy();
  });

  it('should validate a lossAndAdjustment to be valid if not quantity is present', function(){
      var lossAndAdjustment = new LossAndAdjustment({"type":{"name":"LOSS1", "additive":true}, "quantity":50});
      expect(lossAndAdjustment.isQuantityValid()).toBeTruthy();
  });


  it('should return true if two losses and adjustment have the same type name', function(){
      var lossAndAdjustment1 = new LossAndAdjustment({"type":{"name":"LOSS1"}, "quantity":60});
      var lossAndAdjustment2 = new LossAndAdjustment({"type":{"name":"LOSS1"}, "quantity":70});
      expect(lossAndAdjustment1.equals(lossAndAdjustment2)).toBeTruthy();
  });

  it('should return false if two losses and adjustment do not have the same type name', function(){
      var lossAndAdjustment1 = new LossAndAdjustment({"type":{"name":"LOSS1"}, "quantity":60});
      var lossAndAdjustment2 = new LossAndAdjustment({"type":{"name":"LOSS2"}, "quantity":60});
      expect(lossAndAdjustment1.equals(lossAndAdjustment2)).toBeFalsy();
  });
});

