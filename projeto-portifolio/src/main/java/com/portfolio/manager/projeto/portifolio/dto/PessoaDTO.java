package com.portfolio.manager.projeto.portifolio.dto;

import com.portfolio.manager.projeto.portifolio.enums.Atribuicao;
import lombok.Data;

@Data
public class PessoaDTO {
    private Long id;
    private String nome;
    private Atribuicao atribuicao;
}
