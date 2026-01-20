package com.portfolio.manager.projeto.portifolio.dto;

import com.portfolio.manager.projeto.portifolio.enums.StatusProjeto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortifolioDTO {
    private Long id;
    private String  nome;
    private LocalDate dataInicio;
    private LocalDate dataprevisaoFim;
    private LocalDate dataFim;
    private BigDecimal orcamento;
    private StatusProjeto status;
    private String risco;
    private String descricao;
}
