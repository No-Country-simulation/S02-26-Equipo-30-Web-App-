package com.nc.horseretail.model.user;


public enum UserStatus {

    ACTIVE, SUSPENDED, //User account is suspended by admin
    BLOCKED, // temporarily blocked (e.g. too many failed logins).
    DEACTIVATED, //User voluntarily deactivated their account.
    DELETED //Soft deleted account.
}

/**

 ACTIVE
 •	Puede autenticarse
 •	Puede publicar
 •	Puede enviar mensajes

 SUSPENDED
 •	Acción manual de ADMIN
 •	No puede autenticarse
 •	No puede operar
 •	Listings deberían ocultarse

 BLOCKED
 •	Bloqueo automático por seguridad
 •	Ej: intentos fallidos de login
 •	Puede desbloquearse

 DEACTIVATED
 •	Usuario elimina su cuenta voluntariamente
 •	Soft delete lógico reversible

 DELETED
 •	Soft delete administrativo definitivo
 •	No debe aparecer en búsquedas
 •	Solo visible en auditoría

 */