-- Modification ordre bouton action worflow

-- MAJ group 
 UPDATE ticketing_param_bouton_action SET id_groupe = 4 WHERE id_action = 346;
 
 -- MAJ ordre affichage selon action
 UPDATE workflow_action
SET display_order=2
WHERE id_action=303;
UPDATE workflow_action
SET display_order=3
WHERE id_action=308;
UPDATE workflow_action
SET display_order=4
WHERE id_action=307;
UPDATE workflow_action
SET display_order=5
WHERE id_action=352;
UPDATE workflow_action
SET display_order=6
WHERE id_action=355;
UPDATE workflow_action
SET display_order=7
WHERE id_action=302;
UPDATE workflow_action
SET display_order=8
WHERE id_action=324;
UPDATE workflow_action
SET display_order=9
WHERE id_action=334;
UPDATE workflow_action
SET display_order=10
WHERE id_action=336;
UPDATE workflow_action
SET display_order=11
WHERE id_action=335;
UPDATE workflow_action
SET display_order=12
WHERE id_action=309;
UPDATE workflow_action
SET display_order=13
WHERE id_action=312;
UPDATE workflow_action
SET display_order=14
WHERE id_action=305;
UPDATE workflow_action
SET display_order=15
WHERE id_action=304;
UPDATE workflow_action
SET display_order=16
WHERE id_action=317;
UPDATE workflow_action
SET display_order=17
WHERE id_action=326;
UPDATE workflow_action
SET display_order=18
WHERE id_action=306;
UPDATE workflow_action
SET display_order=19
WHERE id_action=346;
UPDATE workflow_action
SET display_order=20
WHERE id_action=325;
UPDATE workflow_action
SET display_order=22
WHERE id_action=310;
UPDATE workflow_action
SET display_order=23
WHERE id_action=311;
UPDATE workflow_action
SET display_order=24
WHERE id_action=331;
UPDATE workflow_action
SET display_order=26
WHERE id_action=315;
UPDATE workflow_action
SET display_order=27
WHERE id_action=314;
UPDATE workflow_action
SET display_order=28
WHERE id_action=353;
UPDATE workflow_action
SET display_order=29
WHERE id_action=356;
UPDATE workflow_action
SET display_order=30
WHERE id_action=316;
UPDATE workflow_action
SET display_order=31
WHERE id_action=319;
UPDATE workflow_action
SET display_order=32
WHERE id_action=329;
UPDATE workflow_action
SET display_order=33
WHERE id_action=337;
UPDATE workflow_action
SET display_order=34
WHERE id_action=313;
UPDATE workflow_action
SET display_order=35
WHERE id_action=347;
UPDATE workflow_action
SET display_order=36
WHERE id_action=348;
UPDATE workflow_action
SET display_order=37
WHERE id_action=323;
UPDATE workflow_action
SET display_order=38
WHERE id_action=354;
UPDATE workflow_action
SET display_order=39
WHERE id_action=357;
UPDATE workflow_action
SET display_order=40
WHERE id_action=318;
UPDATE workflow_action
SET display_order=40
WHERE id_action=333;
UPDATE workflow_action
SET display_order=41
WHERE id_action=320;
UPDATE workflow_action
SET display_order=42
WHERE id_action=322;
UPDATE workflow_action
SET display_order=43
WHERE id_action=327;
UPDATE workflow_action
SET display_order=44
WHERE id_action=328;
UPDATE workflow_action
SET display_order=45
WHERE id_action=330;
UPDATE workflow_action
SET display_order=46
WHERE id_action=332;
UPDATE workflow_action
SET display_order=47
WHERE id_action=340;
UPDATE workflow_action
SET display_order=48
WHERE id_action=341;
UPDATE workflow_action
SET display_order=49
WHERE id_action=342;
UPDATE workflow_action
SET display_order=50
WHERE id_action=343;
UPDATE workflow_action
SET display_order=51
WHERE id_action=344;
UPDATE workflow_action
SET display_order=52
WHERE id_action=338;
UPDATE workflow_action
SET display_order=53
WHERE id_action=339;


-- pour recette et PR7
UPDATE workflow_action
SET display_order=21
WHERE id_action=349;

-- uniquement pour recette
UPDATE workflow_action
SET display_order=54
WHERE id_action=350;

-- Modification nom action workflow

-- MAJ categoriser en Ajouter une nomenclature
UPDATE workflow_action SET name='Ajouter une nomenclature', description='Ajout de la nomenclature' WHERE id_action=324;

