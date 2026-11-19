package br.com.sebrae.estoque.repository;

import br.com.sebrae.estoque.model.Produto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProdutoRepository
        extends JpaRepository<Produto, Long> {

    @Query("""
        SELECT p FROM Produto p
        WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
        OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :busca, '%'))
    """)
    List<Produto> pesquisar(String busca);

    @Query("""
        SELECT p FROM Produto p
        WHERE p.quantidade <= p.estoqueMinimo
    """)
    List<Produto> produtosComEstoqueBaixo();

    @Query("""
        SELECT COALESCE(SUM(p.precoCusto * p.quantidade), 0)
        FROM Produto p
    """)
    java.math.BigDecimal calcularValorEstoque();

    boolean existsByCategoriaId(Long categoriaId);
}