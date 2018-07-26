package vegfarm.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import vegfarm.dao.CustomerRepository;
import vegfarm.dao.RoleRepository;
import vegfarm.domain.Customer;
import vegfarm.domain.Role;
import vegfarm.domain.RoleEnum;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	private static final String usersQuery="select email,password,active from user where email=?";
	private static final String rolesQuery="select u.user_name, ur.role,u.email from user u, role ur where u.id = ur.user_id and u.email =?";
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private DataSource dataSource;

	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	CustomerRepository customerRepository; 
	
	@Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new ProcessAuthSuccess();
    }
	
	 @Autowired
     private SecurityProperties security;
     
     @Autowired
     private AuthenticationSuccessHandler authenticationSuccessHandler;

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.
		jdbcAuthentication()
		.usersByUsernameQuery(usersQuery)
		.authoritiesByUsernameQuery(rolesQuery)
		.dataSource(dataSource)
		.passwordEncoder(bCryptPasswordEncoder);
	}
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.
		authorizeRequests()
		.antMatchers("/","/static/**","/resources/**","/css/**").permitAll()
		.antMatchers("/login").permitAll()
		.antMatchers("/verify").permitAll()
		.antMatchers("/registration").permitAll()
		.antMatchers("/api/**").hasAnyAuthority("ROLE_SUPER","ROLE_ADMIN")
		.antMatchers("/open/**").permitAll()
		.antMatchers("/user/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN","ROLE_SUPER")
		.antMatchers("/admin/**").hasAnyAuthority("ROLE_SUPER","ROLE_ADMIN").anyRequest()
		
		.authenticated().and().csrf().disable().formLogin()
		.loginPage("/login").failureUrl("/login?error=true")
		.successHandler(authenticationSuccessHandler)

		.usernameParameter("email")
		.passwordParameter("password")
		.and().logout()
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		.logoutSuccessUrl("/login").and().exceptionHandling()
		.accessDeniedPage("/access-denied");
	}

	@Order(Ordered.HIGHEST_PRECEDENCE + 10)
	protected static class AuthenticationSecurity extends GlobalAuthenticationConfigurerAdapter {

		@Autowired
		private UserDetailsService userDetailsService;

		@Override
		public void init(AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(userDetailsService);
		}
	}

	protected  class ProcessAuthSuccess extends SimpleUrlAuthenticationSuccessHandler {

		/** Logger implementation. */
		private final Logger logger = LoggerFactory.getLogger(this.getClass());

		@Override
		public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException,
		ServletException {
	        String redirectUrl="/home";
			if (authentication.getPrincipal() instanceof Customer) {
				final Customer customer = (Customer) authentication.getPrincipal();
				if(customer!=null && customer.getUserName()!=null){
					Customer customerFromDb=customerRepository.findByEmail(customer.getUserName());
					if(customerFromDb !=null){
						Role role=roleRepository.findByCustomer(customerFromDb);
						if((role.getRole()==RoleEnum.ROLE_ADMIN)||(role.getRole()==RoleEnum.ROLE_SUPER)){
							redirectUrl="/admin/home";
						}else{
							redirectUrl="/user/home";
						}
					}
				}
				response.sendRedirect(redirectUrl);
			}
		}
	}
}