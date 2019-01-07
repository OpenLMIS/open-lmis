DO
$do$
BEGIN
  IF EXISTS (SELECT * FROM programs WHERE id = 3) THEN
    UPDATE regimens SET code = 'DET' WHERE name = 'HIV Determine Consumo';
    UPDATE regimens SET code = 'RDT Sífilis' WHERE name = 'Sífilis Teste Rápido Consumo';
    UPDATE regimens SET code = 'RDT Sífilis_inj' WHERE name = 'Sífilis Teste Rápido Injustificado';
    UPDATE regimens SET code = 'Uni' WHERE name = 'HIV Unigold Consumo';
    UPDATE regimens SET code = 'Uni+' WHERE name = 'HIV Unigold Positivos +';
    UPDATE regimens SET code = 'DET+' WHERE name = 'HIV Determine Positivos +';
    UPDATE regimens SET code = 'Uni_inj' WHERE name = 'HIV Unigold Injustificado';
    UPDATE regimens SET code = 'RDT Malaria_inj' WHERE name = 'Malaria Teste Rápido Injustificado';
    UPDATE regimens SET code = 'DET_inj' WHERE name = 'HIV Determine Injustificado';
    UPDATE regimens SET code = 'RDT Sífilis+' WHERE name = 'Sífilis Teste Positivos +';
    UPDATE regimens SET code = 'RDT Malaria+' WHERE name = 'Malaria Teste Positivos +';
    UPDATE regimens SET code = 'RDT Malaria' WHERE name = 'Malaria Teste Rápido Consumo';
  END IF;
END
$do$