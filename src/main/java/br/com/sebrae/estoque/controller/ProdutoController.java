package br.com.sebrae.estoque.controller;

import br.com.sebrae.estoque.model.Produto;
import br.com.sebrae.estoque.model.Categoria;

import br.com.sebrae.estoque.repository.ProdutoRepository;
import br.com.sebrae.estoque.repository.CategoriaRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoController(
            ProdutoRepository produtoRepository,
            CategoriaRepository categoriaRepository) {

        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) String busca,
            Model model) {

        if (busca != null && !busca.isBlank()) {
            model.addAttribute(
                    "produtos",
                    produtoRepository.pesquisar(busca)
            );
        } else {
            model.addAttribute(
                    "produtos",
                    produtoRepository.findAll()
            );
        }

        model.addAttribute("busca", busca);

        return "produtos";
    }

    @GetMapping("/novo")
    public String novo(Model model) {

        model.addAttribute("produto", new Produto());

        model.addAttribute("categoriaNome", "");

        return "formulario-produto";
    }


    @PostMapping("/salvar")
    public String salvar(
            @ModelAttribute Produto produto,
            @RequestParam(required = false) String categoriaNome,
            RedirectAttributes redirect) {

        if (categoriaNome == null || categoriaNome.isBlank()) {

            produto.setCategoria(null);

        } else {

            String nomeNormalizado = categoriaNome.trim();

            Categoria categoria = categoriaRepository
                    .findByNomeIgnoreCase(nomeNormalizado)
                    .orElseGet(() -> {

                        Categoria novaCategoria = new Categoria();
                        novaCategoria.setNome(nomeNormalizado);

                        return categoriaRepository.save(novaCategoria);
                    });

            produto.setCategoria(categoria);
        }

        if (produto.getQuantidade() == null
                || produto.getQuantidade() < 0
                || produto.getEstoqueMinimo() == null
                || produto.getEstoqueMinimo() < 0
                || produto.getPrecoCusto() == null
                || produto.getPrecoCusto().signum() < 0
                || produto.getPrecoVenda() == null
                || produto.getPrecoVenda().signum() < 0) {

            redirect.addFlashAttribute(
                    "erro", "Verifique os valores informados."
            );

            return "redirect:/produtos";
        }

        produtoRepository.save(produto);

        redirect.addFlashAttribute(
                "sucesso", "Produto salvo com sucesso!"
        );

        return "redirect:/produtos";
    }


    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model model) {

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Produto não encontrado")
                );

        model.addAttribute("produto", produto);

        model.addAttribute(
                "categorias",
                categoriaRepository.findAll()
        );

        return "formulario-produto";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(
            @PathVariable Long id,
            RedirectAttributes redirect) {

        produtoRepository.deleteById(id);

        redirect.addFlashAttribute(
                "sucesso", "Produto excluído com sucesso!"
        );

        return "redirect:/produtos";
    }

}