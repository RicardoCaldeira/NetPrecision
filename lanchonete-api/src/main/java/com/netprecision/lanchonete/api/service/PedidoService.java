package com.netprecision.lanchonete.api.service;

import com.netprecision.lanchonete.api.model.Pedido;
import com.netprecision.lanchonete.api.model.dto.ItensPedidoDTO;

import java.util.List;

public interface PedidoService {
  Pedido cadastrar(ItensPedidoDTO itensPedidoDTO);
  Pedido incrementarPedido(Long idPedido, ItensPedidoDTO itensPedidoDTO);
  List<Pedido> listarTodos();
  Pedido listarPorId(Long id);
}
