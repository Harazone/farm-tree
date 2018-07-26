/**
 * 
 */
package vegfarm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vegfarm.domain.Customer;
import vegfarm.domain.Role;

/**
 * @author hara
 *
 */

@Repository("role")
public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findByCustomer(Customer customer);
}