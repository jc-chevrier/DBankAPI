INSERT INTO ACCOUNT (id, first_name, last_name, birth_date, country, passport_number, phone_number, secret, iban, balance, date_added, active)
VALUES
('5d707d00945f4c7cba02e80ce826a2ad', 'Mario', 'Aliti', parsedatetime('1967-01-04', 'yyyy-MM-dd'), 'France', '413380089', '330663242134', '0e8f85d7-1b24-4fd7-b859-3c1ffed0c6be', 'FR1230001007941234567890142', 1540.65,  CURRENT_TIMESTAMP(), true),
('35707d00945f4c7cba02e80ce826a2ae', 'Mario', 'Aliti', parsedatetime('1968-01-04', 'yyyy-MM-dd'), 'France', '413380089', '330763282132', '0e8f85d7-1b24-4fd7-b859-3c1ffed0c6be', 'FR9430001007941234567890132', 2320,  CURRENT_TIMESTAMP(), true),
('4707d00945f4c7cba02e80ce826a233a', 'Mario', 'Olorin', parsedatetime('1983-02-17', 'yyyy-MM-dd'), 'France', '123380011', '330763282134', '0e8f85d7-1b24-4fd7-b859-3c1ffed0c621', 'FR2130001007941234567890176', 6110,  CURRENT_TIMESTAMP(), true),
('2d707d00945f4c7cba23e80ce826a2ad', 'Pierre', 'Liv', parsedatetime('1989-05-26', 'yyyy-MM-dd'), 'France', '533380022', '330663282125', 'ab8f85d7-1b24-4fd7-b859-3c1ffed0c6be', 'FR6510001007941234567890185', 2540,  CURRENT_TIMESTAMP(), true),
('5d707d00945f4c7cba02e344ce83a222', 'Christophe', 'Strise', parsedatetime('1997-06-18', 'yyyy-MM-dd'), 'France', '533380046', '330763282198', 'fe8f85d7-1b24-4fd7-b859-3c1ffed0c3b1', 'FR2130001007941234567890122', 521,  CURRENT_TIMESTAMP(), true),
('1d707d00945f4c7cba02e80ce826a232', 'Louis', 'Evans', parsedatetime('1999-02-26', 'yyyy-MM-dd'), 'France', '263380034', '330663282176', '238f85d7-1b24-4fd7-b859-3c1ffed0c631', 'FR3430001007941234567890184', 2001,  CURRENT_TIMESTAMP(), true),
('22707d00945f4c7344380ce83434abb1', 'Stéphane', 'Filio', parsedatetime('1993-07-25', 'yyyy-MM-dd'), 'France', '473380655', '330763282187', 'ee8f85d7-1b24-4fd7-b859-3c1ffed0c643', 'FR7426501007941234567890112', 7021.34,  CURRENT_TIMESTAMP(), true);

INSERT INTO CARD (id, number, cryptogram, expiration_date, code, ceiling, virtual, localization, contactless, blocked, expired, account_id, date_added, active)
VALUES
('ef707d00945f4c7cba02e80ce826a2ad', '6795753267527579',  '204', parsedatetime('2025-03', 'yyyy-MM'), '1542086', 3000, false, false, true, false, false, '5d707d00945f4c7cba02e80ce826a2ad', CURRENT_TIMESTAMP(), true),
('f4707d00945f4c7cba02e80ce826a243', '4595753267527978',  '232', parsedatetime('2026-01', 'yyyy-MM'), '1572001', 3400, false, false, true, false, false, '35707d00945f4c7cba02e80ce826a2ae', CURRENT_TIMESTAMP(), true),
('e5a707d00945f4c7cba02e80ce826a2a', '7595753267527538',  '101', parsedatetime('2023-12', 'yyyy-MM'), '1631461', 5000, false, false, true, false, false, '5d707d00945f4c7cba02e344ce83a222', CURRENT_TIMESTAMP(), true),
('6d707d00945f4c7cba02e80ce826a221', '1235753267531574',  '774', parsedatetime('2027-09', 'yyyy-MM'), '1689115', 4500, false, false, true, false, false, '1d707d00945f4c7cba02e80ce826a232', CURRENT_TIMESTAMP(), true),
('22707d00945f4c7cba02e80ce826a201', '13295753267527422',  '274', parsedatetime('2023-07', 'yyyy-MM'), '1510407', 2300, false, false, true, false, false, '22707d00945f4c7344380ce83434abb1', CURRENT_TIMESTAMP(), true);

INSERT INTO OPERATION (id, label, amount, second_account_name, second_account_country, second_account_iban, rate, category, confirmed, first_account_id, first_account_card_id, date_added, active)
VALUES
('57607d00945f4c7cba02e80ce826a268', 'Boulangerie NCY', 5.6, 'La boulangère', 'France', 'FR1230001007941234567890142', null, null, true, '5d707d00945f4c7cba02e80ce826a2ad', 'ef707d00945f4c7cba02e80ce826a2ad', CURRENT_TIMESTAMP(), true);