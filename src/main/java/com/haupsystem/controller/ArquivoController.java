package com.haupsystem.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.haupsystem.model.Arquivo;
import com.haupsystem.model.ArquivoDto;
import com.haupsystem.repository.RepositorioArquivo;
import com.haupsystem.service.ArquivoService;
import com.haupsystem.service.SupabaseStorageService;

@RestController
@RequestMapping("/arquivo")
public class ArquivoController {
	
	@Autowired
	private ArquivoService arquivoService;
	
	@Autowired
	private RepositorioArquivo repositorioArquivo;
	
	@Autowired
	private SupabaseStorageService storageService;
	
	@PostMapping("/incluir")
	public ResponseEntity<ArquivoDto> incluir(@RequestParam("arquivo") MultipartFile file) {
		Arquivo entity = arquivoService.incluir(file);
		ArquivoDto dto = arquivoService.convertToDto(entity);
		return ResponseEntity.status(HttpStatus.CREATED).body(dto);
	}

	@GetMapping("/carregar/{id}")
	public ArquivoDto carregar(@PathVariable Long id) {
		Arquivo entity = repositorioArquivo.findById(id).get();
		ArquivoDto dto = arquivoService.convertToDto(entity);
		return dto;
	}

	@DeleteMapping("/deletar/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		Arquivo arquivo = repositorioArquivo.findById(id).get();
		arquivoService.deletar(arquivo);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/download/{id}")
	public ResponseEntity<FileSystemResource> downloadArquivo(@PathVariable Long id) throws UnsupportedEncodingException {
		boolean attachment = true;
		boolean validarImagem = false;
		return montarResponseParaDownloadOuExibicao(id, attachment, validarImagem);
	}

	@GetMapping("/imagem/{id}")
	public ResponseEntity<FileSystemResource> exibirImagem(@PathVariable Long id) throws UnsupportedEncodingException {
		boolean attachment = false;
		boolean validarImagem = true;
		return montarResponseParaDownloadOuExibicao(id, attachment, validarImagem);
	}
	
	private ResponseEntity<FileSystemResource> montarResponseParaDownloadOuExibicao(Long id, boolean attachment, boolean validarImagem) throws UnsupportedEncodingException {
		
		if(id == null) {
			throw new IllegalArgumentException("ID do arquivo obrigatorio");
		}

		Arquivo arquivo = repositorioArquivo.findById(id).get();
		
		Path caminhoArquivo = Paths.get(arquivo.getDiretorio());

		if (!Files.exists(caminhoArquivo)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "O arquivo não foi encontrado no servidor");
		}

		FileSystemResource recurso = new FileSystemResource(caminhoArquivo.toFile());
		
	    BodyBuilder bb = ResponseEntity
	    		.ok()
	    		.contentType(arquivoService.obterMediaTypeArquivo(caminhoArquivo));
	    
	    if(attachment) {
	    	String fileName = URLEncoder.encode(arquivo.getNomeOriginal(), StandardCharsets.UTF_8.toString());
	    	bb.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
	    } else {
	    	bb.header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable");
	    }

		return bb.body(recurso);
		
	}
	
	@PostMapping("/uploadSupabase")
    public ResponseEntity<String> uploadSupabase(@RequestParam("file") MultipartFile file) {
        try {
            String url = storageService.uploadFile(file.getOriginalFilename(), file.getBytes());
            return ResponseEntity.ok("Arquivo salvo em: " + url);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @GetMapping("/downloadSupabase/{filename}")
    public ResponseEntity<byte[]> downloadSupabase(@PathVariable String filename) {
        try {
            byte[] bytes = storageService.downloadFile(filename);
            return ResponseEntity.ok(bytes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
	
}
