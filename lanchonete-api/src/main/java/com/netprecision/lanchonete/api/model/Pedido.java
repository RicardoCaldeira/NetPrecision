package com.netprecision.lanchonete.api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Pedido {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
  private List<PedidoProduto> itens;

  private Double precoTotal;

  private LocalDateTime dataCriacaoPedido;

  private Boolean fechado;

  public Pedido() {
    this.dataCriacaoPedido = LocalDateTime.now();
    this.fechado = false;
  }

}