package com.example.backend.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminVenueResponse {
    private UUID id;
    private String courtName;
    private String sport;
    private String courtLocation;
    private UUID createdBy;
    private String managerName;
    private Boolean disabled;
    private Integer price;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCourtName() { return courtName; }
    public void setCourtName(String courtName) { this.courtName = courtName; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getCourtLocation() { return courtLocation; }
    public void setCourtLocation(String courtLocation) { this.courtLocation = courtLocation; }

    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public Boolean getDisabled() { return disabled; }
    public void setDisabled(Boolean disabled) { this.disabled = disabled; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
}
