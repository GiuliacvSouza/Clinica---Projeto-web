package controller;

import bll.CodigoPostalService;
import model.CodigoPostal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/codigos-postais")
public class CodigoPostalController {

    private final CodigoPostalService codigoPostalService;

    public CodigoPostalController(CodigoPostalService codigoPostalService) {
        this.codigoPostalService = codigoPostalService;
    }

    /**
     * GET /codigos-postais/{codigoPostal}
     * Devolve JSON com codigoPostal e localidade, ou 404 se não encontrado.
     */
    @GetMapping("/{codigoPostal}")
    public ResponseEntity<Map<String, String>> buscarPorCodigo(
            @PathVariable String codigoPostal) {

        try {
            CodigoPostal cp = codigoPostalService.buscarPorId(codigoPostal);
            return ResponseEntity.ok(Map.of(
                    "codigoPostal", cp.getCodigoPostal(),
                    "localidade",   cp.getLocalidade()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
