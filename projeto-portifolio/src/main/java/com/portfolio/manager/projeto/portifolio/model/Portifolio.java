package com.portfolio.manager.projeto.portifolio.model;

import com.portfolio.manager.projeto.portifolio.enums.StatusProjeto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(name = "Projeto")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Portifolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nome;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_previsao_fim")
    private LocalDate dataPrevisaoFim;

    @Column(name= "data_fim")
    private LocalDate dataFim;

    private BigDecimal orcamento;

    @Column(length = 1000)
    private String descricao;

    @Enumerated(EnumType.STRING)
    private StatusProjeto status;

    @Column(name = "id_gerente")
    private Long idGerente;

    private String risco;


    @ElementCollection
    @CollectionTable(
            name = "Portifolio_pessoas",
            joinColumns = @JoinColumn(name = "portifolio_id")
    )
    @Column(name = "pessoa_id")
    private Set<Long> pessoasIds = new HashSet<>();

}

