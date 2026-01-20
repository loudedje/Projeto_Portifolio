package com.portfolio.manager.projeto.portifolio.service;

import com.portfolio.manager.projeto.portifolio.dto.PessoaDTO;
import com.portfolio.manager.projeto.portifolio.enums.Atribuicao;
import com.portfolio.manager.projeto.portifolio.enums.StatusProjeto;
import com.portfolio.manager.projeto.portifolio.external.client.PessoaExternaClient;
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
    public List<Portifolio> listarTodos() {
        return portifolioRepository.findAll();
    }

    public Portifolio buscarPorId(Long id) {
        return portifolioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
    }

    public Portifolio salvar(Portifolio portifolio) {
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
            default -> false; // ENCERRADO ou outros estados finais não permitem transição
        };

        if (!transicaoValida) {
            throw new IllegalStateException("Transição inválida de " + atual + " para " + novo);
        }
    }

    public void excluir(Long id) {
        Portifolio projeto = buscarPorId(id);

        if (List.of(StatusProjeto.INICIADO, StatusProjeto.EM_ANDAMENTO, StatusProjeto.ENCERRADO)
                .contains(projeto.getStatus())) {
            throw new IllegalStateException("Não é possível excluir projetos com status: " + projeto.getStatus());
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        PessoaDTO pessoa = pessoaExternaClient.buscarPorId(pessoaId);

        if (pessoa.getAtribuicao() != Atribuicao.FUNCIONARIO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Apenas pessoas com atribuição FUNCIONARIO podem ser associadas");
        }

        if (portifolio.getPessoasIds().size() >= 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Projeto já possui 10 pessoas");
        }

        portifolio.getPessoasIds().add(pessoaId);
        portifolioRepository.save(portifolio);
    }


}


