package ru.mifi.practice.voln.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

/** Настройка JDBC-аутентификации и SQL-запросов управления пользователями. */
@Configuration
public class GlobalConfiguration {
    /**
     * Учётка администратора заводится только когда пароль задан снаружи. Раньше здесь на каждом
     * старте создавался пользователь admin с паролем admin и ролью ADMIN — в том числе в проде.
     */
    @Value("${app.admin.password:}")
    private String adminPassword;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, DataSource dataSource, PasswordEncoder encoder)
        throws Exception {
        var managerConfigurer = auth.jdbcAuthentication();
        managerConfigurer.dataSource(dataSource)
            .usersByUsernameQuery("SELECT username, password, enabled FROM users WHERE username=?")
            .authoritiesByUsernameQuery("SELECT username, authority FROM authorities WHERE username=?");
        if (!adminPassword.isBlank()) {
            managerConfigurer.withUser(User.withUsername("admin").password(encoder.encode(adminPassword)).roles("ADMIN"));
        }
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
