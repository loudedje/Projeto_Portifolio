package com.portfolio.manager.projeto.portifolio.model;

import com.portfolio.manager.projeto.portifolio.enums.Atribuicao;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
public class Pessoa {

    private Long id;
    private String nome;
    private Atribuicao atribuicao;

}


