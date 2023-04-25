package com.netprecision.lanchonete.api.repository;

import com.netprecision.lanchonete.api.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
