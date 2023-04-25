package com.netprecision.lanchonete.api.service.impl;

import com.netprecision.lanchonete.api.exception.OrderException;
import com.netprecision.lanchonete.api.exception.ProductException;
import com.netprecision.lanchonete.api.model.Pedido;
import com.netprecision.lanchonete.api.model.PedidoProduto;
import com.netprecision.lanchonete.api.model.Produto;
import com.netprecision.lanchonete.api.model.dto.ItensPedidoDTO;
import com.netprecision.lanchonete.api.repository.PedidoProdutoRepository;
import com.netprecision.lanchonete.api.repository.PedidoRepository;
import com.netprecision.lanchonete.api.repository.ProdutoRepository;
import com.netprecision.lanchonete.api.service.PedidoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

  @Override
  public Pedido cadastrar(ItensPedidoDTO itensPedidoDTO) {
    Pedido novoPedido = new Pedido();

    Double precoTotal = 0.0;
    List<PedidoProduto> pedidoProdutoList = new ArrayList<>();
    for (PedidoProduto item : itensPedidoDTO.getItens()) {
      PedidoProduto pedidoProduto = new PedidoProduto();

      Produto produto = produtoRepository.findById(item.getProduto().getId())
          .orElseThrow(() -> throwProductNotFoundException(item.getProduto().getId()));

      precoTotal += produto.getPreco() * item.getQuantidade();

      pedidoProduto.setProduto(produto);
      pedidoProduto.setPedido(novoPedido);
      pedidoProduto.setQuantidade(item.getQuantidade());

      pedidoProdutoList.add(pedidoProduto);
    }

    novoPedido.setPrecoTotal(precoTotal);
    pedidoRepository.saveAndFlush(novoPedido);

    pedidoProdutoRepository.saveAllAndFlush(pedidoProdutoList);

   return novoPedido;
  }

  @Override
  public Pedido incrementarPedido(Long idPedido, ItensPedidoDTO itensPedidoDTO) {
    Pedido pedido = pedidoRepository.findById(idPedido)
        .orElseThrow(() -> throwOrderNotFoundException(idPedido));

    if (pedido.estaFechado()) {
      throwClosedOrderException(idPedido);
    }

    List<PedidoProduto> pedidoProdutoList = new ArrayList<>();
    for (PedidoProduto item : itensPedidoDTO.getItens()) {
      Produto produto = produtoRepository.findById(item.getProduto().getId())
          .orElseThrow(() -> throwProductNotFoundException(item.getProduto().getId()));

      PedidoProduto pedidoProduto = pedidoProdutoRepository.findByPedidoAndProduto(pedido, produto);
      if (pedidoProduto == null) {
        pedidoProduto = new PedidoProduto(pedido, produto, item.getQuantidade());
      } else {
        pedidoProduto.setQuantidade(pedidoProduto.getQuantidade() + item.getQuantidade());
      }

      pedidoProdutoList.add(pedidoProduto);
      pedido.setPrecoTotal(pedido.getPrecoTotal() + item.getQuantidade() * produto.getPreco());
    }

    pedidoRepository.saveAndFlush(pedido);
    pedidoProdutoRepository.saveAllAndFlush(pedidoProdutoList);

    return pedido;
  }

  @Override
  public Pedido decrementarPedido(Long idPedido, ItensPedidoDTO itensPedidoDTO) {
    Pedido pedido = pedidoRepository.findById(idPedido)
        .orElseThrow(() -> throwOrderNotFoundException(idPedido));

    if (pedido.estaFechado()) {
      throwClosedOrderException(idPedido);
    }

    List<PedidoProduto> pedidoProdutoList = new ArrayList<>();
    for (PedidoProduto item : itensPedidoDTO.getItens()) {
      Produto produto = produtoRepository.findById(item.getProduto().getId())
          .orElseThrow(() -> throwProductNotFoundException(item.getProduto().getId()));

      PedidoProduto pedidoProduto = pedidoProdutoRepository.findByPedidoAndProduto(pedido, produto);
      if (pedidoProduto == null) {
        throw new IllegalArgumentException(
            "O pedido informado não contém o produto " + item.getProduto().getId());
      }

      Double precoPedidoProduto = pedidoProduto.getQuantidade() * produto.getPreco();

      pedidoProduto.setQuantidade(pedidoProduto.getQuantidade() - item.getQuantidade());
      if (pedidoProduto.getQuantidade() < 1) {
        pedido.setPrecoTotal(pedido.getPrecoTotal() - precoPedidoProduto);
        pedidoProdutoRepository.delete(pedidoProduto);
      } else {
        pedido.setPrecoTotal(pedido.getPrecoTotal() - item.getQuantidade() * produto.getPreco());
        pedidoProdutoList.add(pedidoProduto);
      }
    }

    pedidoRepository.saveAndFlush(pedido);
    pedidoProdutoRepository.saveAllAndFlush(pedidoProdutoList);

    return pedido;
  }

  @Override
  public Pedido fecharPedido(Long idPedido, Double valorPagamento) {
    Pedido pedido = pedidoRepository.findById(idPedido)
        .orElseThrow(() -> throwOrderNotFoundException(idPedido));

    if (pedido.estaFechado()) {
      throwClosedOrderException(idPedido);
    }

    if (valorPagamento < pedido.getPrecoTotal()) {
      throw new IllegalArgumentException("Valor de pagamento insuficiente!");
    }

    pedido.fechar(valorPagamento);
    return pedido;
  }

  @Override
  public List<Pedido> listarTodos() {
    return pedidoRepository.findAll();
  }

  @Override
  public Pedido listarPorId(Long id) {
    return pedidoRepository.findById(id)
        .orElseThrow(() -> throwOrderNotFoundException(id));
  }

  private OrderException throwOrderNotFoundException(Long idpedido) {
    throw new OrderException("O pedido " + idpedido + " não existe!");
  }

  private OrderException throwClosedOrderException(Long idpedido) {
    throw new OrderException("O pedido " + idpedido + " já encontra-se fechado!");
  }

  private ProductException throwProductNotFoundException(Long idProduto) {
    throw new ProductException("O produto " + idProduto + " não existe!");
  }

}
