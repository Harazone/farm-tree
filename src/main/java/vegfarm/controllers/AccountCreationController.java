/**
 * 
 */
package vegfarm.controllers;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import vegfarm.dao.CustomerRepository;
import vegfarm.dao.RoleRepository;
import vegfarm.domain.Customer;
import vegfarm.domain.Role;
import vegfarm.domain.RoleEnum;

/**
 * @author hara
 * Created on Jul 26, 2018
 */
@Controller
@RequestMapping("/open")
public class AccountCreationController {

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@RequestMapping(value="/create")
	public String createAccount(@RequestBody Customer customer, HttpServletRequest request){
		HashMap<String,Object> result =new HashMap<String,Object>();
	    result.put("success", false);
	    result.put("existingUser", false);
	   Customer customerFromDb= customerRepository.findByEmail(customer.getEmail());
	   if(customerFromDb!=null){
		   result.put("existingUser", true);
	   }else{
		   customer.setPassword(bCryptPasswordEncoder.encode(customer.getPassword()));
		   customer.setActive(true);
		   customer= customerRepository.save(customer);
		   Role userRole=new Role();
		   userRole.setRole(RoleEnum.ROLE_USER);
		   userRole.setCustomer(customer);
		   roleRepository.save(userRole);
		   result.put("success",true);
	   }
	   
		return "home";
	}
}
