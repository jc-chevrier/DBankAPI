package fr.ul.miage.chevrier.banque.security;

import lombok.RequiredArgsConstructor;
import lombok.Getter;

/**
 * Enumeration des rôles sur l'API,
 * que peuvent avoir les utilisateurs.
 */
@RequiredArgsConstructor
@Getter
public enum Role {
    ADMIN ("Admin"),
    CLIENT ("Client"),
    ATM ("ATM"),
    MERCHANT ("Merchant");

    private final String label;
}
