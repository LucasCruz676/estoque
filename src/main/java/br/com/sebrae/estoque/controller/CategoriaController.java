package br.com.sebrae.estoque.controller;

import br.com.sebrae.estoque.model.Categoria;
import br.com.sebrae.estoque.repository.CategoriaRepository;
import br.com.sebrae.estoque.repository.ProdutoRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaRepository repository;
    private final ProdutoRepository produtoRepository;

    public CategoriaController(
            CategoriaRepository repository,
            ProdutoRepository produtoRepository) {

        this.repository = repository;
        this.produtoRepository = produtoRepository;
    }

    @GetMapping
    public String listar(Model model) {

        model.addAttribute(
                "categorias",
                repository.findAll()
        );

        return "categorias";
    }

    @GetMapping("/novo")
    public String novo(Model model) {

        model.addAttribute("categoria", new Categoria());

        return "formulario-categoria";
    }

    @PostMapping("/salvar")
    public String salvar(
            @ModelAttribute Categoria categoria,
            RedirectAttributes redirect) {

        if (categoria.getNome() == null
                || categoria.getNome().isBlank()) {

            redirect.addFlashAttribute(
                    "erro", "Informe o nome da categoria."
            );

            return "redirect:/categorias/novo";
        }

        repository.save(categoria);

        redirect.addFlashAttribute(
                "sucesso", "Categoria salva com sucesso!"
        );

        return "redirect:/categorias";
    }

    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model model) {

        Categoria categoria = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Categoria não encontrada")
                );

        model.addAttribute("categoria", categoria);

        return "formulario-categoria";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(
            @PathVariable Long id,
            RedirectAttributes redirect) {

        if (produtoRepository.existsByCategoriaId(id)) {

            redirect.addFlashAttribute(
                    "erro", "Categoria possui produtos cadastrados."
            );

            return "redirect:/categorias";
        }

        repository.deleteById(id);

        redirect.addFlashAttribute(
                "sucesso", "Categoria excluída!"
        );

        return "redirect:/categorias";
    }
}