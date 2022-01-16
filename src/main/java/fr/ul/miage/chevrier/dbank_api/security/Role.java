package fr.ul.miage.chevrier.dbank_api.security;

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

    //Libellé d'un rôle.
    private final String label;

    /**
     * Obtenir un rôle à partir de son libellé.
     *
     * @param label     Libellé du rôle.
     * @return          Rôle.
     */
    public static Role from(String label) {
        if(label.equals(Role.ADMIN.getLabel())) {
            return Role.ADMIN;
        } else  {
            if(label.equals(Role.CLIENT.getLabel())) {
                return Role.CLIENT;
            } else {
                if(label.equals(Role.ATM.getLabel())) {
                    return Role.ATM;
                } else {
                    if(label.equals(Role.MERCHANT.getLabel())) {
                        return Role.MERCHANT;
                    } else {
                        return null;
                    }
                }
            }
        }
    }
}
