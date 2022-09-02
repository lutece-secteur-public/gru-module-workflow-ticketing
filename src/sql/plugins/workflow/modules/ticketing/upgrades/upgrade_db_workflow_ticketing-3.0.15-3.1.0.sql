-- Signalement et Vous simplifier Paris

-- Ajout de 2 boutons au workflow
INSERT INTO  workflow_action (name, description, id_workflow, id_state_before, id_state_after, id_icon, is_automatic, is_mass_action, display_order, is_automatic_reflexive_action)
VALUES       ('Signalé', 'Signalement de la sollicitation', 301, 301, 301, 1, 0, 0, 47, 0),
             ('Vous simplifier Paris', 'Vous simplifier Paris', 301, 301, 301, 1, 0, 0, 48, 0);

-- Parametrage des 2 boutons           
INSERT INTO ticketing_param_bouton_action
(id_action, id_couleur, ordre, icone, id_groupe)
VALUES(352, 'Noir', 20, 'fa fa-question-circle-o', 2),
(353, 'Noir', 21, 'fa fa-question-circle-o', 2),
(354, 'Noir', 22, 'fa fa-question-circle-o', 2),
(355, 'Noir', 23, 'fa fa-question-circle-o', 2),
(356, 'Noir', 24, 'fa fa-question-circle-o', 2),
(357, 'Noir', 25, 'fa fa-question-circle-o', 2);

-- Ajouter de 2 taches
INSERT INTO workflow_task
(task_type_key, id_action, display_order)
VALUES('taskSignalement', 352, 1),
('taskSignalement', 353, 1),
('taskSignalement', 354, 1),
('taskVousSimplifierParis', 355, 1),
('taskVousSimplifierParis', 356, 1),
('taskVousSimplifierParis', 357, 1);