/**
 * 
 */
package vegfarm.bootstrap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import vegfarm.dao.CustomerRepository;
import vegfarm.dao.RoleRepository;
import vegfarm.domain.Customer;
import vegfarm.domain.Role;
import vegfarm.domain.RoleEnum;

/**
 * @author hara
 * Created on Jul 26, 2018 2018
 */
@Component
public class Bootstrap implements InitializingBean{

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Override
	public void afterPropertiesSet(){
		Customer customer=new Customer();
		Customer customerFromDb= customerRepository.findByEmail("vegfarm@vegfarm.com");
		if(customerFromDb==null){
			customer = createSuperAdmin(customer);
			assignRoleToSuperAdmin(customer);
		}
		
	}

	/**
	 * @param customer
	 */
	private void assignRoleToSuperAdmin(Customer customer) {
		Role role=new Role();
		role.setCustomer(customer);
		role.setRole(RoleEnum.ROLE_SUPER);
		roleRepository.save(role);
	}

	/**
	 * @param customer
	 * @return
	 */
	private Customer createSuperAdmin(Customer customer) {
		customer.setEmail("vegfarm@vegfarm.com");
		customer.setPassword(bCryptPasswordEncoder.encode("vegfarm"));
		customer.setUserName("vegfarm");
		customer=customerRepository.save(customer);
		return customer;
	}
}
