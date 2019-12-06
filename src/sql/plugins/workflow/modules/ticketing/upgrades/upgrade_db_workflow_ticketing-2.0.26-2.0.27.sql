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


-- paramétrage de la tâche de relance automatique
INSERT INTO `workflow_task` (`id_task`, `task_type_key`, `id_action`, `display_order`) VALUES
(569, 'taskNotifyWaitingTicket', 340, 1),
(570, 'taskNotifyGru',340, 2);

INSERT INTO test.workflow_task_notify_gru_cf (id_task, id_spring_provider, marker_provider_ids, demand_status, crm_status_id, set_onglet, message_guichet, status_text_guichet,
                                              sender_name_guichet, subject_guichet, demand_max_step_guichet, demand_user_current_step_guichet, is_active_onglet_guichet, status_text_agent,
                                              message_agent, is_active_onglet_agent, subject_email, message_email, sender_name_email, recipients_cc_email, recipients_cci_email,
                                              is_active_onglet_email, message_sms, billing_group_sms, billing_account_sms, is_active_onglet_sms, id_mailing_list_broadcast,
                                              email_broadcast, sender_name_broadcast, subject_broadcast, message_broadcast, recipients_cc_broadcast, recipients_cci_broadcast,
                                              is_active_onglet_broadcast)
                                              VALUES
(570, 'workflow-ticketing.externaluser.provider-manager.@.ticket', '', 0, 0, 4,
 null, null, null, null, 0, 0, 0,
 null, null, 0, null, null, null, null, null,
 0, null, null, null, 0, -1, '${email_recipients!}',
 'Mairie de Paris', '[GRU] Relance n°${nb_automatic_notification!} - Le service ${unit_name!} requiert une action de votre part sur la sollicitation ${reference!}',
 '<p>Relance n&deg;${nb_automatic_notification!}:</p><p>Ceci est une relance qui vous est adress&eacute;e par le service ${unit_name!} concernant la demande li&eacute;e &agrave; la sollicitation ${reference!}. <a href="${ticketing_ticket_link!}">Cliquez ici</a> pour y r&eacute;pondre (attention : apr&egrave;s validation, vous ne pourrez plus modifier votre r&eacute;ponse ni ajouter de compl&eacute;ment d''information).</p>',
 '', '', 1);


