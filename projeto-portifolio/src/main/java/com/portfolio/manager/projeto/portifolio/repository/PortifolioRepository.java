package com.portfolio.manager.projeto.portifolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.portfolio.manager.projeto.portifolio.model.Portifolio;
import org.springframework.stereotype.Repository;

@Repository
public interface PortifolioRepository extends JpaRepository<Portifolio, Long> {
}