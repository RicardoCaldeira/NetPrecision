package com.netprecision.lanchonete.api.repository;

import com.netprecision.lanchonete.api.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
