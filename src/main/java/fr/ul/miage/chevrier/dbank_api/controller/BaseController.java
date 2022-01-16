package fr.ul.miage.chevrier.dbank_api.controller;

import fr.ul.miage.chevrier.dbank_api.security.Role;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;

/**
 * Contrôleur de base parent des autres
 * contrôleurs. Il fournit des utilitaires
 * pour les contrôleurs.
 */
@NoArgsConstructor
public class BaseController {
    /**
     * Obtenir l'utilisateur connecté.
     *
     * @return Authentication       Utilisateur connecté.
     */
    protected Authentication getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Obtenir le rôle unique de l'utilisateur
     * connecté.
     *
     * @return Role       Rôle de l'utilisateur connecté.
     */
    protected Role getCurrentUserRole() {
       return getCurrentUser().getAuthorities()
                              .stream()
                              .map(authority -> Role.from(authority.getAuthority().replace("ROLE_", "")))
                              .toList()
                              .get(0);
    }

    /**
     * Savoir si l'utilisateur connecté a un rôle.
     *
     * @return       L'utilisateur connecté a le rôle ?
     */
    protected Boolean currentUserIs(Role role) {
        return getCurrentUserRole().equals(role);
    }

    /**
     * Obtenir le filtre sur le secret du compte
     * en fonction du rôle connecté.
     *
     * @return String       Filtre sur le secret.
     */
    protected String getFilterSecretByCurrentUserRole() {
        String secret;
        if(currentUserIs(Role.CLIENT)) {
            secret = getCurrentUser().getName();
        } else {
            secret = "";
        }
        return secret;
    }
}