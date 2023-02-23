package com.shoppingcart.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.hibernate.criterion.Restrictions.and;

@Configuration
@EnableWebSecurity
public class WebSecuriryConfig extends WebSecurityConfigurerAdapter { //extends WebSecurityConfigurerAdapter de cau hinh ma hoa

	@Bean //di chung với @Configuration
	public UserDetailsService userDetailsService() {
		return new ShoppingUserDetailsService();
	}

	@Bean //muc dich cua Bean là de autowired
	public PasswordEncoder passwordEncoder() {//ma hoa password
		return new BCryptPasswordEncoder();
	}

	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception { //cau hinh phan quyen
		http.authorizeRequests()
				//phan quyen -> /users/admin or /users/edit
				//"/users/**", "/categories/**", hasAnyAuthority("admin", "editor")
				.antMatchers("/users/**").hasAuthority("Admin")
				.antMatchers("/categories/**", "/brands/**", "/products/**").hasAnyAuthority("Admin", "Editor")

				.anyRequest().authenticated() //authenticated -> tat ca request deu phai nhap login, .anyRequest().permitAll() -> tat ca deu login
				.and()
				.formLogin()
				.loginPage("/login") //la GetMapping
				.usernameParameter("email") //thay username = email, bat email o name html, mac dinh la nhap username va pass, con cua minh la email va pass nen phai them .usernameParameter("email")
				.permitAll() //chap nhan de dang nhap vao
				.and().logout().permitAll()
				.and()
				.rememberMe()
				.key("ABC_123")
				.tokenValiditySeconds(7 * 24 * 60 * 60);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**"); //o trang login nhung van muon truy cap nhung src images, js -> de hien image or trang login
	}
}
