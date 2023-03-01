-- Nouvelles actions pour réassigner des sollicitations apres suppression ou desactivation de la personne assignee à celles-ci selon statut
INSERT INTO workflow_action
(id_action, name, description, id_workflow, id_state_before, id_state_after, id_icon, is_automatic, is_mass_action, display_order, is_automatic_reflexive_action)
VALUES(367, 'Réassignation entité (utilisateur supprimé ou inactif)', 'Réassignation entité après suppression ou désactivation de l''utilisateur', 301, 303, 303, 1, 0, 0, 63, 0),
(368, 'Réassignation entité (utilisateur supprimé ou inactif)', 'Réassignation entité après suppression ou désactivation de l''utilisateur', 301, 304, 304, 1, 0, 0, 64, 0),
(369, 'Réassignation entité (utilisateur supprimé ou inactif)', 'Réassignation entité après suppression ou désactivation de l''utilisateur', 301, 305, 305, 1, 0, 0, 65, 0),
(370, 'Réassignation entité (utilisateur supprimé ou inactif)', 'Réassignation entité après suppression ou désactivation de l''utilisateur', 301, 307, 307, 1, 0, 0, 66, 0);

-- tache reassignation tickets
INSERT INTO workflow_task
(id_task, task_type_key, id_action, display_order)
VALUES(643, 'taskReassignTicketToUnitForAUserNotAvailable', 367, 1),
(644, 'taskTicketingMarkAsUnread', 367, 2),
(645, 'taskReassignTicketToUnitForAUserNotAvailable', 368, 1),
(646, 'taskTicketingMarkAsUnread', 368, 2),
(647, 'taskReassignTicketToUnitForAUserNotAvailable', 369, 1),
(648, 'taskTicketingMarkAsUnread', 369, 2),
(649, 'taskReassignTicketToUnitForAUserNotAvailable', 370, 1),
(650, 'taskTicketingMarkAsUnread', 370, 2);

-- mettre marquage non lu apres reassignation
INSERT INTO workflow_task_ticketing_mark_unread_config
(id_task, id_marking)
VALUES(644, 1),
(646, 1),
(648, 1),
(650, 1);

