-- Signalement et Vous simplifier Paris

-- Parametrage des 2 boutons           
INSERT INTO ticketing_param_bouton_action
(id_action, id_couleur, ordre, icone, id_groupe)
VALUES(352, 'Noir', 20, 'fa fa-question-circle-o', 2),
(353, 'Noir', 21, 'fa fa-question-circle-o', 2),
(354, 'Noir', 22, 'fa fa-question-circle-o', 2),
(355, 'Noir', 23, 'fa fa-question-circle-o', 2),
(356, 'Noir', 24, 'fa fa-question-circle-o', 2),
(357, 'Noir', 25, 'fa fa-question-circle-o', 2);

-- Ajout de 2 actions - boutons
INSERT INTO workflow_action
(id_action, name, description, id_workflow, id_state_before, id_state_after, id_icon, is_automatic, is_mass_action, display_order, is_automatic_reflexive_action)
VALUES(352,'Signalé', 'Signalement de la sollicitation', 301, 303, 303, 1, 0, 0, 47, 0),
(353, 'Signalé', 'Signalement de la sollicitation', 301, 305, 305, 1, 0, 0, 47, 0),
(354,'Signalé', 'Signalement de la sollicitation', 301, 306, 306, 1, 0, 0, 47, 0),
(355,'Vous simplifier Paris', 'Vous simplifier Paris', 301, 303, 303, 1, 0, 0, 48, 0),
(356,'Vous simplifier Paris', 'Vous simplifier Paris', 301, 305, 305, 1, 0, 0, 48, 0),
(357,'Vous simplifier Paris', 'Vous simplifier Paris', 301, 306, 306, 1, 0, 0, 48, 0);

-- Ajout de 2 taches
INSERT INTO workflow_task
(id_task, task_type_key, id_action, display_order)
VALUES(608,'taskSignalementTicket', 352, 1),
(609,'taskTicketingComment', 352, 2),
(610,'taskSignalementTicket', 353, 1),
(611, 'taskTicketingComment', 353, 2),
(612,'taskSignalementTicket', 354, 1),
(613,'taskTicketingComment', 354, 2),
(614,'taskVousSimplifierParis', 355, 1),
(615,'taskTicketingComment', 355, 2),
(616, 'taskVousSimplifierParis', 356, 1),
(617, 'taskTicketingComment', 356, 2),
(618,'taskVousSimplifierParis', 357, 1),
(619, 'taskTicketingComment', 357, 2);

-- Configuration des commentaires
INSERT INTO workflow_task_comment_config
(id_task, title, is_mandatory, is_richtext)
VALUES(609, 'Commentaire', 0, 1),
(611, 'Commentaire', 0, 1),
(613, 'Commentaire', 0, 1),
(615, 'Commentaire', 0, 1),
(617, 'Commentaire', 0, 1),
(619, 'Commentaire', 0, 1);