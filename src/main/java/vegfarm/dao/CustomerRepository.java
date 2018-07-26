package vegfarm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vegfarm.domain.Customer;

@Repository("Customer")
public interface CustomerRepository extends JpaRepository<Customer, Long>{
	 Customer findByEmail(String email);
}
