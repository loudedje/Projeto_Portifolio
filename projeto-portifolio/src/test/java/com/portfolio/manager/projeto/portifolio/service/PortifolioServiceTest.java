package com.portfolio.manager.projeto.portifolio.service;

import com.portfolio.manager.projeto.portifolio.dto.PessoaDTO;
import com.portfolio.manager.projeto.portifolio.enums.Atribuicao;
import com.portfolio.manager.projeto.portifolio.enums.StatusProjeto;
import com.portfolio.manager.projeto.portifolio.external.client.PessoaExternaClient;
import com.portfolio.manager.projeto.portifolio.model.Portifolio;
import com.portfolio.manager.projeto.portifolio.repository.PortifolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortifolioServiceTest {

    @Mock
    private PortifolioRepository portifolioRepository;

    @Mock
    private PessoaExternaClient pessoaExternaClient;

    @InjectMocks
    private PortifolioService portifolioService;

    private Portifolio projeto;

    @BeforeEach
    void setUp() {
        projeto = new Portifolio();
        projeto.setId(1L);
        projeto.setNome("Projeto Original");
        projeto.setStatus(StatusProjeto.EM_ANALISE);
        projeto.setOrcamento(new BigDecimal("50000"));
        projeto.setDataInicio(LocalDate.now());
        projeto.setDataPrevisaoFim(LocalDate.now().plusMonths(1));
        projeto.setPessoasIds(new HashSet<Long>());
    }
    @Test
    @DisplayName("Deve calcular risco ALTO quando orçamento > 500k ou prazo > 6 meses")
    void deveCalcularRiscoAlto() {
        projeto.setOrcamento(new BigDecimal("600000"));
        projeto.setDataInicio(LocalDate.now());
        projeto.setDataPrevisaoFim(LocalDate.now().plusMonths(7));
        when(portifolioRepository.findById(1L)).thenReturn(Optional.of(projeto));

        when(portifolioRepository.save(any())).thenReturn(projeto);

        Portifolio salvo = portifolioService.salvar(projeto);

        assertEquals("RISCO ALTO", salvo.getRisco());
    }
    @Test
    @DisplayName("Deve calcular risco MÉDIO quando orçamento > 100k")
    void deveCalcularRiscoMedio() {
        projeto.setOrcamento(new BigDecimal("200000")); // Entre 100k e 500k
        when(portifolioRepository.findById(1L)).thenReturn(Optional.of(projeto));
        when(portifolioRepository.save(any())).thenReturn(projeto);

        Portifolio salvo = portifolioService.salvar(projeto);

        assertEquals("RISCO MÉDIO", salvo.getRisco());
    }

    @Test
    @DisplayName("Deve calcular risco BAIXO quando orçamento < 100k e prazo curto")
    void deveCalcularRiscoBaixo() {
        projeto.setOrcamento(new BigDecimal("50000"));
        projeto.setDataPrevisaoFim(LocalDate.now().plusMonths(1));
        when(portifolioRepository.findById(1L)).thenReturn(Optional.of(projeto));
        when(portifolioRepository.save(any())).thenReturn(projeto);

        Portifolio salvo = portifolioService.salvar(projeto);

        assertEquals("RISCO BAIXO", salvo.getRisco());
    }

    @Test
    @DisplayName("Deve lançar exceção ao pular etapas de status")
    void deveValidarTransicaoStatusInvalida() {
        Portifolio dadosNovos = new Portifolio();
        dadosNovos.setStatus(StatusProjeto.PLANEJADO); // Pulando de EM_ANALISE para PLANEJADO

        when(portifolioRepository.findById(1L)).thenReturn(Optional.of(projeto));

        assertThrows(IllegalStateException.class, () ->
                portifolioService.atualizar(1L, dadosNovos)
        );
    }

    @Test
    @DisplayName("Deve permitir transição lógica de status")
    void devePermitirTransicaoStatusValida() {
        Portifolio dadosNovos = new Portifolio();
        dadosNovos.setStatus(StatusProjeto.ANALISE_REALIZADA); // Próximo passo correto

        when(portifolioRepository.findById(1L)).thenReturn(Optional.of(projeto));
        when(portifolioRepository.save(any())).thenReturn(projeto);

        Portifolio atualizado = portifolioService.atualizar(1L, dadosNovos);

        assertEquals(StatusProjeto.ANALISE_REALIZADA, atualizado.getStatus());
    }

    @Test
    @DisplayName("Deve impedir exclusão de projetos Iniciados, Em Andamento ou Encerrados")
    void deveImpedirExclusaoProjetosAtivos() {
        projeto.setStatus(StatusProjeto.INICIADO);
        when(portifolioRepository.findById(1L)).thenReturn(Optional.of(projeto));

        assertThrows(IllegalStateException.class, () -> portifolioService.excluir(1L));
    }

    @Test
    @DisplayName("Deve impedir associação de membros que não são FUNCIONARIOS")
    void deveValidarAtribuicaoAoAssociarMembro() {
        PessoaDTO pessoaMock = new PessoaDTO();
        pessoaMock.setAtribuicao(Atribuicao.TERCEIRO); // Não é funcionário

        when(portifolioRepository.findById(1L)).thenReturn(Optional.of(projeto));
        when(pessoaExternaClient.buscarPorId(2L)).thenReturn(pessoaMock);

        assertThrows(ResponseStatusException.class, () ->
                portifolioService.associarPessoa(1L, 2L)
        );
    }

    @Test
    @DisplayName("Deve associar membro com sucesso quando as regras forem atendidas")
    void deveAssociarMembroComSucesso() {
        PessoaDTO pessoaMock = new PessoaDTO();
        pessoaMock.setAtribuicao(Atribuicao.FUNCIONARIO);

        when(portifolioRepository.findById(1L)).thenReturn(Optional.of(projeto));
        when(pessoaExternaClient.buscarPorId(2L)).thenReturn(pessoaMock);

        portifolioService.associarPessoa(1L, 2L);

        assertTrue(projeto.getPessoasIds().contains(2L));
        verify(portifolioRepository, times(1)).save(projeto);
    }

    @Test
    @DisplayName("Deve listar todos os projetos corretamente")
    void deveListarProjetos() {
        when(portifolioRepository.findAll()).thenReturn(List.of(projeto));
        List<Portifolio> lista = portifolioService.listarTodos();
        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
    }
    @Test
    @DisplayName("Deve impedir associação quando o projeto já tem 10 membros")
    void deveImpedirMaisDeDezMembros() {
        for (long i = 1; i <= 10; i++) {
            projeto.getPessoasIds().add(i);
        }
        when(portifolioRepository.findById(1L)).thenReturn(Optional.of(projeto));

        PessoaDTO pessoaMock = new PessoaDTO();
        pessoaMock.setAtribuicao(Atribuicao.FUNCIONARIO);
        when(pessoaExternaClient.buscarPorId(99L)).thenReturn(pessoaMock);
        assertThrows(ResponseStatusException.class, () ->
                portifolioService.associarPessoa(1L, 99L)
        );
    }
}