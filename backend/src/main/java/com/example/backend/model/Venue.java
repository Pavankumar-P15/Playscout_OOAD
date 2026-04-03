package com.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "venues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "court_name", nullable = false)
    private String courtName;

    @Column(nullable = false)
    private String sport;

    @Column(name = "court_location")
    private String courtLocation;

    @Column(name = "courts_available")
    private Integer courtsAvailable;

    @Column
    private Integer price;

    @Column(name = "court_image")
    private String courtImage;

    @Column(name = "game_icon")
    private String gameIcon;

    @Column
    private BigDecimal rating;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "disabled", nullable = false)
    private Boolean disabled = false;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCourtName() { return courtName; }
    public void setCourtName(String courtName) { this.courtName = courtName; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getCourtLocation() { return courtLocation; }
    public void setCourtLocation(String courtLocation) { this.courtLocation = courtLocation; }

    public Integer getCourtsAvailable() { return courtsAvailable; }
    public void setCourtsAvailable(Integer courtsAvailable) { this.courtsAvailable = courtsAvailable; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public String getCourtImage() { return courtImage; }
    public void setCourtImage(String courtImage) { this.courtImage = courtImage; }

    public String getGameIcon() { return gameIcon; }
    public void setGameIcon(String gameIcon) { this.gameIcon = gameIcon; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }

    public Boolean getDisabled() { return disabled; }
    public void setDisabled(Boolean disabled) { this.disabled = disabled; }
}
