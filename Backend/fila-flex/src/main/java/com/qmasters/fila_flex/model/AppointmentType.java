package com.qmasters.fila_flex.model;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "appointment_types")
public class AppointmentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private BigDecimal preco;

    @Column(nullable = false)
    private Integer tempoExecucao; // Em minutos

    @Column(nullable = false)
    private LocalDate dataEntrega;

    @Lob
    private String documentacaoNecessaria;

    public AppointmentType() {}

    public AppointmentType(Long id, String nome, String descricao, String categoria, BigDecimal preco,
                           Integer tempoExecucao, LocalDate dataEntrega, String documentacaoNecessaria) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.preco = preco;
        this.tempoExecucao = tempoExecucao;
        this.dataEntrega = dataEntrega;
        this.documentacaoNecessaria = documentacaoNecessaria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Integer getTempoExecucao() {
        return tempoExecucao;
    }

    public void setTempoExecucao(Integer tempoExecucao) {
        this.tempoExecucao = tempoExecucao;
    }

    public LocalDate getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDate dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public String getDocumentacaoNecessaria() {
        return documentacaoNecessaria;
    }

    public void setDocumentacaoNecessaria(String documentacaoNecessaria) {
        this.documentacaoNecessaria = documentacaoNecessaria;
    }
}

