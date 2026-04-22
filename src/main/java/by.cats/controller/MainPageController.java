package by.cats.controller;


import by.cats.dto.response.ApiResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/main_page")
public class MainPageController {

    @GetMapping("/info")
    public ResponseEntity<ApiResponseDto> getInfo() {
        String info = "Тут нужно будет отправлять файлик с необходимой информацией";

        return ResponseEntity.ok(
                new ApiResponseDto(true, "Текст справки отправлен", info)
        );
    }

    @GetMapping("info/additional")
    public ResponseEntity<ApiResponseDto> getAdditionalInfo() {
        String additionalInfo = "Тут нужно будет отправлять файлик с необходимой  ДОПОЛНИТЕЛЬНОЙ информацией";

        return ResponseEntity.ok(
                new ApiResponseDto(true, "Текст с доп. информацией отправлен", additionalInfo)
        );
    }
}
