package fr.ul.miage.chevrier.dbank_api.security;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Configuration de Keycloak.
 */
public class Configuration {
    @KeycloakConfiguration
    @ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true", matchIfMissing = true)
    public static class KeycloakConfigurationAdapter extends KeycloakWebSecurityConfigurerAdapter {
        /**
         * Définition de la stratégie d'authentification,
         * qui est ici STATELESS (pas de session).
         */
        @Bean
        @Override
        protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
            return new NullAuthenticatedSessionStrategy();
        }

        /**
         * Définition de la stratégie de nommage des rôles,
         * qui est ici pas de préfixe "ROLE_" par défaut
         * devant les noms des rôles.
         */
        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
            keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
            auth.authenticationProvider(keycloakAuthenticationProvider);
        }

        /**
         * Définition du résolveur de la configuration pour le bogue
         * référencé héritée de la version 7 de la dépendance.
         *
         * @return KeycloakConfigResolver       Résolveur de la configuration.
         */
        @Bean
        public KeycloakConfigResolver KeycloakConfigResolver() {
            return new KeycloakSpringBootConfigResolver();
        }

        /**
         * Définition de la configuration de Keycloak.
         *
         * @param httpSecurity      Gestionnaire de la sécurité.
         * @throws Exception
         */
        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception
        {
            httpSecurity
                    //Désactivation CSRF.
                    .csrf().disable()
                    .sessionManagement()

                    //Stratégie d'authentification.
                    .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                    //Filtres de Keycloak pour l'obligation de l'authentification des utilisateurs, etc.
                    .and()
                    .addFilterBefore(keycloakPreAuthActionsFilter(), LogoutFilter.class)
                    .addFilterBefore(keycloakAuthenticationProcessingFilter(), X509AuthenticationFilter.class)
                    .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())

                    //URI pour la déconnexion.
                    .and()
                    .logout()
                    .addLogoutHandler(keycloakLogoutHandler())
                    .logoutUrl("/logout").logoutSuccessHandler(
                    (HttpServletRequest request, HttpServletResponse response, Authentication authentication) ->
                    response.setStatus(HttpServletResponse.SC_OK))

                    //Restrictions des accès aux routes par rôle.
                    .and()
                    .authorizeRequests()

                    //Il faut juste être authentifié pour accéder à la route d'accueil,
                    //ou pour utiliser la méthode HTTP OPTIONS.
                    .antMatchers(HttpMethod.OPTIONS).authenticated()
                    .antMatchers("/").authenticated()

                    //Droits des admins sur les consoles des bases de données de dev et de test.
                    .antMatchers("/dev_database/console/**").anonymous()//TODO
                    .antMatchers("/test_database/console/**").hasRole(Role.ADMIN.getLabel())


                    //Droits des admins, clients, ATMs, et marchands sur les comptes.
                    .antMatchers(HttpMethod.GET, "/accounts").hasAnyRole(Role.ADMIN.getLabel(), Role.CLIENT.getLabel(), Role.ATM.getLabel())
                    .antMatchers(HttpMethod.GET, "/accounts/*").hasAnyRole(Role.ADMIN.getLabel(), Role.CLIENT.getLabel(), Role.ATM.getLabel())
                    .antMatchers(HttpMethod.POST, "/accounts").hasAnyRole(Role.ADMIN.getLabel(), Role.CLIENT.getLabel())
                    .antMatchers(HttpMethod.PUT, "/accounts/*").hasAnyRole(Role.ADMIN.getLabel(), Role.CLIENT.getLabel())
                    .antMatchers(HttpMethod.PATCH, "/accounts/*").hasAnyRole(Role.ADMIN.getLabel(), Role.CLIENT.getLabel())

                    //Droits des admins, clients, ATMs, et marchands sur les cartes.
                    .antMatchers(HttpMethod.GET, "/cards").hasAnyRole(Role.ADMIN.getLabel(), Role.CLIENT.getLabel())
                    .antMatchers(HttpMethod.GET, "/cards/{cardId}").hasAnyRole(Role.ADMIN.getLabel(), Role.CLIENT.getLabel())
                    .antMatchers(HttpMethod.POST, "/cards").hasAnyRole(Role.ADMIN.getLabel(), Role.CLIENT.getLabel())
                    .antMatchers(HttpMethod.POST, "/cards/*/code/check").hasAnyRole(Role.ADMIN.getLabel(), Role.ATM.getLabel())
                    .antMatchers(HttpMethod.POST, "/cards/identity/check").hasAnyRole(Role.ADMIN.getLabel(), Role.MERCHANT.getLabel())
                    .antMatchers(HttpMethod.POST, "/cards/*/expire").hasAnyRole(Role.ADMIN.getLabel())
                    .antMatchers(HttpMethod.PUT, "/cards/*").hasAnyRole(Role.ADMIN.getLabel(), Role.CLIENT.getLabel())
                    .antMatchers(HttpMethod.PATCH, "/cards/*").hasAnyRole(Role.ADMIN.getLabel(), Role.CLIENT.getLabel())

                    //Droits des admins, clients, ATMs, et marchands sur les opérations.
                    .antMatchers(HttpMethod.GET, "/operations").hasAnyRole(Role.ADMIN.getLabel(), Role.CLIENT.getLabel(), Role.ATM.getLabel())
                    .antMatchers(HttpMethod.GET, "/operations/*").hasAnyRole(Role.ADMIN.getLabel(), Role.CLIENT.getLabel(), Role.ATM.getLabel(), Role.MERCHANT.getLabel())
                    .antMatchers(HttpMethod.POST, "/operations").hasAnyRole(Role.ADMIN.getLabel(), Role.CLIENT.getLabel(), Role.ATM.getLabel())
                    .antMatchers(HttpMethod.POST, "/operations/*/confirm").hasAnyRole(Role.ADMIN.getLabel())
                    .antMatchers(HttpMethod.PUT, "/operations/*").hasAnyRole(Role.ADMIN.getLabel())
                    .antMatchers(HttpMethod.PATCH, "/operations/*").hasAnyRole(Role.ADMIN.getLabel())
                    .antMatchers(HttpMethod.DELETE, "/operations/*").hasAnyRole(Role.ADMIN.getLabel(), Role.MERCHANT.getLabel())

                    .anyRequest().denyAll();
        }
    }
}