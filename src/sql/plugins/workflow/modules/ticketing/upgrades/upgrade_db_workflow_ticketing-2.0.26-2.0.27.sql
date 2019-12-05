-- attributs pour l'évolution "relance automatique"
ALTER TABLE ticketing_ticket ADD nb_relance int(2) default 0;
ALTER TABLE ticketing_ticket ADD date_derniere_relance timestamp;

-- configuration pour l'évolution "relance automatique"
INSERT INTO `core_datastore` (`entity_key`, `entity_value`) VALUES
('ticketing.configuration.relance_auto.nb_relance_max', '3'),
('ticketing.configuration.relance_auto.frequence_relance', '10')
;

-- Nouvelles actions pour l'évolution "relance automatique"
INSERT INTO `workflow_action` (`id_action`, `name`, `description`, `id_workflow`, `id_state_before`, `id_state_after`, `id_icon`, `is_automatic`, `is_mass_action`, `display_order`, `is_automatic_reflexive_action`) VALUES
(340, 'Relance automatique', 'Relance automatique', 301, 307, 307, 1, 0, 0, 38, 0),
--  dernière action manuelle = "Solliciter un acteur terrain" [à partir d\'un niveau 2]
(341, 'Retour de la sollicitation', 'Retour de la sollicitation après relances automatiques infructueuses', 301, 307, 303, 1, 0, 0, 39, 0),
-- dernière action manuelle = "Solliciter un acteur terrain" [à partir d\'un niveau 3]
(342, 'Retour de la sollicitation', 'Retour de la sollicitation après relances automatiques infructueuses', 301, 307, 305, 1, 0, 0, 40, 0),
-- dernière action manuelle = "Solliciter un contributeur" [à partir d\'un niveau 2]
(343, 'Retour de la sollicitation', 'Retour de la sollicitation après relances automatiques infructueuses', 301, 307, 303, 1, 0, 0, 41, 0),
-- dernière action manuelle = "Solliciter un contributeur" [à partir d\'un niveau 3]
(344, 'Retour de la sollicitation', 'Retour de la sollicitation après relances automatiques infructueuses', 301, 307, 305, 1, 0, 0, 42, 0);

INSERT INTO `workflow_task` (`id_task`, `task_type_key`, `id_action`, `display_order`) VALUES
(569, 'taskNotifyWaitingTicket', 340, 1);


-- paramétrage de la tâche de relance automatique
create table workflow_task_ticketing_notify_waiting_ticket_config
(
    id_task int default 0 not null primary key ,
    sender_name varchar(255) null,
    subject varchar(255) null,
    message text null
);
INSERT INTO `workflow_task_ticketing_notify_waiting_ticket_config` (`id_task`, `sender_name`, `subject`, `message`) VALUES
(569, 'Ville de Paris', '[GRU] Relance n°${nb_automatic_notification!} - Le service ${unit_name!} requiert une action de votre part sur la sollicitation ${reference!}', '<p>Relance n&deg;${nb_automatic_notification!} :</p><br><p>Ceci est une relance qui vous est adress&eacute;e par le service ${unit_name!} concernant la demande li&eacute;e &agrave; la sollicitation ${reference!}. <a href="${ticketing_ticket_link!}">Cliquez ici</a> pour y r&eacute;pondre (attention : apr&egrave;s validation, vous ne pourrez plus modifier votre r&eacute;ponse ni ajouter de compl&eacute;ment d\'information).</p>');




