package com.netprecision.lanchonete.api.service;

import com.netprecision.lanchonete.api.model.Pedido;
import com.netprecision.lanchonete.api.model.Produto;
import com.netprecision.lanchonete.api.model.dto.PedidoDTO;

public interface PedidoService {

  Pedido cadastrar(PedidoDTO pedidoDTO);
  /*String adicionarProduto(Produto produto);
  String retirarProduto(Produto produto);*/

}
