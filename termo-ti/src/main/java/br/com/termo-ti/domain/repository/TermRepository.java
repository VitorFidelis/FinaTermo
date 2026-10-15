package br.com.TermoTI.domain.repository;

import br.com.TermoTI.domain.model.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TermRepository extends JpaRepository<Term, UUID> {

}
