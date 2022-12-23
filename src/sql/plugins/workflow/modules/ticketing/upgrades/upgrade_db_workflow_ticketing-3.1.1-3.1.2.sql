-- Possiblite de supprimer Signalement et Vous simplifier Paris

-- Vider emplacement pour intercaler boutons
UPDATE workflow_action SET display_order = display_order + 1 WHERE display_order > 5; 
UPDATE workflow_action SET display_order = display_order + 1 WHERE display_order > 7; 
UPDATE workflow_action SET display_order = display_order + 1 WHERE display_order > 30; 
UPDATE workflow_action SET display_order = display_order + 1 WHERE display_order > 32; 
UPDATE workflow_action SET display_order = display_order + 1 WHERE display_order > 42;
UPDATE workflow_action SET display_order = display_order + 1 WHERE display_order > 44;

-- Ajout de 2 actions - boutons
INSERT INTO workflow_action
(id_action, name, description, id_workflow, id_state_before, id_state_after, id_icon, is_automatic, is_mass_action, display_order, is_automatic_reflexive_action)
VALUES(358,'Supprimer Signalement', 'Suppression du signalement', 301, 303, 303, 1, 0, 0, 6, 0),
(359, 'Supprimer Signalement', 'Suppression du signalement', 301, 305, 305, 1, 0, 0, 31, 0),
(360,'Supprimer Signalement', 'Suppression du signalement', 301, 306, 306, 1, 0, 0, 43, 0),
(361,'Supprimer Vous simplifier Paris', 'Suppression de l''action Vous simplifier Paris', 301, 303, 303, 1, 0, 0, 8, 0),
(362,'Supprimer Vous simplifier Paris', 'Suppression de l''action Vous simplifier Paris', 301, 305, 305, 1, 0, 0, 32, 0),
(363,'Supprimer Vous simplifier Paris', 'Suppression de l''action Vous simplifier Paris', 301, 306, 306, 1, 0, 0, 45, 0);

-- Ajout couleur rouge clair
INSERT INTO ticketing_couleur_bouton (id_couleur, couleur) VALUES('Rouge clair', '#DE2916');


-- Parametrage des 2 boutons           
INSERT INTO ticketing_param_bouton_action
(id_action, id_couleur, ordre, icone, id_groupe)
VALUES(358, 'Rouge clair', 26, 'far fa-question-circle', 2),
(359, 'Rouge clair', 27, 'far fa-question-circle', 2),
(360, 'Rouge clair', 28, 'far fa-question-circle', 2),
(361, 'Rouge clair', 29, 'far fa-question-circle', 2),
(362, 'Rouge clair', 30, 'far fa-question-circle', 2),
(363, 'Rouge clair', 31, 'far fa-question-circle', 2);

-- Ajout de 2 taches
INSERT INTO workflow_task
(id_task, task_type_key, id_action, display_order)
VALUES(620,'taskRemoveSignalementTicket', 358, 1),
(621,'taskRemoveSignalementTicket', 359, 1),
(622,'taskRemoveSignalementTicket', 360, 1),
(624, 'taskRemoveVousSimplifierParis', 361, 1),
(625,'taskRemoveVousSimplifierParis', 362, 1),
(626,'taskRemoveVousSimplifierParis', 363, 1),
(627, 'taskAutomaticIdAdminBOInit', 301, 10);

-- configuration pour la relance automatique de usagers
INSERT INTO core_datastore (entity_key, entity_value) VALUES
('ticketing.configuration.relance_auto_usager.nb_relance_max', '3'),
('ticketing.configuration.relance_auto_usager.frequence_relance', '7');
