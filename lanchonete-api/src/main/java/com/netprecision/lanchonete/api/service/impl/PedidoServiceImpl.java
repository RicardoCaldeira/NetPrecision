package com.netprecision.lanchonete.api.service.impl;

import com.netprecision.lanchonete.api.model.Pedido;
import com.netprecision.lanchonete.api.model.PedidoProduto;
import com.netprecision.lanchonete.api.model.Produto;
import com.netprecision.lanchonete.api.model.dto.PedidoDTO;
import com.netprecision.lanchonete.api.repository.PedidoProdutoRepository;
import com.netprecision.lanchonete.api.repository.PedidoRepository;
import com.netprecision.lanchonete.api.repository.ProdutoRepository;
import com.netprecision.lanchonete.api.service.PedidoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Transactional
@Service
public class PedidoServiceImpl implements PedidoService {

  @Autowired
  private PedidoRepository pedidoRepository;

  @Autowired
  private ProdutoRepository produtoRepository;

  @Autowired
  private PedidoProdutoRepository pedidoProdutoRepository;


  public Pedido cadastrar(PedidoDTO pedidoDTO) {
    Pedido novoPedido = new Pedido();

    Double precoTotal = 0.0;
    List<PedidoProduto> pedidoProdutoList = new ArrayList<>();
    for (PedidoProduto item : pedidoDTO.getItens()) {
      PedidoProduto pedidoProduto = new PedidoProduto();

      Produto produto = produtoRepository.getReferenceById(item.getProduto().getId());
      precoTotal += produto.getPreco();
      pedidoProduto.setProduto(produto);

      pedidoProduto.setPedido(novoPedido);
      pedidoProduto.setQuantidade(item.getQuantidade());

      pedidoProdutoList.add(pedidoProduto);
    }

    novoPedido.setPrecoTotal(precoTotal);
    novoPedido.setDataPedido(new Date());
    pedidoRepository.save(novoPedido);

    pedidoProdutoRepository.saveAll(pedidoProdutoList);

    return novoPedido;
  }

}
