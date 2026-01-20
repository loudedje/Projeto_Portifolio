package com.portfolio.manager.projeto.portifolio.external.controller;

import com.portfolio.manager.projeto.portifolio.dto.PessoaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api-externa/pessoas")
@RequiredArgsConstructor
public class PessoaController {

    private final Map<Long, PessoaDTO> banco = new HashMap<>();

    @PostMapping
    public PessoaDTO criar(@RequestBody PessoaDTO dto) {
        dto.setId((long) banco.size() + 1);
        banco.put(dto.getId(), dto);
        return dto;
    }

    @GetMapping("/{id}")
    public PessoaDTO buscar(@PathVariable Long id) {
        PessoaDTO pessoaDTO = banco.get(id);
        if (pessoaDTO == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return pessoaDTO;
    }
}
