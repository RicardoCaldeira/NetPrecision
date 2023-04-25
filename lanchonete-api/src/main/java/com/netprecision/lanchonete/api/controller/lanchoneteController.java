package com.netprecision.lanchonete.api.controller;

import com.netprecision.lanchonete.api.model.Pedido;
import com.netprecision.lanchonete.api.model.dto.PedidoDTO;
import com.netprecision.lanchonete.api.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lanchonete")
public class lanchoneteController {

  @Autowired
  private PedidoService pedidoService;

  @PostMapping("/cadastrarPedido")
  public ResponseEntity<Pedido> cadastrarPedido(@RequestBody PedidoDTO pedidoDTO) {

    Pedido novoPedido = pedidoService.cadastrar(pedidoDTO);

    return new ResponseEntity<>(novoPedido, HttpStatus.CREATED);
  }

}
