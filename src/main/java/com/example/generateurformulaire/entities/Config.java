package com.example.generateurformulaire.entities;

import jakarta.persistence.*;

@Entity
public class Config {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idConfig;
    private String label;
    private String placeholder;
    private boolean checked;
    private boolean disabled;
    private boolean required;
    private String maxLength;
    private String minLength;
    private String row;
    private String col;
    private boolean multiple;

    // Getters and setters
    @OneToOne
    @JoinColumn(name = "input_id")
    private Input input; // This should match the mappedBy value in Input

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
    }

    public String getMinLength() {
        return minLength;
    }

    public void setMinLength(String minLength) {
        this.minLength = minLength;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getCol() {
        return col;
    }

    public void setCol(String col) {
        this.col = col;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public Long getId() {
        return idConfig;
    }


    public void setIdConfig(Long idConfig) {
        this.idConfig = idConfig;
    }


    public String getRows() {
        return row;
    }

    public String getCols() {
        return col;
    }

}


