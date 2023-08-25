INSERT INTO condition_codes (code) VALUES ('ZPTR');
SET @temp_id = (SELECT id FROM condition_codes WHERE code = 'ZPTR');

INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A615','Salgskontor per material per sone', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A783', 'Salgskontor per material per avfall per sone', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A791', 'Salgskontor per material per avfall per sone Per AP', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A790', 'Salgskontor per material per sone per AP', @temp_id);

INSERT INTO condition_codes (code) VALUES ('ZR05');
SET @temp_id = (SELECT id FROM condition_codes WHERE code = 'ZR05');

INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A704', 'Rabatt per kunde/material', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('?', 'Rabatt salgskontor per kunde per material per avfall', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A766', 'Rabatt salgskontor per kunde per material per apparattype', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A781', 'Rabatt salgskontor per apparatplass  per kunde per material ', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('?', 'Rabatt salgskontor per apparatplass  per kunde per material per avfall', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A780', 'Rabatt salgskontor per apparatplass  per kunde per material per apparattype', @temp_id);


INSERT INTO condition_codes (code) VALUES ('ZR02');
SET @temp_id = (SELECT id FROM condition_codes WHERE code = 'ZR02');

INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A704', 'Rabatt per kunde/material', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A785', 'Rabatt salgskontor per kunde per material per avfall', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A766', 'Rabatt salgskontor per kunde per material per apparattype', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A781', 'Rabatt salgskontor per apparatplass  per kunde per material ', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A784', 'Rabatt salgskontor per apparatplass  per kunde per material per avfall', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A780', 'Rabatt salgskontor per apparatplass  per kunde per material per apparattype', @temp_id);


INSERT INTO condition_codes (code) VALUES ('ZPRK');
SET @temp_id = (SELECT id FROM condition_codes WHERE code = 'ZPRK');

INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A704', 'Rabatt per kunde/material', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A785', 'Rabatt salgskontor per kunde per material per avfall', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A766', 'Rabatt salgskontor per kunde per material per apparattype', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A795', 'Rabatt salgskontor per kunde per sone  per material', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A781', 'Rabatt salgskontor per apparatplass  per kunde per material ', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A784', 'Rabatt salgskontor per apparatplass  per kunde per material per avfall', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A780', 'Rabatt salgskontor per apparatplass  per kunde per material per apparattype', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A798', 'Rabatt kunde/SD-dokument/Material', @temp_id);


INSERT INTO condition_codes (code) VALUES ('ZH00');
SET @temp_id = (SELECT id FROM condition_codes WHERE code = 'ZH00');

INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A767', 'Kundehiearki per material', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A786', 'Kundehiearki per material per avfall', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A768', 'Kundehiearki per material per apparattype', @temp_id);


INSERT INTO condition_codes (code) VALUES ('ZH02');
SET @temp_id = (SELECT id FROM condition_codes WHERE code = 'ZH02');

INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A767', 'Kundehiearki per material', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A786', 'Kundehiearki per material per avfall', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A768', 'Kundehiearki per material per apparattype', @temp_id);


INSERT INTO condition_codes (code) VALUES ('ZH03');
SET @temp_id = (SELECT id FROM condition_codes WHERE code = 'ZH03');

INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A767', 'Kundehiearki per material', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A786', 'Kundehiearki per material per avfall', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A805', 'Kundehiearki per material per apparattype', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A789', 'Kundehiearki per SK per material', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A775', 'Kundehiearki per SK, sone og material', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A805', 'Kundehiearki per SK, material og apparattype', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A770', 'Kundehiearki per SK, sone, material og apparattype', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A775', 'Kundehiearki per sone og material', @temp_id);


INSERT INTO condition_codes (code) VALUES ('ZGEB');
SET @temp_id = (SELECT id FROM condition_codes WHERE code = 'ZGEB');

INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A765', 'Salgsorg./DistrKanal/kunde', @temp_id);
INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('A778', 'Salgsorg./DistrKanal/node', @temp_id);

INSERT INTO condition_codes (code) VALUES ('ZBEH');
SET @temp_id = (SELECT id FROM condition_codes WHERE code = 'ZBEH');

INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('814', 'Behandlingsgebyr', @temp_id);

INSERT INTO condition_codes (code) VALUES ('ZMIL');
SET @temp_id = (SELECT id FROM condition_codes WHERE code = 'ZMIL');

INSERT INTO key_combinations (key_combination, description, condition_code_id) VALUES ('777', 'Dersom miljøgebyr er satt til 0, må saken inn til OP.', @temp_id);

