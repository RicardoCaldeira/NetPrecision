package com.netprecision.lanchonete.api.controller;

import com.netprecision.lanchonete.api.model.Pedido;
import com.netprecision.lanchonete.api.model.dto.ItensPedidoDTO;
import com.netprecision.lanchonete.api.model.dto.PagamentoDTO;
import com.netprecision.lanchonete.api.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lanchonete")
public class lanchoneteController {

  @Autowired
  private PedidoService pedidoService;

  @PostMapping("/cadastrarPedido")
  public ResponseEntity<String> cadastrarPedido(@RequestBody ItensPedidoDTO itensPedidoDTO) {
    pedidoService.cadastrar(itensPedidoDTO);
    return new ResponseEntity<>("Pedido cadastrado com sucesso!", HttpStatus.CREATED);
  }

  @PutMapping("/incrementarPedido/{idPedido}")
  public ResponseEntity<Pedido> incrementarPedido(@PathVariable Long idPedido, @RequestBody ItensPedidoDTO itensPedidoDTO) {
    return new ResponseEntity<>(pedidoService.incrementarPedido(idPedido, itensPedidoDTO), HttpStatus.OK);
  }

  @PutMapping("/decrementarPedido/{idPedido}")
  public ResponseEntity<Pedido> decrementarPedido(@PathVariable Long idPedido, @RequestBody ItensPedidoDTO itensPedidoDTO) {
    return new ResponseEntity<>(pedidoService.decrementarPedido(idPedido, itensPedidoDTO), HttpStatus.OK);
  }

  @PutMapping("/fecharPedido/{idPedido}")
  public ResponseEntity<Pedido> fecharPedido(@PathVariable Long idPedido, @RequestBody PagamentoDTO pagamentoDTO) {
    return new ResponseEntity<>(pedidoService.fecharPedido(idPedido, pagamentoDTO.getValorPagamento()), HttpStatus.OK);
  }

  @GetMapping("/listarPedidos")
  public ResponseEntity<List<Pedido>> listarPedidos() {
    List<Pedido> pedidos = pedidoService.listarTodos();
    return new ResponseEntity<>(pedidos, HttpStatus.OK);
  }

  @GetMapping("/listarPedido/{idPedido}")
  public ResponseEntity<Pedido> listarPedidoPorId(@PathVariable Long idPedido) {
    Pedido pedido = pedidoService.listarPorId(idPedido);
    return new ResponseEntity<>(pedido, HttpStatus.OK);
  }

}
