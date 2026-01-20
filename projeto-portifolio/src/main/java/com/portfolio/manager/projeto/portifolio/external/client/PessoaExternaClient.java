package com.portfolio.manager.projeto.portifolio.external.client;

import com.portfolio.manager.projeto.portifolio.dto.PessoaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PessoaExternaClient {

    private final RestTemplate restTemplate;

    public PessoaDTO buscarPorId(Long id) {
        return restTemplate.getForObject(
                "http://localhost:8080/api-externa/pessoas/" + id,
                PessoaDTO.class
        );
    }
}
