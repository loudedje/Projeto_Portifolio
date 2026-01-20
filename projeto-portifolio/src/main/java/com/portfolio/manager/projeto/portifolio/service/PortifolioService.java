package com.portfolio.manager.projeto.portifolio.service;

import com.portfolio.manager.projeto.portifolio.enums.StatusProjeto;
import com.portfolio.manager.projeto.portifolio.model.Pessoa;
import com.portfolio.manager.projeto.portifolio.model.Portifolio;
import com.portfolio.manager.projeto.portifolio.repository.PortifolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortifolioService {

    private final PortifolioRepository portifolioRepository;
    private final PessoaService pessoaService;

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

    public void associarMembro(Long idProjeto, Long idPessoa) {
        Portifolio projeto = buscarPorId(idProjeto);
        Pessoa pessoa = pessoaService.buscarPorId(idPessoa);

        if (!pessoa.isFuncionario()) {
            throw new IllegalStateException("Apenas membros com a atribuição 'funcionário' podem ser associados.");
        }

        if (projeto.getMembros().size() >= 10) {
            throw new IllegalStateException("Cada projeto deve permitir a alocação de no máximo 10 membros.");
        }


        projeto.getMembros().add(pessoa);
        portifolioRepository.save(projeto);
    }

}


