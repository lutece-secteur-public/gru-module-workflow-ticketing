-- Nouvelles actions pour l'évolution "relance automatique des usagers"
INSERT INTO workflow_action (id_action, name, description, id_workflow, id_state_before, id_state_after, id_icon, is_automatic, is_mass_action, display_order, is_automatic_reflexive_action) VALUES
(365, 'Relance automatique usager', 'Relance automatique usager', 301, 304, 304, 1, 0, 0, 61, 0),
--  dernière action manuelle = "Demande compléments"
(366, 'Retour après relance usager', 'Retour de la sollicitation après relances automatiques sans réponse de l''usager', 301, 304, 306, 1, 0, 0, 62, 0);

 -- Parametrage des 2 boutons           
INSERT INTO ticketing_param_bouton_action
(id_action, id_couleur, ordre, icone, id_groupe)
VALUES(365, 'Noir', 32, 'far fa-question-circle', 3),
(366, 'Noir', 33, 'far fa-question-circle', 3);

-- paramétrage de la tache de relance automatique usager
INSERT INTO workflow_task (id_task, task_type_key, id_action, display_order) VALUES
(631, 'taskNotifyWaitingTicket', 365, 1),
(632, 'taskNotifyGru',365, 2);

-- parametrage de la tache de retour automatique usager
INSERT INTO workflow_task (id_task, task_type_key, id_action, display_order) VALUES
(633, 'taskNotifyWaitingTicket', 366, 1),
(634, 'taskNotifyGru', 366, 2),
(635, 'taskTicketingMarkAsUnread',366, 3);

INSERT INTO workflow_task_ticketing_email_external_user_config (id_task, message_direction, id_following_action,
                                                                     id_contact_attribute, default_subject)
                                                                     VALUES
(631, 1, null, null, '');

INSERT INTO workflow_task_ticketing_email_external_user_config (id_task, message_direction, id_following_action,
                                                                     id_contact_attribute, default_subject)
                                                                     VALUES
(633, 1, null, null, '');

INSERT INTO workflow_task_notify_gru_cf
(id_task, id_spring_provider, marker_provider_ids, demand_status, crm_status_id, set_onglet, message_guichet, status_text_guichet, sender_name_guichet, subject_guichet, demand_max_step_guichet, demand_user_current_step_guichet, is_active_onglet_guichet, status_text_agent, message_agent, is_active_onglet_agent, subject_email, message_email, sender_name_email, recipients_cc_email, recipients_cci_email, is_active_onglet_email, message_sms, billing_group_sms, billing_account_sms, is_active_onglet_sms, id_mailing_list_broadcast, email_broadcast, sender_name_broadcast, subject_broadcast, message_broadcast, recipients_cc_broadcast, recipients_cci_broadcast, is_active_onglet_broadcast)
VALUES(632, 'notifygru-ticketing.provider-manager.@.ticket', '', 0, 1, 3, '<p>Bonjour ${firstname!} ${lastname!},</p>
<p>Suite &agrave; votre demande num&eacute;ro ${reference!}, nous avons besoin d''informations compl&eacute;mentaires.</p>
<p>Pour les fournir, <a href="${url_completed!}">cliquez ici</a>.</p>
<p>Cordialement,</p>
<p>Ville de Paris</p>', 'En attente de compléments', 'Ville de Paris', '${ticket_type!} Numéro ${reference!} - Relance Demande d''informations complémentaires', 4, 2, 1, 'En attente de compléments par l''usager', '<p>Une demande de compl&eacute;ments a &eacute;t&eacute; faite &agrave; l''usager.</p>', 1, '${ticket_type!} Numéro ${reference!} - Relance Demande d''informations complémentaires', '<p>Bonjour ${firstname!} ${lastname!},</p>
<p>Suite &agrave; votre demande num&eacute;ro ${reference!}, nous avons besoin d''informations compl&eacute;mentaires.</p>
<p>Pour les fournir, <a href="${url_completed!}">cliquez ici</a>.</p>
<p>Cordialement,</p>
<p>Ville de Paris</p>
<p><em>Ceci est un message automatique, veuillez ne pas y r&eacute;pondre directement.</em></p>', 'Ville de Paris', '', '', 1, 'Bonjour ${firstname} ${lastname}, votre demande ${reference} est en attente d''informations complémentaires de votre part. Cordialement. Ville de Paris', NULL, NULL, 0, 1, '', 'Ville de Paris', 'Relance - Demande d''information complémentaires, demande ${reference}', '<p>Bonjour ${firstname} ${lastname},</p>
<p>Votre demande ${reference} est en attente d''informations compl&eacute;mentaires de votre part. <a href="${url_completed}">Cliquez ici</a> pour acc&eacute;der &agrave; votre demande.</p>
<p>Cordialement,</p>
<p>Ville de Paris</p>', '', '', 0);

INSERT INTO workflow_task_notify_gru_cf
(id_task, id_spring_provider, marker_provider_ids, demand_status, crm_status_id, set_onglet, message_guichet, status_text_guichet, sender_name_guichet, subject_guichet, demand_max_step_guichet, demand_user_current_step_guichet, is_active_onglet_guichet, status_text_agent, message_agent, is_active_onglet_agent, subject_email, message_email, sender_name_email, recipients_cc_email, recipients_cci_email, is_active_onglet_email, message_sms, billing_group_sms, billing_account_sms, is_active_onglet_sms, id_mailing_list_broadcast, email_broadcast, sender_name_broadcast, subject_broadcast, message_broadcast, recipients_cc_broadcast, recipients_cci_broadcast, is_active_onglet_broadcast)
VALUES(634, 'notifygru-ticketing.provider-manager.@.ticket', '', 0, 1, 3, '<p>Bonjour ${firstname!} ${lastname!},</p>
<p>La demande num&eacute;ro ${reference!} est cl&ocirc;tur&eacute;e car nous n''avons reçu les informations compl&eacute;mentaires n&eacute;cessaires.</p>
<p>Cordialement,</p>
<p>Ville de Paris</p>
<p><em>Ceci est un message automatique, veuillez ne pas y r&eacute;pondre directement.</em></p>', 'Traité', 'Ville de Paris', '${ticket_type!} Numéro ${reference!} - Retour sans réponse à la demande d''informations complémentaires', 4, 2, 1, 'Traité', '<p>Une information de cl&ocirc;ture de la demande a &eacute;t&eacute; faite &agrave; l''usager.</p>', 1, '${ticket_type!} Numéro ${reference!} - Fin de Demande d''informations complémentaires', '<p>Bonjour ${firstname!} ${lastname!},</p>
<p>La demande num&eacute;ro ${reference!} est cl&ocirc;tur&eacute;e car nous n''avons reçu les informations compl&eacute;mentaires n&eacute;cessaires.</p>
<p>Cordialement,</p>
<p>Ville de Paris</p>
<p><em>Ceci est un message automatique, veuillez ne pas y r&eacute;pondre directement.</em></p>', 'Ville de Paris', '', '', 1, 'Bonjour ${firstname} ${lastname}, votre demande ${reference} est  cl&ocirc;tur&eacute;e. Cordialement. Ville de Paris', NULL, NULL, 0, 1, '', 'Ville de Paris', 'Relance - Demande d''information complémentaires, demande ${reference}', '<p>Bonjour ${firstname} ${lastname},</p>
<p>Votre demande ${reference} a &eacute;t&eacute; cl&ocirc;tur&eacute;e par manque de r&eacute;ponses &agrave; la demande d''informations compl&eacute;mentaires.</p>
<p>Cordialement,</p>
<p>Ville de Paris</p>', '', '', 0);

