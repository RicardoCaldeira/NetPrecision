package com.netprecision.lanchonete.api.controller;

import com.netprecision.lanchonete.api.model.Pedido;
import com.netprecision.lanchonete.api.model.dto.ItensPedidoDTO;
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

  @PutMapping("/incrementarPedido/{id}")
  public ResponseEntity<Pedido> incrementarPedido(@PathVariable Long id, @RequestBody ItensPedidoDTO itensPedidoDTO) {
    return new ResponseEntity<>(pedidoService.incrementarPedido(id, itensPedidoDTO), HttpStatus.OK);
  }

  @GetMapping("/listarPedidos")
  public ResponseEntity<List<Pedido>> listarPedidos() {
    List<Pedido> pedidos = pedidoService.listarTodos();
    return new ResponseEntity<>(pedidos, HttpStatus.OK);
  }

  @GetMapping("/listarPedido/{id}")
  public ResponseEntity<Pedido> listarPedidoPorId(@PathVariable Long id) {
    Pedido pedido = pedidoService.listarPorId(id);
    return new ResponseEntity<>(pedido, HttpStatus.OK);
  }

}
