function Distribution(deliveryZone, program, period) {
  this.deliveryZone = deliveryZone;
  this.program = program;
  this.period = period;

  function getZpp() {
    return this.deliveryZone.id + '_' + this.program.id + '_' + this.period.id;
  }

  return this;
}
