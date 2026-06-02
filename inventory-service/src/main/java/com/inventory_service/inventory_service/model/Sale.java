package com.inventory_service.inventory_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "sales",
    indexes = {
        @Index(name = "idx_sales_product_period", columnList = "company_id, product_id, anio, mes"),
        @Index(name = "idx_sales_fecha",           columnList = "company_id, fecha_venta")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale {

    @Id
    @UuidGenerator
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    // Columna generada en MySQL — Hibernate no escribe, solo lee
    @Column(name = "precio_total", nullable = false, precision = 14, scale = 2,
            insertable = false, updatable = false)
    private BigDecimal precioTotal;

    @Column(name = "fecha_venta", nullable = false)
    private LocalDate fechaVenta;

    // Columnas generadas en MySQL — Hibernate no escribe, solo lee
    @Column(name = "mes", insertable = false, updatable = false)
    private Integer mes;

    @Column(name = "anio", insertable = false, updatable = false)
    private Integer anio;

    @Column(name = "notas", length = 255)
    private String notas;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        // Precalculamos en Java para no depender del GENERATED ALWAYS al leer
        // el mismo objeto en memoria justo después de persistir
        if (this.cantidad != null && this.precioUnitario != null) {
            this.precioTotal = this.precioUnitario.multiply(BigDecimal.valueOf(this.cantidad));
        }
        if (this.fechaVenta != null) {
            this.mes  = this.fechaVenta.getMonthValue();
            this.anio = this.fechaVenta.getYear();
        }
    }
}
