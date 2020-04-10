package per.itachi.test.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 貌似不写EnableWebSecurity也行；
 * 
 * https://www.jianshu.com/p/24c6a65c3913
 * */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		super.configure(auth);
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		//将用户名密码等信息存于内存，可用于测试；
		auth.inMemoryAuthentication().passwordEncoder(passwordEncoder)
				.withUser("springboot").password(passwordEncoder.encode("springboot")).roles("USER");
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		super.configure(web);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
//		super.configure(http);
		http.authorizeRequests()
				.antMatchers("/product/**").hasAnyRole("USER")
				.antMatchers("/admin/**").hasAnyRole("ADMIN")
				.anyRequest().authenticated()
			.and()
				.formLogin()
			.and()
				.httpBasic();
//		http.formLogin()
//				.and()
//				.authorizeRequests()
//				.anyRequest()
//				.authenticated();
	}

}
