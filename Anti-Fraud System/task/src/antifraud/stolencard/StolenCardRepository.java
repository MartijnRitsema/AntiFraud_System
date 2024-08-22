package antifraud.stolencard;

import org.springframework.data.repository.CrudRepository;

public interface StolenCardRepository extends CrudRepository<StolenCard, Long> {
    boolean existsByNumber(String number);
    StolenCard findByNumber(String number);
}
