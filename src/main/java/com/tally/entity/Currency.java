package com.tally.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "currency")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "currency_id", nullable = false)
    private Long currency_id;

    @JsonProperty("userId")
    @Column(name = "userid", nullable = false)
    private Long userId;

    @JsonProperty("cmpId")
    @Column(name = "cmpid", nullable = false)
    private Long cmpId;

    private String guid;

    @JsonProperty("masterId")
    @Column(name = "masterid")
    private Long masterId;

    @JsonProperty("alterId")
    @Column(name = "alterid")
    private Long alterId;

    private String name;
    private String symbol;

    @JsonProperty("formalName")
    @Column(name = "formalname")
    private String formalName;

    @JsonProperty("decimalPlaces")
    @Column(name = "decimalplaces")
    private Integer decimalPlaces;

    @JsonProperty("decimalSeparator")
    @Column(name = "decimalseparator")
    private String decimalSeparator;

    @JsonProperty("showAmountInWords")
    @Column(name = "showamountinwords")
    private String showAmountInWords;

    @JsonProperty("suffixSymbol")
    @Column(name = "suffixsymbol")
    private String suffixSymbol;

    @JsonProperty("spaceBetweenAmountAndSymbol")
    @Column(name = "spacebetweenamountandsymbol")
    private String spaceBetweenAmountAndSymbol;

    @JsonProperty("languageId")
    @Column(name = "languageid")
    private Integer languageId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
