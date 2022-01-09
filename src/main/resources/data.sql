INSERT INTO ACCOUNT (id, date_added, first_name, last_name, birth_date, country, passport_number, phone_number, secret, iban, balance, active)
VALUES
('5d707d00945f4c7cba02e80ce826a2ad', CURRENT_TIMESTAMP(), 'mario', 'aliti', parsedatetime('1967-01-26', 'yyyy-MM-dd'), 'France', '533380006', '330763282134', 'secret', 'FR7630001007941234567890185', 540.65, true),
('12707d00945f4c7cba02e80ce826a2ad', CURRENT_TIMESTAMP(), 'louis', 'dals', parsedatetime('1993-04-21', 'yyyy-MM-dd'), 'France', '533380654', '330763282134', 'secret', 'FR7886501007941234567890185', 7021.34, true);