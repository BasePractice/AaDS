package ru.mifi.practice.voln.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

@Configuration
public class GlobalConfiguration {
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, DataSource dataSource, PasswordEncoder encoder) {
        var managerConfigurer = auth.jdbcAuthentication();
        managerConfigurer.dataSource(dataSource)
            .usersByUsernameQuery("SELECT username, password, enabled FROM users WHERE username=?")
            .authoritiesByUsernameQuery("SELECT username, authority FROM authorities WHERE username=?")
            .withUser(User.withUsername("admin").password(encoder.encode("admin")).roles("ADMIN"));
        JdbcUserDetailsManager detailsService = managerConfigurer.getUserDetailsService();
        detailsService.setCreateAuthoritySql("INSERT INTO authorities (id, user_id, authority) " +
            "VALUES (nextval('authority_id_seq'), (SELECT id FROM users WHERE username = ?),?)  " +
            "ON CONFLICT (user_id, authority) DO NOTHING");
        detailsService.setDeleteUserAuthoritiesSql("DELETE FROM authorities WHERE user_id = " +
            "(SELECT id FROM users WHERE username = ?)");
        detailsService.setCreateUserSql("INSERT INTO users (username, password, enabled) VALUES (?,?,?) " +
            "ON CONFLICT (username) DO NOTHING");
    }
}
