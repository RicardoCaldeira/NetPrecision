package com.netprecision.lanchonete.api.service;

import com.netprecision.lanchonete.api.model.Pedido;
import com.netprecision.lanchonete.api.model.dto.ItensPedidoDTO;

import java.util.List;

public interface PedidoService {
  Pedido cadastrar(ItensPedidoDTO itensPedidoDTO);
  Pedido incrementarPedido(Long idPedido, ItensPedidoDTO itensPedidoDTO);
  Pedido decrementarPedido(Long idPedido, ItensPedidoDTO itensPedidoDTO);
  Pedido fecharPedido(Long idPedido, Double valorPagamento);
  List<Pedido> listarTodos();
  Pedido listarPorId(Long id);
}
