package fr.ul.miage.chevrier.banque;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Classe des utilitaires utilisés par
 * les tests.
 */
public class GlobalTest {
    //Séparateur des parties d'un URL.
    protected final static String URL_PART_SEPARATOR = "/";
    //URI des comptes bancaires.
    protected final static String URI_ACCOUNTS = URL_PART_SEPARATOR + "accounts";
    //URI des cartes des comptes bancaires.
    protected final static String URI_CARDS = URL_PART_SEPARATOR + "cards";
    //URI des opérations sur les comptes bancaires.
    protected final static String URI_OPERATIONS = URL_PART_SEPARATOR + "operations";

    /**
     * Transformer un object JAVA en JSON stocké en chaine de caractères.
     *
     * @param object        Objet à transformer en JSON stocké en chaine de caractères.
     * @return String       JSON stocké en chaine de caractères.
     * @throws Exception
     */
    protected String toJSONString(Object object) {
        ObjectMapper map = new ObjectMapper();
        try {
            return map.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
