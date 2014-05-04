ALTER TABLE facility_program_equipments
  ADD isActive BOOLEAN NOT NULL default(true),
  ADD dateDecommissioned DATE NULL,
  ADD hasServiceContract BOOLEAN NOT NULL default(false),
  ADD serviceContractEndDate DATE NULL,
  ADD primaryDonorId INT NULL REFERENCES donors(id)