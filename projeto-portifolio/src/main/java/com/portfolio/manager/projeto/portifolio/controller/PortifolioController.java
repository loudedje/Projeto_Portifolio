package com.portfolio.manager.projeto.portifolio.controller;

import com.portfolio.manager.projeto.portifolio.dto.PortifolioDTO;
import com.portfolio.manager.projeto.portifolio.mapper.PortifolioMapper;
import com.portfolio.manager.projeto.portifolio.model.Portifolio;
import com.portfolio.manager.projeto.portifolio.service.PortifolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/projetos")
public class PortifolioController {

    private final PortifolioService portifolioService;

    @Autowired
    public PortifolioController(PortifolioService portifolioService) {
        this.portifolioService = portifolioService;
    }



    @Operation(summary = "Listar todos os projetos",
            responses = { @ApiResponse(responseCode = "200", description = "Sucesso") })
    @GetMapping
    public ResponseEntity<List<PortifolioDTO>> listar() {
        List<PortifolioDTO> lista = portifolioService.listarTodos()
                .stream()
                .map(PortifolioMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Criar um projeto")
    @PostMapping
    public ResponseEntity<PortifolioDTO> salvar(@RequestBody PortifolioDTO portifolioDTO) {
        Portifolio entidade = PortifolioMapper.toEntity(portifolioDTO);
        Portifolio salvo = portifolioService.salvar(entidade);
        return new ResponseEntity<>(PortifolioMapper.toDTO(salvo), HttpStatus.CREATED);
    }

    @Operation(summary = "Buscar projeto por ID")
    @GetMapping("/{id}")
    public ResponseEntity<PortifolioDTO> getById(@PathVariable Long id) {
        Portifolio projeto = portifolioService.buscarPorId(id);
        return ResponseEntity.ok(PortifolioMapper.toDTO(projeto));
    }

    @Operation(summary = "Atualizar um projeto")
    @PutMapping("/{id}")
    public ResponseEntity<PortifolioDTO> atualizar(@PathVariable Long id, @RequestBody PortifolioDTO dto) {
        Portifolio dadosNovos = PortifolioMapper.toEntity(dto);
        Portifolio atualizado = portifolioService.atualizar(id, dadosNovos);
        return ResponseEntity.ok(PortifolioMapper.toDTO(atualizado));
    }

    @Operation(summary = "Excluir um projeto")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortifolio(@PathVariable("id") Long id){
        portifolioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Associar pessoa ao projeto")
    @PostMapping("/{projetoId}/pessoas/{pessoaId}")
    public ResponseEntity<Void> associarPessoa(@PathVariable Long projetoId, @PathVariable Long pessoaId) {
        portifolioService.associarPessoa(projetoId, pessoaId);
        return ResponseEntity.ok().build();

    }

}