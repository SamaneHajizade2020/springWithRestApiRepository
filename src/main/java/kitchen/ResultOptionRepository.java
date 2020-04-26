package kitchen;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultOptionRepository extends JpaRepository<ResultOptimise, Long> {
}
