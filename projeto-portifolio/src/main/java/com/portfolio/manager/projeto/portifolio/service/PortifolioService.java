package com.portfolio.manager.projeto.portifolio.service;

import com.portfolio.manager.projeto.portifolio.dto.PessoaDTO;
import com.portfolio.manager.projeto.portifolio.enums.Atribuicao;
import com.portfolio.manager.projeto.portifolio.enums.StatusProjeto;
import com.portfolio.manager.projeto.portifolio.external.client.PessoaExternaClient;
import com.portfolio.manager.projeto.portifolio.external.controller.PessoaController;
import com.portfolio.manager.projeto.portifolio.model.Portifolio;
import com.portfolio.manager.projeto.portifolio.repository.PortifolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortifolioService {

    private final PortifolioRepository portifolioRepository;
    private final PessoaExternaClient pessoaExternaClient;
    private final PessoaController pessoaController;
    public List<Portifolio> listarTodos() {
        return portifolioRepository.findAll();
    }

    public Portifolio buscarPorId(Long id) {
        return portifolioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
    }

    public Portifolio salvar(Portifolio portifolio) {
        validarGerenteExistente(portifolio.getIdGerente());
        if (portifolio.getId() != null) {
            Portifolio portifolioExistente = buscarPorId(portifolio.getId());
            transicaoStatus(portifolioExistente.getStatus(), portifolio.getStatus());
        } else {
            portifolio.setStatus(StatusProjeto.EM_ANALISE);
        }

        portifolio.setRisco(calcularRisco(portifolio));
        return portifolioRepository.save(portifolio);
    }

    public Portifolio atualizar(Long id, Portifolio dadosNovos) {
        Portifolio projetoExistente = buscarPorId(id);

        transicaoStatus(projetoExistente.getStatus(), dadosNovos.getStatus());

        projetoExistente.setNome(dadosNovos.getNome());
        projetoExistente.setStatus(dadosNovos.getStatus());
        projetoExistente.setOrcamento(dadosNovos.getOrcamento());
        projetoExistente.setDataPrevisaoFim(dadosNovos.getDataPrevisaoFim());
        projetoExistente.setDataInicio(dadosNovos.getDataInicio());
        projetoExistente.setDescricao(dadosNovos.getDescricao());

        projetoExistente.setRisco(calcularRisco(projetoExistente));

        return portifolioRepository.save(projetoExistente);
    }

    private void transicaoStatus(StatusProjeto atual, StatusProjeto novo) {
        if (atual == novo || novo == null) return;

        boolean transicaoValida = switch (atual) {
            case EM_ANALISE -> novo == StatusProjeto.ANALISE_REALIZADA;
            case ANALISE_REALIZADA -> novo == StatusProjeto.ANALISE_APROVADA;
            case ANALISE_APROVADA -> novo == StatusProjeto.PLANEJADO;
            case PLANEJADO -> novo == StatusProjeto.INICIADO;
            case INICIADO -> novo == StatusProjeto.EM_ANDAMENTO;
            case EM_ANDAMENTO -> novo == StatusProjeto.ENCERRADO;
            default -> false;
        };

        if (!transicaoValida) {
            throw new IllegalStateException("Transição inválida de " + atual + " para " + novo);
        }
    }

    public void excluir(Long id) {
        Portifolio projeto = portifolioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));
        if (projeto.getStatus() == StatusProjeto.INICIADO||
                projeto.getStatus() == StatusProjeto.EM_ANDAMENTO ||
                projeto.getStatus() == StatusProjeto.ENCERRADO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Erro: Projetos com status " + projeto.getStatus() + " não podem ser excluídos.");
        }
        portifolioRepository.delete(projeto);
    }

    private String calcularRisco(Portifolio p) {
        if (p.getOrcamento() == null || p.getDataInicio() == null || p.getDataPrevisaoFim() == null) {
            return "BAIXO";
        }

        long meses = java.time.temporal.ChronoUnit.MONTHS.between(p.getDataInicio(), p.getDataPrevisaoFim());
        BigDecimal orcamento = p.getOrcamento();

        if (orcamento.compareTo(new BigDecimal("500000")) > 0 || meses > 6) {
            return "RISCO ALTO";
        } else if (orcamento.compareTo(new BigDecimal("100000")) > 0 || meses >= 3) {
            return "RISCO MÉDIO";
        } else {
            return "RISCO BAIXO";
        }
    }

    public void associarPessoa(Long portifolioId, Long pessoaId) {

        Portifolio portifolio = portifolioRepository.findById(portifolioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));

        PessoaDTO pessoa;
        try {
            pessoa = pessoaExternaClient.buscarPorId(pessoaId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pessoa ID " + pessoaId + " não encontrada no sistema externo.");
        }

        if (!Atribuicao.FUNCIONARIO.equals(pessoa.getAtribuicao())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Erro: A pessoa " + pessoa.getNome() + " é " + pessoa.getAtribuicao() +
                            ". Apenas membros com atribuição FUNCIONARIO podem ser associados.");
        }

        if (portifolio.getPessoasIds().size() >= 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Limite atingido: O projeto já possui 10 membros.");
        }

        portifolio.getPessoasIds().add(pessoaId);
        portifolioRepository.save(portifolio);
    }

    private void validarGerenteExistente(Long idGerente) {
        if (idGerente == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O projeto deve ter um gerente responsável.");
        }

        try {
            pessoaController.buscar(idGerente);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gerente não encontrado com o ID: " + idGerente);
        }
    }
    }





