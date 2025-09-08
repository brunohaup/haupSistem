package com.haupsystem.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.haupsystem.model.Compra;
import com.haupsystem.model.CompraDto;
import com.haupsystem.model.CompraItemArquivoDto;
import com.haupsystem.model.CompraPageDto;
import com.haupsystem.service.CompraService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/compra")
@RequiredArgsConstructor
public class CompraController {
	
	@Autowired
    private final CompraService compraService;

    // GET - Lista apenas as compras do usuário logado
    @GetMapping
    public ResponseEntity<List<Compra>> listarComprasUsuarioLogado() {
        return ResponseEntity.ok(compraService.listarComprasUsuarioLogado());
    }

    // POST - Cadastra uma nova compra
    @PostMapping("/criarCompra")
    public ResponseEntity<Void> criarCompra(@Valid @RequestBody CompraDto compra) {
        compraService.criarCompra(compra);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/carregar/{id}")
    public ResponseEntity<CompraDto> carregarPorId(@PathVariable Long id) {
    	CompraDto obj = this.compraService.findById(id);
        return ResponseEntity.ok().body(obj);
    }
    
    @PostMapping("/avancarEtapa")
    public ResponseEntity<CompraDto> avancarEtapa(@Valid @RequestBody CompraDto compra) {
    	CompraDto obj = this.compraService.avancarEtapa(compra);
        return ResponseEntity.ok().body(obj);
    }
    
    @PostMapping("/salvar")
    public ResponseEntity<CompraDto> salvar(@Valid @RequestBody CompraDto compra) {
    	CompraDto obj = this.compraService.salvar(compra);
        return ResponseEntity.ok().body(obj);
    }
    
    @GetMapping("/paginado")
    public ResponseEntity<CompraPageDto> listarComprasPaginadas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "dataHoraInclusao,desc") String sort) {
        
        String sortBy = "dataHoraInclusao";
        String sortDir = "desc";
        
        if (sort != null && sort.contains(",")) {
            String[] sortParams = sort.split(",");
            sortBy = sortParams[0];
            if (sortParams.length > 1) {
                sortDir = sortParams[1];
            }
        }
        
        CompraPageDto resultado = compraService.listarComprasPaginadas(page, size, search, sortBy, sortDir);
        return ResponseEntity.ok(resultado);
    }
    
    @PostMapping(value = "/incluirNota", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CompraItemArquivoDto> incluirNota(@RequestParam("file") MultipartFile file, @RequestParam("idItem") Long idItem) {
    	CompraItemArquivoDto obj = compraService.incluirNotaVinculandoComItem(file, idItem);
        return ResponseEntity.ok().body(obj);
    }
    
    @GetMapping("/listarNotas/{id}")
    public ResponseEntity<List<CompraItemArquivoDto>> listarNotasPorItem(@PathVariable Long id) {
    	List<CompraItemArquivoDto> obj = this.compraService.listarNotasDoItem(id);
        return ResponseEntity.ok().body(obj);
    }
    
    @GetMapping("/removerNota/{id}")
    public ResponseEntity<Void> removerNota(@PathVariable Long id) {
    	this.compraService.removerNota(id);
        return ResponseEntity.noContent().build();
    }
    
}
