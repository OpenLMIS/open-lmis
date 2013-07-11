function Distribution(deliveryZoneId, programId, periodId) {
  this.deliveryZone = {id: deliveryZoneId};
  this.program = {id: programId};
  this.period = {id: periodId};

  return this;
}
