package by.cats.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ShipPlacemant {
    @JsonProperty("x")
    private Integer x;

    @JsonProperty("y")
    private Integer y;

    @JsonProperty("type")
    private String type;

    @JsonProperty("horizontal")
    private Boolean horizontal;
}
