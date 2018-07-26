/**
 * 
 */
package vegfarm.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author hara
 *
 */
@Controller
public class HomeController {

	@RequestMapping("/login")

	public String login() {

		return "login";
	}

	@RequestMapping("/admin/home")

	public String adminHome() {

		return "adminHome";
	}
	@RequestMapping("/user/home")

	public String userHome() {

		return "userHome";
	}
	
	@RequestMapping("/verify")
	public String verifyEmail(){
		
		return "verify-email";
	}
}

