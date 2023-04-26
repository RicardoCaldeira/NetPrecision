package com.netprecision.lanchonete.api.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.netprecision.lanchonete.api.exception.OrderException;
import com.netprecision.lanchonete.api.exception.ProductException;
import com.netprecision.lanchonete.api.model.Pedido;
import com.netprecision.lanchonete.api.model.PedidoProduto;
import com.netprecision.lanchonete.api.model.Produto;
import com.netprecision.lanchonete.api.model.dto.ItensPedidoDTO;
import com.netprecision.lanchonete.api.repository.PedidoProdutoRepository;
import com.netprecision.lanchonete.api.repository.PedidoRepository;
import com.netprecision.lanchonete.api.repository.ProdutoRepository;
import com.netprecision.lanchonete.api.service.impl.PedidoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class PedidoServiceImplTests {

  @Mock
  private PedidoRepository pedidoRepository;

  @Mock
  private ProdutoRepository produtoRepository;

  @Mock
  private PedidoProdutoRepository pedidoProdutoRepository;

  @InjectMocks
  private PedidoServiceImpl pedidoService;

  @Nested
  class cadastrar {

    @Test
    public void shouldSave_givenValidItem() {
      ItensPedidoDTO itensPedidoDTO = new ItensPedidoDTO();
      List<PedidoProduto> itens = new ArrayList<>();

      Produto produto = new Produto();
      produto.setId(1L);
      produto.setPreco(10.0);

      PedidoProduto item = new PedidoProduto();
      item.setProduto(produto);
      item.setQuantidade(2);

      itens.add(item);
      itensPedidoDTO.setItens(itens);

      Pedido novoPedido = new Pedido();
      novoPedido.setId(1L);

      when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
      when(pedidoRepository.saveAndFlush(any(Pedido.class))).thenReturn(novoPedido);

      Pedido pedidoCadastrado = pedidoService.cadastrar(itensPedidoDTO);

      assertEquals(20.0, pedidoCadastrado.getPrecoTotal());
      assertNotNull(pedidoCadastrado.getDataCriacaoPedido());

      verify(pedidoRepository, times(1)).saveAndFlush(pedidoCadastrado);
      verify(pedidoProdutoRepository, times(1)).saveAllAndFlush(anyList());
    }

    @Test
    void shouldNotSave_givenInvalidItem() {
      ItensPedidoDTO itensPedidoDTO = new ItensPedidoDTO();
      Produto produto = new Produto();
      produto.setId(999L);
      PedidoProduto item = new PedidoProduto();
      item.setProduto(produto);
      item.setQuantidade(1);
      itensPedidoDTO.setItens(Collections.singletonList(item));

      when(produtoRepository.findById(anyLong())).thenReturn(Optional.empty());

      ProductException thrown = assertThrows(ProductException.class, () ->
        pedidoService.cadastrar(itensPedidoDTO));

      assertEquals("O produto 999 não existe!", thrown.getMessage());
      verify(pedidoRepository, never()).saveAndFlush(any(Pedido.class));
      verify(pedidoProdutoRepository, never()).saveAllAndFlush(anyList());
    }

  }

  @Nested
  class IncrementarPedido {

    Long idPedido = 1L;
    Pedido pedido = new Pedido();
    List<PedidoProduto> pedidoProdutoList = new ArrayList<>();
    Produto produto = new Produto();
    PedidoProduto item = new PedidoProduto();
    ItensPedidoDTO itensPedidoDTO = new ItensPedidoDTO();

    @BeforeEach
    void before() {
      pedido.setId(idPedido);
      pedido.setPrecoTotal(10.0);
      pedido.setItens(pedidoProdutoList);

      produto.setId(1L);
      produto.setPreco(2.5);

      item.setProduto(produto);
      item.setQuantidade(3);

      itensPedidoDTO.setItens(Collections.singletonList(item));
    }

    @Test
    public void shouldIncrementOrder_givenValidIten() {
      when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedido));

      when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));

      when(pedidoProdutoRepository.findByPedidoAndProduto(pedido, produto)).thenReturn(null);

      Pedido pedidoAtualizado = pedidoService.incrementarPedido(idPedido, itensPedidoDTO);

      assertEquals(17.5, pedidoAtualizado.getPrecoTotal());

      verify(pedidoRepository, times(1)).saveAndFlush(pedido);
      verify(pedidoProdutoRepository, times(1)).saveAllAndFlush(anyList());
    }

    @Test
    public void shouldThrowOrderException_givenNonexistentOrder() {
      when(pedidoRepository.findById(idPedido)).thenReturn(Optional.empty());
      OrderException thrown = assertThrows(OrderException.class, () ->
          pedidoService.incrementarPedido(idPedido, itensPedidoDTO));

      assertEquals("O pedido 1 não existe!", thrown.getMessage());
      verify(pedidoRepository, never()).saveAndFlush(any(Pedido.class));
      verify(pedidoProdutoRepository, never()).saveAllAndFlush(anyList());
    }

    @Test
    public void shouldThrowOrderException_givenClosedOrder() {
      pedido.setFechado(true);
      when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedido));
      OrderException thrown = assertThrows(OrderException.class, () ->
          pedidoService.incrementarPedido(idPedido, itensPedidoDTO));

      assertEquals("O pedido 1 já encontra-se fechado!", thrown.getMessage());
      verify(pedidoRepository, never()).saveAndFlush(any(Pedido.class));
      verify(pedidoProdutoRepository, never()).saveAllAndFlush(anyList());
    }

    @Test
    public void shouldThrowProductException_givenNonexistentProduct() {
      when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedido));
      when(produtoRepository.findById(anyLong())).thenReturn(Optional.empty());

      ProductException thrown = assertThrows(ProductException.class, () ->
          pedidoService.incrementarPedido(idPedido, itensPedidoDTO));

      assertEquals("O produto 1 não existe!", thrown.getMessage());
      verify(pedidoRepository, never()).saveAndFlush(any(Pedido.class));
      verify(pedidoProdutoRepository, never()).saveAllAndFlush(anyList());
    }

  }

  @Nested
  class decrementarPedido {

    Long idPedido = 1L;
    Long idProduto = 1L;
    Pedido pedido = new Pedido();
    List<PedidoProduto> pedidoProdutoList = new ArrayList<>();
    Produto produto = new Produto();
    PedidoProduto item = new PedidoProduto();
    ItensPedidoDTO itensPedidoDTO = new ItensPedidoDTO();
    PedidoProduto pedidoProduto;

    @BeforeEach
    void before() {
      pedido.setId(idPedido);
      pedido.setPrecoTotal(10.0);
      pedido.setItens(pedidoProdutoList);

      produto.setId(idProduto);
      produto.setPreco(2.5);

      item.setProduto(produto);
      item.setQuantidade(3);

      itensPedidoDTO.setItens(Collections.singletonList(item));
      pedidoProduto = new PedidoProduto(pedido, produto, 3);
    }

    @Test
    public void shouldDecrementOrder_givenValidIten() {
      when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedido));
      when(produtoRepository.findById(idProduto)).thenReturn(Optional.of(produto));
      when(pedidoProdutoRepository.findByPedidoAndProduto(pedido, produto)).thenReturn(pedidoProduto);
      when(pedidoProdutoRepository.saveAll(pedidoProdutoList)).thenReturn(pedidoProdutoList);

      Pedido result = pedidoService.decrementarPedido(idPedido, itensPedidoDTO);

      verify(pedidoProdutoRepository, times(1)).delete(pedidoProduto);
      verify(pedidoRepository, times(1)).saveAndFlush(pedido);
      verify(pedidoProdutoRepository, times(1)).saveAllAndFlush(pedidoProdutoList);

      assertEquals(2.5, result.getPrecoTotal());
    }

    @Test
    public void shouldThrowOrderException_givenNonexistentOrder() {
      when(pedidoRepository.findById(idPedido)).thenReturn(Optional.empty());
      OrderException thrown = assertThrows(OrderException.class, () ->
          pedidoService.decrementarPedido(idPedido, itensPedidoDTO));

      assertEquals("O pedido 1 não existe!", thrown.getMessage());

      verify(pedidoProdutoRepository, never()).delete(pedidoProduto);
      verify(pedidoRepository, never()).saveAndFlush(any(Pedido.class));
      verify(pedidoProdutoRepository, never()).saveAllAndFlush(anyList());
    }

    @Test
    public void shouldThrowOrderException_givenClosedOrder() {
      pedido.setFechado(true);
      when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedido));
      OrderException thrown = assertThrows(OrderException.class, () ->
          pedidoService.decrementarPedido(idPedido, itensPedidoDTO));

      assertEquals("O pedido 1 já encontra-se fechado!", thrown.getMessage());

      verify(pedidoProdutoRepository, never()).delete(pedidoProduto);
      verify(pedidoRepository, never()).saveAndFlush(any(Pedido.class));
      verify(pedidoProdutoRepository, never()).saveAllAndFlush(anyList());
    }

    @Test
    public void shouldThrowProductException_givenNonexistentProduct() {
      when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedido));
      when(produtoRepository.findById(anyLong())).thenReturn(Optional.empty());

      ProductException thrown = assertThrows(ProductException.class, () ->
          pedidoService.decrementarPedido(idPedido, itensPedidoDTO));

      assertEquals("O produto 1 não existe!", thrown.getMessage());

      verify(pedidoProdutoRepository, never()).delete(pedidoProduto);
      verify(pedidoRepository, never()).saveAndFlush(any(Pedido.class));
      verify(pedidoProdutoRepository, never()).saveAllAndFlush(anyList());
    }

    @Test
    public void shouldThrowProductException_givennonExistentPedidoProduto() {
      when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedido));
      when(produtoRepository.findById(idProduto)).thenReturn(Optional.of(produto));
      when(pedidoProdutoRepository.findByPedidoAndProduto(pedido, produto)).thenReturn(null);

      ProductException thrown = assertThrows(ProductException.class, () ->
          pedidoService.decrementarPedido(idPedido, itensPedidoDTO));

      assertEquals("O pedido informado não contém o produto 1", thrown.getMessage());

      verify(pedidoProdutoRepository, never()).delete(pedidoProduto);
      verify(pedidoRepository, never()).saveAndFlush(any(Pedido.class));
      verify(pedidoProdutoRepository, never()).saveAllAndFlush(anyList());
    }

  }

  @Nested
  class fecharPedido {

    Pedido pedido = new Pedido();
    Long idPedido = 1L;
    Double valorPagamento = 10.0;

    @BeforeEach
    void before() {
      pedido.setId(idPedido);
      pedido.setPrecoTotal(10.0);
    }

    @Test
    public void shouldCloseOrder_givenValidParameters() {
      when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedido));

      Pedido pedidoAtualizado = pedidoService.fecharPedido(idPedido, valorPagamento);

      assertTrue(pedidoAtualizado.estaFechado());
      assertNotNull(pedidoAtualizado.getDataPagamentoPedido());

      verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    public void shouldThrowOrderException_givenNonexistentOrder() {
      when(pedidoRepository.findById(idPedido)).thenReturn(Optional.empty());
      OrderException thrown = assertThrows(OrderException.class, () ->
          pedidoService.fecharPedido(idPedido, valorPagamento));

      assertEquals("O pedido 1 não existe!", thrown.getMessage());

      verify(pedidoRepository, never()).save(pedido);
    }

    @Test
    public void shouldThrowOrderException_givenClosedOrder() {
      pedido.setFechado(true);
      when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedido));
      OrderException thrown = assertThrows(OrderException.class, () ->
          pedidoService.fecharPedido(idPedido, valorPagamento));

      assertEquals("O pedido 1 já encontra-se fechado!", thrown.getMessage());

      verify(pedidoRepository, never()).save(pedido);
    }

    @Test
    public void shouldThrowOrderException_givenInsufficientPaymentValue() {
      when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedido));
      valorPagamento = 9.9;

      OrderException thrown = assertThrows(OrderException.class, () ->
          pedidoService.fecharPedido(idPedido, valorPagamento));

      assertEquals("Valor de pagamento insuficiente!", thrown.getMessage());

      verify(pedidoRepository, never()).save(pedido);
    }

  }

  @Nested
  class listarTodos {

    @Test
    public void testListarTodos() {
      List<Pedido> pedidos = new ArrayList<>();
      pedidos.add(new Pedido());
      pedidos.add(new Pedido());
      pedidos.add(new Pedido());

      when(pedidoRepository.findAll()).thenReturn(pedidos);

      List<Pedido> pedidosRetornados = pedidoService.listarTodos();

      assertNotNull(pedidosRetornados);
      assertEquals(pedidos.size(), pedidosRetornados.size());
    }

  }

  @Nested
  class listarPorId {

    @Test
    public void shouldList_givenValidId() {
      Pedido pedido = new Pedido();
      when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

      Pedido resultado = pedidoService.listarPorId(1L);

      assertEquals(pedido, resultado);
    }

    @Test
    public void shouldThrowOrderException_givenInvalidId() {
      when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

      OrderException thrown = assertThrows(OrderException.class, () ->
          pedidoService.listarPorId(1L));

      assertEquals("O pedido 1 não existe!", thrown.getMessage());
    }

  }

}
