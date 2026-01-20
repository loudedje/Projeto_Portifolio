package com.portfolio.manager.projeto.portifolio.controller;

import com.portfolio.manager.projeto.portifolio.dto.PortifolioDTO;
import com.portfolio.manager.projeto.portifolio.mapper.PortifolioMapper;
import com.portfolio.manager.projeto.portifolio.model.Portifolio;
import com.portfolio.manager.projeto.portifolio.service.PortifolioService; // Ajustado para o nome do seu arquivo
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

    @GetMapping
    public ResponseEntity<List<PortifolioDTO>> listar() {
        List<PortifolioDTO> lista = portifolioService.listarTodos()
                .stream()
                .map(PortifolioMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<PortifolioDTO> salvar(@RequestBody PortifolioDTO portifolioDTO) {

        Portifolio entidade = PortifolioMapper.toEntity(portifolioDTO);
        Portifolio salvo = portifolioService.salvar(entidade);

        PortifolioDTO resultado = PortifolioMapper.toDTO(salvo);

        return new ResponseEntity<>(resultado, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletePortifolio(@PathVariable("id")Long id){
        portifolioService.excluir(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<PortifolioDTO> getByid(@PathVariable Long id) {
        Portifolio projeto = portifolioService.buscarPorId(id);
        return ResponseEntity.ok(PortifolioMapper.toDTO(projeto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PortifolioDTO> atualizar(@PathVariable Long id, @RequestBody PortifolioDTO dto) {
        Portifolio dadosNovos = PortifolioMapper.toEntity(dto);

        Portifolio atualizado = portifolioService.atualizar(id, dadosNovos);

        return ResponseEntity.ok(PortifolioMapper.toDTO(atualizado));
    }
    @PostMapping("/{projetoId}/pessoas/{pessoaId}")
    public void associarPessoa(
            @PathVariable Long projetoId,
            @PathVariable Long pessoaId
    ) {
        portifolioService.associarPessoa(projetoId, pessoaId);
    }
}

