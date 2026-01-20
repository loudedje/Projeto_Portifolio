package com.portfolio.manager.projeto.portifolio.repository;

import com.portfolio.manager.projeto.portifolio.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
}
