INSERT INTO title_types (title_type) VALUES ('ZPTR');
SET @temp_id = (SELECT id FROM title_types WHERE title_type = 'ZPTR');

INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A615','Salgskontor per material per sone', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A783', 'Salgskontor per material per avfall per sone', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A791', 'Salgskontor per material per avfall per sone Per AP', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A790', 'Salgskontor per material per sone per AP', @temp_id);

INSERT INTO title_types (title_type) VALUES ('ZR05');
SET @temp_id = (SELECT id FROM title_types WHERE title_type = 'ZR05');

INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A704', 'Rabatt per kunde/material', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('?', 'Rabatt salgskontor per kunde per material per avfall', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A766', 'Rabatt salgskontor per kunde per material per apparattype', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A781', 'Rabatt salgskontor per apparatplass  per kunde per material ', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('?', 'Rabatt salgskontor per apparatplass  per kunde per material per avfall', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A780', 'Rabatt salgskontor per apparatplass  per kunde per material per apparattype', @temp_id);


INSERT INTO title_types (title_type) VALUES ('ZR02');
SET @temp_id = (SELECT id FROM title_types WHERE title_type = 'ZR02');

INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A704', 'Rabatt per kunde/material', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A785', 'Rabatt salgskontor per kunde per material per avfall', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A766', 'Rabatt salgskontor per kunde per material per apparattype', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A781', 'Rabatt salgskontor per apparatplass  per kunde per material ', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A784', 'Rabatt salgskontor per apparatplass  per kunde per material per avfall', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A780', 'Rabatt salgskontor per apparatplass  per kunde per material per apparattype', @temp_id);


INSERT INTO title_types (title_type) VALUES ('ZPRK');
SET @temp_id = (SELECT id FROM title_types WHERE title_type = 'ZPRK');

INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A704', 'Rabatt per kunde/material', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A785', 'Rabatt salgskontor per kunde per material per avfall', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A766', 'Rabatt salgskontor per kunde per material per apparattype', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A795', 'Rabatt salgskontor per kunde per sone  per material', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A781', 'Rabatt salgskontor per apparatplass  per kunde per material ', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A784', 'Rabatt salgskontor per apparatplass  per kunde per material per avfall', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A780', 'Rabatt salgskontor per apparatplass  per kunde per material per apparattype', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A798', 'Rabatt kunde/SD-dokument/Material', @temp_id);


INSERT INTO title_types (title_type) VALUES ('ZH00');
SET @temp_id = (SELECT id FROM title_types WHERE title_type = 'ZH00');

INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A767', 'Kundehiearki per material', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A786', 'Kundehiearki per material per avfall', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A768', 'Kundehiearki per material per apparattype', @temp_id);


INSERT INTO title_types (title_type) VALUES ('ZH02');
SET @temp_id = (SELECT id FROM title_types WHERE title_type = 'ZH02');

INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A767', 'Kundehiearki per material', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A786', 'Kundehiearki per material per avfall', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A768', 'Kundehiearki per material per apparattype', @temp_id);


INSERT INTO title_types (title_type) VALUES ('ZH03');
SET @temp_id = (SELECT id FROM title_types WHERE title_type = 'ZH03');

INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A767', 'Kundehiearki per material', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A786', 'Kundehiearki per material per avfall', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A805', 'Kundehiearki per material per apparattype', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A789', 'Kundehiearki per SK per material', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A775', 'Kundehiearki per SK, sone og material', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A805', 'Kundehiearki per SK, material og apparattype', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A770', 'Kundehiearki per SK, sone, material og apparattype', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A775', 'Kundehiearki per sone og material', @temp_id);


INSERT INTO title_types (title_type) VALUES ('ZGEB');
SET @temp_id = (SELECT id FROM title_types WHERE title_type = 'ZGEB');

INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A765', 'Salgsorg./DistrKanal/kunde', @temp_id);
INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('A778', 'Salgsorg./DistrKanal/node', @temp_id);

INSERT INTO title_types (title_type) VALUES ('ZBEH');
SET @temp_id = (SELECT id FROM title_types WHERE title_type = 'ZBEH');

INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('814', 'Behandlingsgebyr', @temp_id);
# : sorg/sk/kunde/apptype/varegruppe

INSERT INTO title_types (title_type) VALUES ('ZMIL');
SET @temp_id = (SELECT id FROM title_types WHERE title_type = 'ZMIL');

INSERT INTO key_combinations (key_combination, description, title_type_id) VALUES ('777', 'Dersom miljøgebyr er satt til 0, må saken inn til OP.', @temp_id); #:Sorg/oppdr.giv/material; 776:sorg/oppdr.giv/varegruppe;803:sorg/oppdr.giv/AP/Varegruppe

