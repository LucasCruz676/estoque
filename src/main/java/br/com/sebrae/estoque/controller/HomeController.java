package br.com.sebrae.estoque.controller;

import br.com.sebrae.estoque.repository.ProdutoRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProdutoRepository repository;

    public HomeController(ProdutoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public String dashboard(Model model) {

        model.addAttribute(
                "totalProdutos",
                repository.count()
        );

        model.addAttribute(
                "produtosBaixos",
                repository.produtosComEstoqueBaixo()
        );

        model.addAttribute(
                "totalEstoque",
                repository.calcularValorEstoque()
        );

        return "dashboard";
    }
}