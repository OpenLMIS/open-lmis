UPDATE regimens SET skipped = FALSE WHERE id IN (244, 246, 248, 250);

UPDATE regimens SET skipped = TRUE WHERE name LIKE 'Consultas AL STOCK Malaria %x6';