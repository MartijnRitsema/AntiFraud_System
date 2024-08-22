package antifraud.suspiciousip;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SuspiciousIPRepository extends JpaRepository<SuspiciousIP, Long> {
    boolean existsByIp(String ip);
    SuspiciousIP findByIp(String ip);
    void deleteByIp(String ip);
}
