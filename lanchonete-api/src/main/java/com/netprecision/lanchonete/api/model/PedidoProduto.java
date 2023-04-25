package com.netprecision.lanchonete.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class PedidoProduto {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "pedido")
  @JsonIgnore
  private Pedido pedido;

  @ManyToOne()
  @JoinColumn(name = "produto")
  private Produto produto;

  private Integer quantidade;

  public PedidoProduto(Pedido pedido, Produto produto, Integer quantidade) {
    this.pedido = pedido;
    this.produto = produto;
    this.quantidade = quantidade;
  }

}