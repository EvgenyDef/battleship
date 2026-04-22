package by.cats.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponseDto {
    private boolean success;
    private String message;
    private Object data; // Здесь может быть никнейм или объект игрока
}
