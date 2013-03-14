var LossAndAdjustment = function (lossAndAdjustmentJSon) {
  this.type = lossAndAdjustmentJSon.type;
  this.quantity = lossAndAdjustmentJSon.quantity;

  LossAndAdjustment.prototype.isQuantityValid = function () {
    return isUndefined(this.quantity)? false:true;
  };

  LossAndAdjustment.prototype.equals = function(lossAndAdjustment) {
    return this.type.name == lossAndAdjustment.type.name;
  }
};
