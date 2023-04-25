package com.netprecision.lanchonete.api.repository;

import com.netprecision.lanchonete.api.model.Pedido;
import com.netprecision.lanchonete.api.model.PedidoProduto;
import com.netprecision.lanchonete.api.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoProdutoRepository extends JpaRepository<PedidoProduto, Long> {
  PedidoProduto findByPedidoAndProduto(Pedido pedido, Produto produto);
}
