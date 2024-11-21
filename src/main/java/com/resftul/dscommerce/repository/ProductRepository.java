package com.resftul.dscommerce.repository;

import com.resftul.dscommerce.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Verifica a existência de um produto pelo nome e descrição.
     *
     * @param name        O nome do produto.
     * @param description A descrição do produto.
     * @return {@code true} se um produto com o nome e descrição especificados existir, caso contrário, {@code false}.
     */
    boolean existsByNameAndDescription(String name, String description);

    /**
     * Busca produtos pelo nome usando uma consulta LIKE (case-insensitive).
     *
     * @param name     O nome parcial ou completo do produto a ser pesquisado.
     * @param pageable Informações de paginação para recuperar resultados de maneira paginada.
     * @return Uma {@link Page} contendo os resultados da busca.
     *         Cada elemento na página representa uma entidade {@link Product}.
     */
    @Query("SELECT p " +
           "FROM Product p " +
           "WHERE UPPER(p.name) " +
           "LIKE UPPER(CONCAT('%', :name, '%'))")
    Page<Product> searchByName(String name, Pageable pageable);
}
