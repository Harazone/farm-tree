package vegfarm.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import vegfarm.dao.CustomerRepository;
import vegfarm.domain.Customer;

@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class AuthenticationSecurity extends SimpleUrlAuthenticationSuccessHandler {
	@Autowired
	CustomerRepository customerRepository; 
	
	@Override
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException,
	ServletException {
		if (authentication.getPrincipal() instanceof Customer) {
			final Customer customer = (Customer) authentication.getPrincipal();
			if(customer!=null && customer.getUserName()!=null){
				customerRepository.findByEmail(customer.getUserName());
			}
			
			response.sendRedirect("/admin/home");
		}
	}
}