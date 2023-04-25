package com.netprecision.lanchonete.api.model.dto;

import com.netprecision.lanchonete.api.model.PedidoProduto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItensPedidoDTO {

  private List<PedidoProduto> itens;

}
