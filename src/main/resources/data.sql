INSERT INTO ACCOUNT (id, date_added, first_name, last_name, birth_date, passport_number, secret, iban, balance, active)
VALUES
('5d707d00945f4c7cba02e80ce826a2ad', CURRENT_TIMESTAMP(), 'mario', 'aliti', parsedatetime('26/01/1967', 'dd/MM/yyyy'), '533380006', 'secret', 'FR7630001007941234567890185', 540.65, true),
('12707d00945f4c7cba02e80ce826a2ad', CURRENT_TIMESTAMP(), 'louis', 'dals', parsedatetime('21/04/1993', 'dd/MM/yyyy'), '533380654', 'secret', 'FR7886501007941234567890185', 7021.34, true);