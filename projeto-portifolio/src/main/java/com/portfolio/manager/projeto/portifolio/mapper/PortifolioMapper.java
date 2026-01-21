package com.portfolio.manager.projeto.portifolio.mapper;

import com.portfolio.manager.projeto.portifolio.dto.PortifolioDTO;
import com.portfolio.manager.projeto.portifolio.model.Portifolio;

public class PortifolioMapper {

    public static PortifolioDTO toDTO(Portifolio p) {
        if (p == null) return null;

        PortifolioDTO dto = new PortifolioDTO();
        dto.setId(p.getId());
        dto.setNome(p.getNome());
        dto.setDataInicio(p.getDataInicio());
        dto.setDataPrevisaoFim(p.getDataPrevisaoFim());
        dto.setDataFim(p.getDataFim());
        dto.setOrcamento(p.getOrcamento());
        dto.setStatus(p.getStatus());
        dto.setRisco(p.getRisco());
        dto.setDescricao(p.getDescricao());
        dto.setIdGerente(p.getIdGerente());

        return dto;
    }

    public static Portifolio toEntity(PortifolioDTO dto) {
        if (dto == null) return null;

        Portifolio p = new Portifolio();
        p.setId(dto.getId());
        p.setNome(dto.getNome());
        p.setDataInicio(dto.getDataInicio());
        p.setDataPrevisaoFim(dto.getDataPrevisaoFim());
        p.setDataFim(dto.getDataFim());
        p.setOrcamento(dto.getOrcamento());
        p.setDescricao(dto.getDescricao());
        p.setStatus(dto.getStatus());
        p.setRisco(dto.getRisco());
        p.setIdGerente(dto.getIdGerente());


        return p;
    }
}