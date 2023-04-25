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

  private LocalDateTime dataPagamentoPedido;

  private Boolean fechado;

  private Double valorPagamento;

  private Double valorTroco;

  public Pedido() {
    this.dataCriacaoPedido = LocalDateTime.now();
    this.fechado = false;
  }

  public void fechar(Double valorPagamento) {
    this.valorPagamento = valorPagamento;
    this.valorTroco = valorPagamento - this.precoTotal;
    this.dataPagamentoPedido = LocalDateTime.now();
    this.fechado = true;
  }

  public Boolean estaFechado() {
    return this.fechado;
  }

}